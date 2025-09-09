package Model;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RecordParking implements Serializable {
    private String licensePlate;
    private String typeVehicle;
    private String entryTime;
    private String departureTime;
    private double total;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public RecordParking(String licensePlate, String typeVehicle, String entryTime, String departureTime, double total) {
        this.licensePlate = licensePlate;
        this.typeVehicle = typeVehicle;
        this.entryTime = entryTime;
        this.departureTime = departureTime;
        this.total = total;
    }

    public String getLicensePlate() { return licensePlate; }
    public String getTypeVehicle() { return typeVehicle; }
    public String getEntryTime() { return entryTime; }
    public String getDepartureTime() { return departureTime; }
    public double getTotal() { return total; }

    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setTotal(double total) { this.total = total; }

    public long getHoursParked() {
        try {
            LocalDateTime entry = LocalDateTime.parse(entryTime, FORMATTER);
            LocalDateTime exit = LocalDateTime.parse(departureTime, FORMATTER);
            return Duration.between(entry, exit).toHours();
        } catch (Exception e) {
            return 0;
        }
    }
}
