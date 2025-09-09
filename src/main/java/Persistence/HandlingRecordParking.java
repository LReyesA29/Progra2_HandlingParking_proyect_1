package Persistence;
import Model.RecordParking;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import Enums.*;
import Interfaces.IActionsFile;
import constants.CommonConstants;

public class HandlingRecordParking extends FilePlain implements IActionsFile {
    private List<RecordParking> records;
    private static final String FILE_PATH = "src/main/resources/data/recordParking.json";

    public HandlingRecordParking() {
        this.records = new ArrayList<>();
    }

    @Override
    public void loadFile(ETypeFile eTypeFile) {
        if (eTypeFile.equals(ETypeFile.JSON)) {
            loadFileJSON();
        }
    }

    @Override
    public void dumpFile(ETypeFile eTypeFile) {
        if (eTypeFile.equals(ETypeFile.JSON)) {
            dumpFileJSON();
        }
    }

    
    private void loadFileJSON() {
        List<String> contentInLine = this.reader(
        config.getPathFiles().concat(config.getNameFileJson()))
        .stream()
        .filter(line -> !line.equals("[") && !line.equals("]") &&
        !line.equals(CommonConstants.BREAK_LINE) &&
        !line.trim().isEmpty() && !line.trim().isBlank())
        .collect(Collectors.toList());
        
        for (String line : contentInLine) {
            line = line.replace("{", "").replace("},", "").replace("}", "");
            StringTokenizer tokens = new StringTokenizer(line, ",");
            while (tokens.hasMoreElements()) {
                String licensePlate = this.escapeValue(tokens.nextToken().split(":")[1]);
                String typeVehicle = this.escapeValue(tokens.nextToken().split(":")[1]);
                String entryTime = this.escapeValue(tokens.nextToken().split(":")[1]);
                String departureTime = this.escapeValue(tokens.nextToken().split(":")[1]);
                double total = Double.parseDouble(this.escapeValue(tokens.nextToken().split(":")[1]));
                this.records.add(new RecordParking(licensePlate, typeVehicle, entryTime, departureTime, total));
            }
        }
    }
    

    private void dumpFileJSON() {
        List<String> content = new ArrayList<>();
        content.add("[");
        int cont = 0;
        int total = records.size();
        for (RecordParking r : records) {
            StringBuilder json = new StringBuilder();
            json.append("{");
            
            json.append("  \"licensePlate\":\"").append(escape(r.getLicensePlate())).append("\",");
            json.append("  \"typeVehicle\":\"").append(escape(r.getTypeVehicle())).append("\",");
            json.append("  \"entryTime\":\"").append(escape(r.getEntryTime())).append("\",");
            json.append("  \"departureTime\":\"").append(escape(r.getDepartureTime())).append("\",");
            json.append("  \"total\":\"").append(r.getTotal());
            json.append("}");
            cont++;
            if (cont < total) {
                json.append(",");
            }
            content.add(json.toString());
        }
        content.add("]");
        this.writer(FILE_PATH, content);
    }

    public boolean addRecord(RecordParking r) {
        if (findByPlate(r.getLicensePlate()) == null) {
            records.add(r);
            return true;
        }
        return false;
    }

    public RecordParking findByPlate(String plate) {
        return records.stream().filter(r -> r.getLicensePlate().equalsIgnoreCase(plate)).findFirst().orElse(null);
    }

    public boolean updateRecord(RecordParking updated) {
        RecordParking r = findByPlate(updated.getLicensePlate());
        if (r != null) {
            r.setDepartureTime(updated.getDepartureTime());
            r.setTotal(updated.getTotal());
            return true;
        }
        return false;
    }

    public boolean deleteRecord(String plate) {
        return records.removeIf(r -> r.getLicensePlate().equalsIgnoreCase(plate));
    }

    public List<RecordParking> getRecords() {
        return records;
    }

    private String escapeValue(String value) {
		return value.replace("\"", "");
	}
        private String escape(String value) {
	    if (value == null) return "";
	    return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}