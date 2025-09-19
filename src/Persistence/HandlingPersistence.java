package Persistence;

import Interfaces.IActionsFile;
import Enums.ETypeFile;

import Model.User;
import Model.Vehicle;
import Model.VehicleRate;
import Model.RecordParking;

import org.w3c.dom.*;
import javax.xml.parsers.*;

import config.Config;
import constants.CommonConstants;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Persistencia estilo profesor:
 * - VehicleRate: CSV (solo lectura)
 * - User: SER (CRUD básico)
 * - Vehicle: XML (CRUD)
 * - RecordParking: JSON (CRUD)
 */
public class HandlingPersistence extends FilePlain implements IActionsFile {

    private static HandlingPersistence INSTANCE;

    private final List<VehicleRate> rates   = new ArrayList<>();
    private final List<User>        users   = new ArrayList<>();
    private final List<Vehicle>     vehicles= new ArrayList<>();
    private final List<RecordParking> records = new ArrayList<>();

    private HandlingPersistence() { super(); }

    public static HandlingPersistence getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HandlingPersistence();
            INSTANCE.loadAll();
        }
        return INSTANCE;
    }

    /* getters */
    public List<VehicleRate> getRates(){ 
        return rates; }
    public List<User> getUsers(){ 
        return users; }
    public List<Vehicle> getVehicles(){ 
        return vehicles; }
    public List<RecordParking> getRecords(){ 
        return records; }

    /* load inicial*/
    public void loadAll() {
        try { loadFile(ETypeFile.SER);  
        } catch (Exception ignored) {

        }
        try { loadFile(ETypeFile.CSV);  
        } catch (Exception ignored) {

        }
        try { loadFile(ETypeFile.XML);  
        } catch (Exception ignored) {

        }
        try { loadFile(ETypeFile.JSON);
         } catch (Exception ignored) {

         }
    }

    /* IActionsFile */
    @Override public void loadFile(ETypeFile t) {
        switch (t) {
            case CSV:  
                loadFileCSV();  
                break;
            case JSON: 
                loadFileJSON(); 
                break;
            case XML:  
                loadFileXML();  
                break;
            case SER:  
                loadFileSerializate(); 
                break;
            case FILE_PLAIN: 
                default: 
                break;
        }
    }

    @Override public void dumpFile(ETypeFile t) {
        switch (t) {
            case CSV:  
            dumpFileCSV();  
                break; 
            case JSON: 
                dumpFileJSON(); 
                break;
            case XML:  
                dumpFileXML();  
                break;
            case SER:  
                dumpFileSerializate(); 
                break;
            case FILE_PLAIN: 
                default: 
                break;
        }
    }

    /* usuarios serializacion*/
    @SuppressWarnings("unchecked")
    private void loadFileSerializate() {
        users.clear();
        String path = Config.getPathFiles().concat(Config.getNameFileSer());
        try (FileInputStream fin = new FileInputStream(path);
             ObjectInputStream oin = new ObjectInputStream(fin)) {
            List<User> loaded = (List<User>) oin.readObject();
            if (loaded != null) users.addAll(loaded);
        } catch (FileNotFoundException e) {
            // no hay usuario
        } catch (Exception ignore) {}
    }
    private void dumpFileSerializate() {
        String path = Config.getPathFiles().concat(Config.getNameFileSer());
        try (FileOutputStream fout = new FileOutputStream(path);
             ObjectOutputStream oout = new ObjectOutputStream(fout)) {
            oout.writeObject(users);
        } catch (IOException ignore) {}
    }

    public boolean hasUsers() {
        return users != null && !users.isEmpty();
    }

    public boolean userNameTaken(String userName) {
        if (userName == null) return false;
        final String u = userName.trim();
        for (User x : users) {
            if (x.getUserName() != null && x.getUserName().trim().equals(u)) return true;
        }
        return false;
    }

    public boolean addUser(User u) {
        if (u == null || u.getUserName() == null || u.getUserName().trim().isEmpty()
                || u.getPassword() == null || u.getPassword().trim().isEmpty()) {
            return false;
        }
        if (userNameTaken(u.getUserName())) return false;
        users.add(u);
        dumpFile(ETypeFile.SER);
        return true;
    }

    public boolean deleteUser(String userName) {
        if (userName == null) return false;
        boolean removed = users.removeIf(u -> userName.equals(u.getUserName()));
        if (removed) dumpFile(ETypeFile.SER);
        return removed;
    }

    public User findUser(String userName, String password) {
        if (userName == null || password == null) return null;
        for (User u : users) {
            if (userName.equals(u.getUserName()) && password.equals(u.getPassword())) return u;
        }
        return null;
    }

    /* vehicleRate CSV*/
    private void loadFileCSV() {
        rates.clear();
        String path = Config.getPathFiles().concat(Config.getNameFileCSV());
        List<String> lines = reader(path);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            String sep = line.contains(CommonConstants.SEMICOLON) ? CommonConstants.SEMICOLON : ",";
            String[] parts = line.split(sep);
            if (parts.length >= 2) {
                String type = parts[0].trim();
                try {
                    double price = Double.parseDouble(parts[1].trim());
                    rates.add(new VehicleRate(type, price));
                } catch (NumberFormatException ignore) {}
            }
        }
    }
    private void dumpFileCSV() {
         /* no se necesita aca */ 
        }

    public VehicleRate findRateByType(String type) {
        if (type == null) return null;
        final String t = type.trim().toUpperCase();
        for (VehicleRate r : rates) {
            if (r.getTypeVehicle().trim().toUpperCase().equals(t)) return r;
        }
        return null;
    }

    /* vehiculos XMl*/
    private void loadFileXML() {
        vehicles.clear();
        String path = Config.getPathFiles().concat(Config.getNameFileXML());
        File file = new File(path);
        if (!file.exists()) return;
        try {
            DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = bf.newDocumentBuilder();
            Document doc = b.parse(file);
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("vehicle");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                String plate = getTag(el, "licensePlate");
                String type  = getTag(el, "typeVehicle");
                String owner = getTag(el, "owner");
                String model = getTag(el, "model");
                String color = getTag(el, "color");
                String pStr  = getTag(el, "priceHour");
                double p = 0; try { p = Double.parseDouble(pStr); } catch (Exception ignore) {}
                vehicles.add(new Vehicle(plate, type, owner, model, color, p));
            }
        } catch (Exception ignore) {}
    }
    private String getTag(Element el, String name) {
        Node n = el.getElementsByTagName(name).item(0);
        return (n == null) ? "" : n.getTextContent();
    }
    private void dumpFileXML() {
        String path = Config.getPathFiles().concat(Config.getNameFileXML());
        List<String> lines = new ArrayList<>();
        lines.add("<vehicles>");
        for (Vehicle v : vehicles) {
            lines.add("  <vehicle>");
            lines.add("    <licensePlate>" + safe(v.getLicensePlate()) + "</licensePlate>");
            lines.add("    <typeVehicle>"  + safe(v.getTypeVehicle())  + "</typeVehicle>");
            lines.add("    <owner>"        + safe(v.getOwner())        + "</owner>");
            lines.add("    <model>"        + safe(v.getModel())        + "</model>");
            lines.add("    <color>"        + safe(v.getColor())        + "</color>");
            lines.add("    <priceHour>"    + v.getPriceHour()          + "</priceHour>");
            lines.add("  </vehicle>");
        }
        lines.add("</vehicles>");
        writer(path, lines);
    }
    private String safe(String s) { return s == null ? "" : s; }

    public Vehicle findVehicleByPlate(String plate) {
        if (plate == null) return null;
        final String p = plate.trim().toUpperCase();
        for (Vehicle v : vehicles) {
            if (p.equals(v.getLicensePlate().trim().toUpperCase())) return v;
        }
        return null;
    }
    public boolean addVehicle(Vehicle v) {
        if (v == null || v.getLicensePlate() == null || v.getLicensePlate().trim().isEmpty()) return false;
        if (findVehicleByPlate(v.getLicensePlate()) != null) return false;
        vehicles.add(v);
        dumpFile(ETypeFile.XML);
        return true;
    }
    public boolean updateVehicle(Vehicle v) {
        if (v == null || v.getLicensePlate() == null) return false;
        final String p = v.getLicensePlate().trim().toUpperCase();
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getLicensePlate().trim().toUpperCase().equals(p)) {
                vehicles.set(i, v);
                dumpFile(ETypeFile.XML);
                return true;
            }
        }
        return false;
    }
    public boolean deleteVehicle(String plate) {
        if (plate == null) return false;
        boolean removed = vehicles.removeIf(v -> v.getLicensePlate().equalsIgnoreCase(plate));
        if (removed) dumpFile(ETypeFile.XML);
        return removed;
    }

    /* records Json*/
    private void loadFileJSON() {
        records.clear();
        String path = Config.getPathFiles().concat(Config.getNameFileJson());
        List<String> contentInLine = reader(path).stream()
                .filter(line -> !line.equals(CommonConstants.OPENING_BRACKET)
                             && !line.equals(CommonConstants.CLOSING_BRACKET)
                             && !line.equals(CommonConstants.BREAK_LINE)
                             && !line.trim().isEmpty())
                .collect(Collectors.toList());
        for (String line : contentInLine) {
            String row = line.replace("{", "").replace("},", "").replace("}", "");
            StringTokenizer tokens = new StringTokenizer(row, ",");
            try {
                String licensePlate = unquote(tokens.nextToken().split(":", 2)[1]);
                String typeVehicle  = unquote(tokens.nextToken().split(":", 2)[1]);
                String entryTime    = unquote(tokens.nextToken().split(":", 2)[1]);
                String departureTime= unquote(tokens.nextToken().split(":", 2)[1]);
                String totalStr     = unquote(tokens.nextToken().split(":", 2)[1]);
                double total = 0; try { total = Double.parseDouble(totalStr); } catch (Exception ignore) {}
                records.add(new RecordParking(licensePlate, typeVehicle, entryTime, departureTime, total));
            } catch (Exception ignore) { /* línea inválida => se ignora */ }
        }
    }
    private void dumpFileJSON() {
        String path = Config.getPathFiles().concat(Config.getNameFileJson());
        List<String> out = new ArrayList<>();
        out.add(CommonConstants.OPENING_BRACKET);
        int n = records.size();
        int i = 0;
        for (RecordParking r : records) {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"licensePlate\":\"").append(escape(r.getLicensePlate())).append("\",");
            json.append("\"typeVehicle\":\"").append(escape(r.getTypeVehicle())).append("\",");
            json.append("\"entryTime\":\"").append(escape(r.getEntryTime())).append("\",");
            json.append("\"departureTime\":\"").append(escape(r.getDepartureTime())).append("\",");
            json.append("\"total\":").append(r.getTotal());
            json.append("}");
            if (++i < n) json.append(",");
            out.add(json.toString());
        }
        out.add(CommonConstants.CLOSING_BRACKET);
        writer(path, out);
    }
    private String unquote(String value){
         return value == null ? "" : value.replace("\"", "").trim(); 
        }
    private String escape(String value){ 
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\""); 
    }

    public boolean addRecord(RecordParking r) {
        if (r == null || r.getLicensePlate() == null) return false;
        if (findOpenRecordByPlate(r.getLicensePlate()) != null) return false; // ya hay uno abierto
        records.add(r);
        dumpFile(ETypeFile.JSON);
        return true;
    }
    public boolean updateRecord(RecordParking updated) {
        if (updated == null || updated.getLicensePlate() == null) return false;
        final String p = updated.getLicensePlate().trim().toUpperCase();
        for (int i = 0; i < records.size(); i++) {
            RecordParking r = records.get(i);
            boolean same = r.getLicensePlate() != null
                        && r.getLicensePlate().trim().toUpperCase().equals(p);
            boolean isOpen = r.getDepartureTime() == null || r.getDepartureTime().trim().isEmpty();
            if (same && isOpen) {
                r.setDepartureTime(updated.getDepartureTime());
                r.setTotal(updated.getTotal());
                dumpFile(ETypeFile.JSON);
                return true;
            }
        }
        return false;
    }
    public boolean deleteRecord(String plate) {
        if (plate == null) return false;
        boolean removed = records.removeIf(r -> r.getLicensePlate().equalsIgnoreCase(plate));
        if (removed) dumpFile(ETypeFile.JSON);
        return removed;
    }
    public RecordParking findOpenRecordByPlate(String plate) {
        if (plate == null) return null;
        final String p = plate.trim().toUpperCase();
        for (RecordParking r : records) {
            boolean same = r.getLicensePlate() != null
                        && r.getLicensePlate().trim().toUpperCase().equals(p);
            boolean isOpen = r.getDepartureTime() == null || r.getDepartureTime().trim().isEmpty();
            if (same && isOpen) return r;
        }
        return null;
    }
    public int countOpenRecords() {
        int cnt = 0;
        for (RecordParking r : records) {
            if (r.getDepartureTime() == null || r.getDepartureTime().trim().isEmpty()) cnt++;
        }
        return cnt;
    }
}
