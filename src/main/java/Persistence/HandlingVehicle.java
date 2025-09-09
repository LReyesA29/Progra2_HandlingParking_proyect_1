package Persistence;
import Model.Vehicle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import Enums.ETypeFile;
import Interfaces.IActionsFile;

public class HandlingVehicle extends FilePlain implements IActionsFile {
    private List<Vehicle> vehicles;
    private static final String FILE_PATH = "src/main/resources/data/dataVehicle.xml";

    public HandlingVehicle() {
        this.vehicles = new ArrayList<>();
    }

    @Override
    public void loadFile(ETypeFile eTypeFile) {
        if (eTypeFile.equals(ETypeFile.XML)) {
            loadFileXML();
        }
    }

    @Override
    public void dumpFile(ETypeFile eTypeFile) {
        if (eTypeFile.equals(ETypeFile.XML)) {
            dumpFileXML();
        }
    }

    private void loadFileXML() {
        vehicles.clear();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            NodeList list = doc.getElementsByTagName("vehicle");
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                String plate = e.getElementsByTagName("licensePlate").item(0).getTextContent();
                String type = e.getElementsByTagName("typeVehicle").item(0).getTextContent();
                String owner = e.getElementsByTagName("owner").item(0).getTextContent();
                String model = e.getElementsByTagName("model").item(0).getTextContent();
                String color = e.getElementsByTagName("color").item(0).getTextContent();
                double price = Double.parseDouble(e.getElementsByTagName("pricePerHour").item(0).getTextContent());
                vehicles.add(new Vehicle(plate, type, owner, model, color, price));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dumpFileXML() {
        List<String> records = new ArrayList<>();
        records.add("<vehicles>");
        for (Vehicle v : vehicles) {
            records.add("  <vehicle>");
            records.add("    <licensePlate>" + v.getLicensePlate() + "</licensePlate>");
            records.add("    <typeVehicle>" + v.getTypeVehicle() + "</typeVehicle>");
            records.add("    <owner>" + v.getOwner() + "</owner>");
            records.add("    <model>" + v.getModel() + "</model>");
            records.add("    <color>" + v.getColor() + "</color>");
            records.add("    <pricePerHour>" + v.getPricePerHour() + "</pricePerHour>");
            records.add("  </vehicle>");
        }
        records.add("</vehicles>");
        this.writer(FILE_PATH, records);
    }

    public boolean addVehicle(Vehicle v) {
        if (findByPlate(v.getLicensePlate()) == null) {
            vehicles.add(v);
            return true;
        }
        return false;
    }

    public Vehicle findByPlate(String plate) {
        return vehicles.stream().filter(v -> v.getLicensePlate().equalsIgnoreCase(plate)).findFirst().orElse(null);
    }

    public boolean updateVehicle(Vehicle updated) {
        Vehicle v = findByPlate(updated.getLicensePlate());
        if (v != null) {
            v.setOwner(updated.getOwner());
            v.setModel(updated.getModel());
            v.setColor(updated.getColor());
            v.setPricePerHour(updated.getPricePerHour());
            return true;
        }
        return false;
    }

    public boolean deleteVehicle(String plate) {
        return vehicles.removeIf(v -> v.getLicensePlate().equalsIgnoreCase(plate));
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}
