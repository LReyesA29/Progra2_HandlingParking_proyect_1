package main;

import Logic.ParkingControl;
import Logic.RecordParkingService;
import Model.RecordParking;
import Model.User;
import Persistence.HandlingUser;

public class App {
    public static void main(String[] args) {
        HandlingUser handlingUser = new HandlingUser();
        User logged = handlingUser.findUser("admin", "1234");
        if (logged == null) {
            System.out.println("❌ Login fallido.");
            return;
        }
        System.out.println("✅ Login exitoso: " + logged.getUserName());

        ParkingControl parkingControl = new ParkingControl();
        if (!parkingControl.hasAvailableSpace()) {
            System.out.println("❌ No hay cupos disponibles.");
            return;
        }

        RecordParkingService service = new RecordParkingService();
        RecordParking record = new RecordParking("AAA111", "Carro", "08/09/2025 08:00", "08/09/2025 11:00", 0);
        parkingControl.parkVehicle(record);
        service.addRecord(record);

        String date = "08/09/2025";
        System.out.println("Vehículos en " + date + ": " + service.countVehiclesByDate(date));
        System.out.println("Dinero en " + date + ": $" + service.totalIncomeByDate(date));

        service.getAllRecords().forEach(System.out::println);
    }
}
