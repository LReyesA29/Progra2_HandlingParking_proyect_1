package Logic;

import Persistence.HandlingPersistence;

import Model.RecordParking;
import Model.Vehicle;
import Model.VehicleRate;
import Model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class HandlingParking {

    private final HandlingPersistence persistence;
    private static final DateTimeFormatter DF_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HandlingParking() {
        this.persistence = HandlingPersistence.getInstance();
    }

    /*  USUARIOS */

    /** validar usuario persitencia usuario */
    public boolean hasUsers() {
        return persistence.hasUsers();
    }

    /** Crea un usuario  */
    public boolean createUser(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) return false;
        return persistence.addUser(new User(userName.trim(), password));
    }

    /** Login  */
    public boolean login(String user, String pass) {
        if (!persistence.hasUsers()) return false; 
        return persistence.findUser(user, pass) != null;
    }

    /* parqueo*/

    public boolean startParking(String plate, String typeVehicle, LocalDateTime entryTime) {
        if (plate == null || plate.trim().isEmpty() || typeVehicle == null || entryTime == null) return false;

        if (getAvailableSpaces() <= 0) return false; // cupos
        if (persistence.findOpenRecordByPlate(plate) != null) return false; // validar q esta

        VehicleRate rate = persistence.findRateByType(typeVehicle);
        if (rate == null) return false; // tipo sin tarifa

        // La entrada no puede ser un parkeo previo de esta placa
        if (entryOverlapsClosedRecord(plate, entryTime)) return false;

        RecordParking r = new RecordParking(
                plate.trim().toUpperCase(),
                typeVehicle.trim(),
                entryTime.format(DF_DATETIME),
                "",     // abierto
                0.0     // total se calcula al cerrar
        );
        return persistence.addRecord(r); 
    }

    public double finishParking(String plate, LocalDateTime departureTime) {
        RecordParking open = persistence.findOpenRecordByPlate(plate);
        if (open == null || departureTime == null) return -1;

        LocalDateTime ent = open.getEntryDateTime();
        if (ent == null || !departureTime.isAfter(ent)) return -1;

        // Validar que [entrada, salida) no se solape con  misma placa
        if (intervalOverlapsOtherClosedRecords(plate, ent, departureTime, open)) return -1;

        VehicleRate rate = persistence.findRateByType(open.getTypeVehicle());
        double price = (rate == null) ? 0.0 : rate.getPrice();

        long hours = open.getHoursParkedRoundedUpWith(departureTime);
        double total = Math.max(0.0, hours * price);

        open.setDepartureTime(departureTime.format(DF_DATETIME));
        open.setTotal(total);
        boolean ok = persistence.updateRecord(open); // dump  inmediato
        return ok ? total : -1;
    }

    /* Vehicles*/

    public boolean addVehicle(String plate, String type, String owner, String model, String color) {
        if (plate == null || plate.isBlank() || type == null || type.isBlank()) return false;
        if (persistence.findVehicleByPlate(plate) != null) return false;     // placa única
        VehicleRate rate = persistence.findRateByType(type);
        if (rate == null) return false;                                       

        Vehicle v = new Vehicle(plate.trim().toUpperCase(), type.trim(), owner, model, color, rate.getPrice());
        return persistence.addVehicle(v); // dump XML inmediato
    }

    public boolean updateVehicle(String plate, String type, String owner, String model, String color) {
        Vehicle current = persistence.findVehicleByPlate(plate);
        if (current == null) return false;

        String newType  = (type  == null || type.isBlank())  ? current.getTypeVehicle() : type.trim();
        String newOwner = (owner == null || owner.isBlank()) ? current.getOwner()       : owner;
        String newModel = (model == null || model.isBlank()) ? current.getModel()       : model;
        String newColor = (color == null || color.isBlank()) ? current.getColor()       : color;

        VehicleRate rate = persistence.findRateByType(newType);
        if (rate == null) return false;

        Vehicle updated = new Vehicle(current.getLicensePlate(), newType, newOwner, newModel, newColor, rate.getPrice());
        return persistence.updateVehicle(updated); 
    }

    public boolean deleteVehicle(String plate) {
        if (persistence.findOpenRecordByPlate(plate) != null) return false; // no eliminar si hay abierto
        return persistence.deleteVehicle(plate); 
    }

    /* reportes y cupos */

    public int getAvailableSpaces() {
        int max = config.Config.getSizeParking();
        int used = persistence.countOpenRecords();
        return Math.max(0, max - used);
    }

    public long vehiclesCountByDay(LocalDate day) {
        return persistence.getRecords().stream()
                .filter(r -> r.getEntryDateTime() != null
                          && r.getEntryDateTime().toLocalDate().equals(day))
                .count();
    }

    public double incomeByDay(LocalDate day) {
        return persistence.getRecords().stream()
                .filter(r -> r.getEntryDateTime() != null
                          && r.getEntryTime() != null
                          && r.getEntryDateTime().toLocalDate().equals(day))
                .mapToDouble(RecordParking::getTotal)
                .sum();
    }

    /*Getters*/
    public List<RecordParking> getRecords(){ 
        return persistence.getRecords(); 
    }
    public List<Vehicle> getVehicles(){ 
        return persistence.getVehicles(); 
    }
    public List<VehicleRate> getRates(){ 
        return persistence.getRates(); 
    }

    /* Validaciones de solape  */
    private boolean entryOverlapsClosedRecord(String plate, LocalDateTime entry) {
        final String up = plate.trim().toUpperCase();
        for (RecordParking r : persistence.getRecords()) {
            if (r.getLicensePlate() == null) continue;
            if (!up.equals(r.getLicensePlate().trim().toUpperCase())) continue;
            LocalDateTime e = r.getEntryDateTime();
            LocalDateTime d = r.getDepartureDateTime();
            if (e == null || d == null) continue; 
            boolean contains = (entry.isAfter(e) || entry.isEqual(e)) && entry.isBefore(d);
            if (contains) return true;
        }
        return false;
    }

    private boolean intervalOverlapsOtherClosedRecords(String plate, LocalDateTime ent, LocalDateTime dep, RecordParking self) {
        final String up = plate.trim().toUpperCase();
        for (RecordParking r : persistence.getRecords()) {
            if (r == self) continue; // ignora el propio abierto
            if (r.getLicensePlate() == null) continue;
            if (!up.equals(r.getLicensePlate().trim().toUpperCase())) continue;
            LocalDateTime e2 = r.getEntryDateTime();
            LocalDateTime d2 = r.getDepartureDateTime();
            if (e2 == null || d2 == null) continue; 
            boolean overlap = ent.isBefore(d2) && e2.isBefore(dep);
            if (overlap) return true;
        }
        return false;
    }
}
