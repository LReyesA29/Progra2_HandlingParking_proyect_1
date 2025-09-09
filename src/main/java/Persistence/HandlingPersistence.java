package Persistence;

import Model.User;
import Model.Vehicle;
import Model.VehicleRate;
import Model.RecordParking;

import Enums.ETypeFile;
import Interfaces.IActionsFile;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import constants.CommonConstants;
import config.Config;


public class HandlingPersistence extends FilePlain implements IActionsFile {

    private List<User> users = new ArrayList<>();
    private List<VehicleRate> rates = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<RecordParking> records = new ArrayList<>();

    private static HandlingPersistence INSTANCE;

    private HandlingPersistence() { super(); }

    public static HandlingPersistence getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HandlingPersistence();
            INSTANCE.loadAll();
        }
        return INSTANCE;
    }

    
    public static void resetInstance() {
        INSTANCE = null;
    }

    // getters
    public List<User> getUsers() { return users; }
    public List<VehicleRate> getRates() { return rates; }
    public List<Vehicle> getVehicles() { return vehicles; }
    public List<RecordParking> getRecords() { return records; }

    @Override
    public void loadFile(ETypeFile eTypeFile) {
        switch (eTypeFile) {
            case SER: loadFileSerializate(); break;
            case CSV: loadFileCSV(); break;
            case XML: loadFileXML(); break;
            case JSON: loadFileJSON(); break;
            default: System.out.println("Tipo de archivo no soportado"); break;
        }
    }

    @Override
    public void dumpFile(ETypeFile eTypeFile) {
        switch (eTypeFile) {
            case SER: dumpFileSerializate(); break;
            case CSV: dumpFileCSV(); break;
            case XML: dumpFileXML(); break;
            case JSON: dumpFileJSON(); break;
            default: System.out.println("Tipo de archivo no soportado"); break;
        }
    }


    public void loadAll() {
        loadFile(ETypeFile.SER);
        loadFile(ETypeFile.CSV);
        loadFile(ETypeFile.XML);
        loadFile(ETypeFile.JSON);
    }

  
    public void dumpAll() {
        dumpFile(ETypeFile.SER);
        dumpFile(ETypeFile.CSV);
        dumpFile(ETypeFile.XML);
        dumpFile(ETypeFile.JSON);
    }


    @SuppressWarnings("unchecked")
    private void loadFileSerializate() {
        users.clear();
        String path = Config.getPathFiles() + Config.getNameFileSer();
        File f = new File(path);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                users = (List<User>) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpFileSerializate() {
        String path = Config.getPathFiles() + Config.getNameFileSer();
        File f = new File(path);
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void loadFileCSV() {
        rates.clear();
        String path = Config.getPathFiles() + Config.getNameFileCSV();
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
                } catch (NumberFormatException ex) {
                    System.out.println("Skipping invalid rate line: " + line);
                }
            }
        }
    }

    private void dumpFileCSV() {
        String path = Config.getPathFiles() + Config.getNameFileCSV();
        List<String> lines = new ArrayList<>();
        for (VehicleRate r : rates) lines.add(r.getTypeVehicle() + CommonConstants.SEMICOLON + r.getPrice());
        writer(path, lines);
    }

    
    private void loadFileXML() {
        vehicles.clear();
        String path = Config.getPathFiles() + Config.getNameFileXML();
        File f = new File(path);
        if (!f.exists()) return;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("vehicle");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                String plate = el.getElementsByTagName("licensePlate").item(0).getTextContent();
                String type = el.getElementsByTagName("typeVehicle").item(0).getTextContent();
                String owner = el.getElementsByTagName("owner").item(0).getTextContent();
                String model = el.getElementsByTagName("model").item(0).getTextContent();
                String color = el.getElementsByTagName("color").item(0).getTextContent();
                double p = Double.parseDouble(el.getElementsByTagName("pricePerHour").item(0).getTextContent());
                vehicles.add(new Vehicle(plate, type, owner, model, color, p));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dumpFileXML() {
        String path = Config.getPathFiles() + Config.getNameFileXML();
        List<String> lines = new ArrayList<>();
        lines.add("<vehicles>");
        for (Vehicle v : vehicles) {
            lines.add("  <vehicle>");
            lines.add("    <licensePlate>" + v.getLicensePlate() + "</licensePlate>");
            lines.add("    <typeVehicle>" + v.getTypeVehicle() + "</typeVehicle>");
            lines.add("    <owner>" + v.getOwner() + "</owner>");
            lines.add("    <model>" + v.getModel() + "</model>");
            lines.add("    <color>" + v.getColor() + "</color>");
            lines.add("    <pricePerHour>" + v.getPriceHour() + "</pricePerHour>");
            lines.add("  </vehicle>");
        }
        lines.add("</vehicles>");
        writer(path, lines);
    }

    private void loadFileJSON() {
        records.clear();
        String path = Config.getPathFiles() + Config.getNameFileJson();
        List<String> contentInLine = reader(path).stream()
                .filter(line -> !line.equals(CommonConstants.OPENING_BRACKET) && !line.equals(CommonConstants.CLOSING_BRACKET) &&
                        !line.equals(CommonConstants.BREAK_LINE) && !line.trim().isEmpty() && !line.trim().isBlank())
                .collect(Collectors.toList());

        for (String line : contentInLine) {
            String row = line.replace("{", "").replace("},", "").replace("}", "");
            StringTokenizer tokens = new StringTokenizer(row, ",");
            try {
                while (tokens.hasMoreElements()) {
                    String licensePlate = escapeValue(tokens.nextToken().split(":",2)[1]);
                    String typeVehicle = escapeValue(tokens.nextToken().split(":",2)[1]);
                    String entryTime = escapeValue(tokens.nextToken().split(":",2)[1]);
                    String departureTime = escapeValue(tokens.nextToken().split(":",2)[1]);
                    double total = Double.parseDouble(escapeValue(tokens.nextToken().split(":",2)[1]));
                    records.add(new RecordParking(licensePlate, typeVehicle, entryTime, departureTime, total));
                }
            } catch (Exception ex) {
                System.out.println("Skipping invalid JSON line: " + line);
            }
        }
    }

    private void dumpFileJSON() {
        String path = Config.getPathFiles() + Config.getNameFileJson();
        List<String> content = new ArrayList<>();
        content.add(CommonConstants.OPENING_BRACKET);
        int counter = 0;
        int total = records.size();
        for (RecordParking r : records) {
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"licensePlate\":\"").append(escape(r.getLicensePlate())).append("\",");
            json.append("\"typeVehicle\":\"").append(escape(r.getTypeVehicle())).append("\",");
            json.append("\"entryTime\":\"").append(escape(r.getEntryTime())).append("\",");
            json.append("\"departureTime\":\"").append(escape(r.getDepartureTime())).append("\",");
            json.append("\"total\":").append(r.getTotal());
            json.append("}");
            counter++;
            if (counter < total) json.append(",");
            content.add(json.toString());
        }
        content.add(CommonConstants.CLOSING_BRACKET);
        writer(path, content);
    }

    public User findUser(String userName, String password) {
        return users.stream().filter(u -> u.getUserName().equals(userName) && u.getPassword().equals(password)).findFirst().orElse(null);
    }

    public VehicleRate findRateByType(String type) {
        return rates.stream().filter(r -> r.getTypeVehicle().equalsIgnoreCase(type)).findFirst().orElse(null);
    }

    public Vehicle findVehicleByPlate(String plate) {
        return vehicles.stream().filter(v -> v.getLicensePlate().equalsIgnoreCase(plate)).findFirst().orElse(null);
    }

    public RecordParking findRecordByPlate(String plate) {
        return records.stream().filter(r -> r.getLicensePlate().equalsIgnoreCase(plate)).findFirst().orElse(null);
    }

    public RecordParking findOpenRecordByPlate(String plate) {
        if (plate == null) return null;
        final String up = plate.trim().toUpperCase();
        for (RecordParking r : records) {
            String dep = r.getDepartureTime();
            if ((dep == null || dep.trim().isEmpty()) && r.getLicensePlate() != null && r.getLicensePlate().trim().toUpperCase().equals(up)) {
                return r;
            }
        }
        return null;
    }

    public int countOpenRecords() {
        int cnt = 0;
        for (RecordParking r : records) {
            String dep = r.getDepartureTime();
            if (dep == null || dep.trim().isEmpty()) cnt++;
        }
        return cnt;
    }

    public boolean addRecord(RecordParking r) {
        if (findOpenRecordByPlate(r.getLicensePlate()) == null) {
            records.add(r);
            return true;
        }
        return false;
    }

    public boolean updateRecord(RecordParking updated) {
        for (int i = 0; i < records.size(); i++) {
            RecordParking r = records.get(i);
            if (r.getLicensePlate().equalsIgnoreCase(updated.getLicensePlate()) && 
                (r.getDepartureTime() == null || r.getDepartureTime().trim().isEmpty())) {
                // actualizar registro 
                r.setDepartureTime(updated.getDepartureTime());
                r.setTotal(updated.getTotal());
                return true;
            }
        }
        return false;
    }

    public boolean deleteRecord(String plate) {
        return records.removeIf(r -> r.getLicensePlate().equalsIgnoreCase(plate));
    }

    private String escapeValue(String value) { 
        return value == null ? "" : value.replace("\"", "").trim(); 
    }
    private String escape(String value) {
        if (value == null){
            return "";
        } else{
            return value.replace("\\", "\\\\").replace("\"", "\\\""); 
        }     
    }
}
