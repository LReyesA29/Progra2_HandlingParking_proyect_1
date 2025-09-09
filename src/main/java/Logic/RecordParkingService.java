package Logic;
import Model.RecordParking;
import Model.VehicleRate;
import Persistence.HandlingRecordParking;
import Persistence.HandlingVehicleRate;
import Enums.ETypeFile;
import java.util.List;

public class RecordParkingService {
    private HandlingRecordParking handlingParking;
    private HandlingVehicleRate handlingRates;

    public RecordParkingService() {
        handlingParking = new HandlingRecordParking();
        handlingParking.loadFile(ETypeFile.JSON);
        handlingRates = new HandlingVehicleRate();
        handlingRates.loadFile(ETypeFile.CSV);
    }

    public double calculateTotal(RecordParking record) {
        VehicleRate rate = handlingRates.findRateByType(record.getTypeVehicle());
        if (rate != null) {
            long hours = record.getHoursParked();
            return hours * rate.getPrice();
        }
        return 0.0;
    }

    public void addRecord(RecordParking r) {
        double total = calculateTotal(r);
        r.setTotal(total);
        if (handlingParking.addRecord(r)) {
            handlingParking.dumpFile(ETypeFile.JSON);
        }
    }

    public List<RecordParking> getAllRecords() {
        return handlingParking.getRecords();
    }

    public long countVehiclesByDate(String date) {
        return handlingParking.getRecords().stream().filter(r -> r.getEntryTime().startsWith(date)).count();
    }

    public double totalIncomeByDate(String date) {
        return handlingParking.getRecords().stream().filter(r -> r.getEntryTime().startsWith(date)).mapToDouble(RecordParking::getTotal).sum();
    }
}
