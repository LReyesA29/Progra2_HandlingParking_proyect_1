package Logic;

import Model.RecordParking;
import Model.VehicleRate;
import Persistence.HandlingPersistence;
import Enums.ETypeFile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Business logic (reports, calculate totals) using HandlingPersistence unified handler.
 */
public class RecordParkingService {

    private HandlingPersistence handling;
    private static final DateTimeFormatter DF_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DF_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public RecordParkingService() {
        handling = HandlingPersistence.getInstance();
        // handling.loadAll() is performed by singleton creation
    }

    /** Calculates total using rates and rounded-up hours */
    public double calculateTotal(RecordParking record) {
        VehicleRate rate = handling.findRateByType(record.getTypeVehicle());
        if (rate != null) {
            long hours = record.getHoursParkedRoundedUp();
            return hours * rate.getPrice();
        }
        return 0.0;
    }

    public void addRecord(RecordParking r) {
        // calculate total then persist via handling
        double total = calculateTotal(r);
        r.setTotal(total);
        if (handling.addRecord(r)) {
            handling.dumpFile(ETypeFile.JSON);
        }
    }

    // --- new API ---
    public boolean startParking(String plate, String typeVehicle, LocalDateTime entryTime) {
        if (handling.findOpenRecordByPlate(plate) != null) {
            return false; // already parked
        }
        if (getAvailableSpaces() <= 0) return false;
        String entry = entryTime.format(DF_DATETIME);
        RecordParking r = new RecordParking(plate.toUpperCase(), typeVehicle, entry, "", 0.0);
        if (handling.addRecord(r)) {
            handling.dumpFile(ETypeFile.JSON);
            return true;
        }
        return false;
    }

    /**
     * Finish parking for a plate, set departure time and calculate total.
     * Returns total amount or -1 if not found/error.
     */
    public double finishParking(String plate, LocalDateTime departureTime) {
        RecordParking r = handling.findOpenRecordByPlate(plate);
        if (r == null) return -1;
        r.setDepartureTime(departureTime.format(DF_DATETIME));
        double total = calculateTotal(r);
        r.setTotal(total);
        if (handling.updateRecord(r)) {
            handling.dumpFile(ETypeFile.JSON);
            return total;
        }
        return -1;
    }

    public int getCurrentOccupancy() {
        return handling.countOpenRecords();
    }

    public int getAvailableSpaces() {
        int size = config.Config.getSizeParking();
        return Math.max(0, size - getCurrentOccupancy());
    }

    public List<RecordParking> getAllRecords() { return handling.getRecords(); }

    public long countVehiclesByDate(String date) {
        LocalDate target = LocalDate.parse(date, DF_DATE);
        return handling.getRecords().stream()
                .filter(r -> r.getEntryDateTime() != null && r.getEntryDateTime().toLocalDate().equals(target))
                .count();
    }

    public long countVehiclesByHour(String dateHour) {
        // dateHour expected "dd/MM/yyyy HH"
        String[] parts = dateHour.split(" ");
        if (parts.length < 2) return 0;
        LocalDate target = LocalDate.parse(parts[0], DF_DATE);
        int hour = Integer.parseInt(parts[1]);
        return handling.getRecords().stream()
                .filter(r -> r.getEntryDateTime() != null && r.getEntryDateTime().toLocalDate().equals(target) && r.getEntryDateTime().getHour() == hour)
                .count();
    }

    public double totalIncomeByDate(String date) {
        LocalDate target = LocalDate.parse(date, DF_DATE);
        return handling.getRecords().stream()
                .filter(r -> r.getEntryDateTime() != null && r.getEntryDateTime().toLocalDate().equals(target))
                .mapToDouble(RecordParking::getTotal).sum();
    }

    public double totalIncomeByHour(String dateHour) {
        String[] parts = dateHour.split(" ");
        if (parts.length < 2) return 0;
        LocalDate target = LocalDate.parse(parts[0], DF_DATE);
        int hour = Integer.parseInt(parts[1]);
        return handling.getRecords().stream()
                .filter(r -> r.getEntryDateTime() != null && r.getEntryDateTime().toLocalDate().equals(target) && r.getEntryDateTime().getHour() == hour)
                .mapToDouble(RecordParking::getTotal).sum();
    }
}
