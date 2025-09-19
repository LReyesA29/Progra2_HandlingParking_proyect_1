package Logic;

import Model.RecordParking;
import config.Config;


public class ParkingControl {

    private int maxCapacity;
    private RecordParkingService service;

    public ParkingControl() {
        this.maxCapacity = Config.getSizeParking();
        this.service = new RecordParkingService();
    }

    public boolean hasAvailableSpace() {
        return service.getAvailableSpaces() > 0;
    }

    public boolean parkVehicle(RecordParking record) {
        if (record == null) return false;
        return service.startParking(record.getLicensePlate(), record.getTypeVehicle(), record.getEntryDateTime());
    }

    public boolean parkVehicle(String plate, String typeVehicle, java.time.LocalDateTime entryTime) {
        return service.startParking(plate, typeVehicle, entryTime);
    }

    public boolean finishVehicle(String plate, java.time.LocalDateTime departureTime) {
        double total = service.finishParking(plate, departureTime);
        return total >= 0;
    }

    public int getAvailableSpaces() {
        return service.getAvailableSpaces();
    }
}
