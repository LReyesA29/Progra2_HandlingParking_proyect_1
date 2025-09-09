package Logic;
import Enums.ETypeFile;
import Model.RecordParking;
import Persistence.HandlingRecordParking;
import config.Config;

public class ParkingControl {
    private int maxCapacity;
    private HandlingRecordParking handling;

    public ParkingControl() {
        this.maxCapacity = Config.getSizeParking();
        this.handling = new HandlingRecordParking();
        this.handling.loadFile(ETypeFile.JSON);
    }

    public boolean hasAvailableSpace() {
        return handling.getRecords().size() < maxCapacity;
    }

    public boolean parkVehicle(RecordParking record) {
        if (hasAvailableSpace()) {
            handling.addRecord(record);
            handling.dumpFile(ETypeFile.JSON);
            return true;
        }
        return false;
    }

    public int getAvailableSpaces() {
        return maxCapacity - handling.getRecords().size();
    }
}
