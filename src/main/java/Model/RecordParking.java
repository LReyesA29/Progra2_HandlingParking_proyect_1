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

    public LocalDateTime getEntryDateTime() {
        try { return LocalDateTime.parse(entryTime, FORMATTER); } catch(Exception e) { return null; }
    }
    public LocalDateTime getDepartureDateTime() {
        try { return departureTime == null || departureTime.trim().isEmpty() ? null : LocalDateTime.parse(departureTime, FORMATTER); } catch(Exception e) { return null; }
    }

   
    public long getHoursParkedRoundedUp() {
        LocalDateTime e = getEntryDateTime();
        LocalDateTime d = getDepartureDateTime();
        if (e == null || d == null) return 0;
        long minutes = Duration.between(e, d).toMinutes();
        if (minutes <= 0) return 0;
        return (minutes + 59) / 60;
    }

    @Override public String toString() {
        return "Registro de Parqueadero :\n"+
         "placa: "+ licensePlate + "tipo vehiculo" + typeVehicle + "fecha entrada: " + entryTime + "fecha de salida:" +
          (departureTime==null ? "" : departureTime) + "total A pagar: " + total ;
    }
}
