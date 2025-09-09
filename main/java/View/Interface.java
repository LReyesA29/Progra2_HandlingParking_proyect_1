package View;

import Model.RecordParking;
import Model.VehicleRate;
import Model.User;
import Persistence.HandlingPersistence;
import Logic.RecordParkingService;
import Logic.ParkingControl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Interface {

    private Scanner sc = new Scanner(System.in);
    private HandlingPersistence handling = HandlingPersistence.getInstance();
    private RecordParkingService service = new RecordParkingService();
    private ParkingControl control = new ParkingControl();
    private User currentUser = null;

    private static final DateTimeFormatter DF_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void showInfo() {
        int option;
        do {
            System.out.println("******** PARKING MENU ********");
            System.out.println("1. Login");
            System.out.println("2. Registrar Entrada");
            System.out.println("3. Registrar Salida");
            System.out.println("4. Mostrar cupos disponibles");
            System.out.println("5. Reportes");
            System.out.println("6. Listar registros");
            System.out.println("7. Mostrar tarifas");
            System.out.println("8. Guardar todo en archivos");
            System.out.println("9. Salir");
            System.out.println("*******************************");
            System.out.print("Seleccione una opción: ");
            option = GetInt();
            switch (option) {
                case 1:
                    doLogin();
                    break;
                case 2:
                    if (ensureLogged()) registerEntry();
                    break;
                case 3:
                    if (ensureLogged()) registerExit();
                    break;
                case 4:
                    showAvailableSpaces();
                    break;
                case 5:
                    if (ensureLogged()) showReports();
                    break;
                case 6:
                    listRecords();
                    break;
                case 7:
                    showRates();
                    break;
                case 8:
                    saveAll();
                    break;
                case 9:
                    System.out.println("Finalizando programa.");
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (true);
    }

    private boolean ensureLogged() {
        if (currentUser == null) {
            System.out.println("Debe iniciar sesión primero.");
            return false;
        }
        return true;
    }

    private void doLogin() {
        System.out.print("Usuario: ");
        String user = sc.nextLine().trim();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine().trim();
        User u = handling.findUser(user, pass);
        if (u == null) {
            System.out.println("Login fallido. Credenciales incorrectas.");
        } else {
            currentUser = u;
            System.out.println("Login exitoso: " + u.getUserName());
        }
    }

    private void registerEntry() {
        System.out.print("Ingrese placa: ");
        String plate = sc.nextLine().trim().toUpperCase();
        System.out.print("Ingrese tipo de vehículo (ej: Carro, Moto): ");
        String type = sc.nextLine().trim();
        if (handling.findRateByType(type) == null) {
            System.out.println("Tipo no encontrado. Tarifas existentes:");
            showRates();
            return;
        }
        System.out.print("Fecha y hora (dd/MM/yyyy HH:mm) o Enter para ahora: ");
        String input = sc.nextLine().trim();
        LocalDateTime when;
        try {
            when = input.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(input, DF_DATETIME);
        } catch (Exception e) {
            System.out.println("Formato de fecha/hora inválido.");
            return;
        }
        boolean ok = control.parkVehicle(plate, type, when);
        if (ok) System.out.println("Entrada registrada para " + plate + " a las " + when.format(DF_DATETIME));
        else System.out.println("No se pudo registrar entrada. ¿Ya está registrado o no hay cupos?");
    }

    private void registerExit() {
        System.out.print("Ingrese placa: ");
        String plate = sc.nextLine().trim().toUpperCase();
        System.out.print("Fecha y hora de salida (dd/MM/yyyy HH:mm) o Enter para ahora: ");
        String input = sc.nextLine().trim();
        LocalDateTime when;
        try {
            when = input.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(input, DF_DATETIME);
        } catch (Exception e) {
            System.out.println("Formato de fecha/hora inválido.");
            return;
        }
        double total = service.finishParking(plate, when);
        if (total < 0) {
            System.out.println("No existe entrada abierta para la placa " + plate);
        } else {
            System.out.println("Salida registrada. Total a pagar: $" + String.format("%.2f", total));
        }
    }

    private void saveAll() {
        handling.dumpAll();
        System.out.println("Todos los datos fueron escritos en disco.");
    }

    private void showAvailableSpaces() {
        int available = control.getAvailableSpaces();
        int total = config.Config.getSizeParking();
        System.out.println("Cupos disponibles: " + available + " / " + total);
    }

    private void showReports() {
        int option;
        do {
            System.out.println("------ REPORTES ------");
            System.out.println("1. Vehículos por fecha (dd/MM/yyyy)");
            System.out.println("2. Vehículos por hora (dd/MM/yyyy HH)");
            System.out.println("3. Recaudación por fecha (dd/MM/yyyy)");
            System.out.println("4. Recaudación por hora (dd/MM/yyyy HH)");
            System.out.println("5. Regresar");
            System.out.print("Seleccione: ");
            option = readInt();
            sc.nextLine();
            switch (option) {
                case 1:
                    System.out.print("Fecha (dd/MM/yyyy): ");
                    String d1 = sc.nextLine().trim();
                    System.out.println("El " + d1 + " ingresaron " + service.countVehiclesByDate(d1) + " vehículos.");
                    break;
                case 2:
                    System.out.print("Fecha y hora (dd/MM/yyyy HH): ");
                    String d2 = sc.nextLine().trim();
                    System.out.println("En " + d2 + " ingresaron " + service.countVehiclesByHour(d2) + " vehículos.");
                    break;
                case 3:
                    System.out.print("Fecha (dd/MM/yyyy): ");
                    String d3 = sc.nextLine().trim();
                    System.out.println("Recaudación total el " + d3 + ": $" + String.format("%.2f", service.totalIncomeByDate(d3)));
                    break;
                case 4:
                    System.out.print("Fecha y hora (dd/MM/yyyy HH): ");
                    String d4 = sc.nextLine().trim();
                    System.out.println("Recaudación total en " + d4 + ": $" + String.format("%.2f", service.totalIncomeByHour(d4)));
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (true);
    }

    private void listRecords() {
        System.out.println("1. Listar todos");
        System.out.println("2. Listar abiertos");
        System.out.println("3. Buscar por placa");
        System.out.println("4. Regresar");
        System.out.print("Seleccione: ");
        int op = readInt();
        sc.nextLine();
        switch (op) {
            case 1:
                List<RecordParking> all = service.getAllRecords();
                if (all.isEmpty()) System.out.println("No hay registros.");
                else all.forEach(r -> System.out.println(r));
                break;
            case 2:
                service.getAllRecords().stream().filter(r -> r.getDepartureTime() == null || r.getDepartureTime().trim().isEmpty()).forEach(r -> System.out.println(r));
                break;
            case 3:
                System.out.print("Ingrese placa: ");
                String plate = sc.nextLine().trim().toUpperCase();
                service.getAllRecords().stream().filter(r -> r.getLicensePlate().equalsIgnoreCase(plate)).forEach(r -> System.out.println(r));
                break;
            case 4:
                return;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private void showRates() {
        List<VehicleRate> rates = handling.getRates();
        if (rates.isEmpty()) {
            System.out.println("No hay tarifas cargadas.");
            return;
        }
        System.out.println("Tarifas:");
        for (VehicleRate vr : rates) {
            System.out.println(vr.getTypeVehicle() + " -> " + vr.getPrice());
        }
    }

    private int GetInt() {
        try {
            int x = Integer.parseInt(sc.nextLine().trim());
            return x;
        } catch (Exception e) {
            return -1;
        }
    }
}
