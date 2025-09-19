package Model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RecordParking implements Serializable {

    private String licensePlate;
    private String typeVehicle;
    private String entryTime;      // dd/MM/yyyy HH:mm
    private String departureTime;  // dd/MM/yyyy HH:mm (vacío si abierto)
    private double total;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public RecordParking() {}

    public RecordParking(String licensePlate, String typeVehicle, String entryTime, String departureTime, double total) {
        this.licensePlate = licensePlate;
        this.typeVehicle = typeVehicle;
        this.entryTime = entryTime;
        this.departureTime = departureTime;
        this.total = total;
    }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getTypeVehicle() { return typeVehicle; }
    public void setTypeVehicle(String typeVehicle) { this.typeVehicle = typeVehicle; }

    public String getEntryTime() { return entryTime; }
    public void setEntryTime(String entryTime) { this.entryTime = entryTime; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public LocalDateTime getEntryDateTime() {
        try {
            return (entryTime == null || entryTime.isBlank()) ? null : LocalDateTime.parse(entryTime, FORMATTER);
        } catch (DateTimeParseException e) { return null; }
    }
    public LocalDateTime getDepartureDateTime() {
        try {
            return (departureTime == null || departureTime.isBlank()) ? null : LocalDateTime.parse(departureTime, FORMATTER);
        } catch (DateTimeParseException e) { return null; }
    }

    /** Horas redondeadas hacia arriba usando la salida actual almacenada. */
    public long getHoursParkedRoundedUp() {
        LocalDateTime e = getEntryDateTime();
        LocalDateTime d = getDepartureDateTime();
        if (e == null || d == null) return 0L;
        long minutes = Duration.between(e, d).toMinutes();
        if (minutes <= 0) return 0L;
        return (minutes + 59) / 60;
    }

    /** Helper para calcular horas con una salida "candidata" sin setearla aún. */
    public long getHoursParkedRoundedUpWith(LocalDateTime customDeparture) {
        LocalDateTime e = getEntryDateTime();
        if (e == null || customDeparture == null) return 0L;
        long minutes = Duration.between(e, customDeparture).toMinutes();
        if (minutes <= 0) return 0L;
        return (minutes + 59) / 60;
    }

    @Override
    public String toString() {
        return "Registro de Parqueadero:\n" +
               "placa: " + licensePlate +
               " tipo vehiculo: " + typeVehicle +
               " fecha entrada: " + entryTime +
               " fecha salida: " + (departureTime == null ? "" : departureTime) +
               " total a pagar: " + total;
    }
}
