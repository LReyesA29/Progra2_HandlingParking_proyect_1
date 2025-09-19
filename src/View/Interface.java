package View;

import Logic.HandlingParking;

import Model.RecordParking;
import Model.Vehicle;
import Model.VehicleRate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class Interface {

    private final HandlingParking handlingParking;
    private  Scanner sc;

    private static final DateTimeFormatter DF_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DF_DATE     = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Interface() {
        this.handlingParking = new HandlingParking();
        this.sc = new Scanner(System.in);
    }

    public void showInfo() {
       // obliga a crear un usuario si no hay ninguno
        ensureUserSetup();

        if (!requireLogin()) {
            printText("Finalizando el programa...");
            return;
        }
        while (true) {
            printMainMenu();
            String op = getText("Opcion: ").trim();
            
            switch (op) {
                case "1": 
                    showparkingg(); 
                    break;
                case "2": 
                    showMenuVehicles(); 
                    break;
                case "3": 
                    showReports(); 
                    break;
                case "4": 
                    mostrarCupos(); 
                    break;
                case "5": 
                    listarTarifas(); 
                    break; 
                case "6": 
                    menuUsuarios(); 
                    break;  
                case "0":
                    printText("Finalizando el programa...");
                    return;
                default:
                    printError("Opcipn invalida.");
            }
        }
    }

    /* validar usuarios crear*/
    private void ensureUserSetup() {
        if (handlingParking.hasUsers()) return;
        printText("No hay usuarios registrados creaa uno para continuar");
        while (!handlingParking.hasUsers()) {
            String u = getTextValidate("Usuario nuevo: ");
            String p1 = getTextValidate("Contraseña: ");
            String p2 = getTextValidate("Repite la contraseña: ");
            if (!p1.equals(p2)) {
                printError("Las contraseñas no coinciden.");
                continue;
            }
            boolean ok = handlingParking.createUser(u, p1);
            if (ok) {
                printText("Usuario creado correctamente.");
            } else {
                printError("No se pudo crear. ¿Nombre duplicado o datos inválidos?");
            }
        }
    }

    /*login*/
    private boolean requireLogin() {
        printText("=== Inicio de sesión ===");
        for (int i = 3; i >= 1; i--) {
            String user = getTextValidate("Usuario: ");
            String pass = getTextValidate("Contraseña: ");
            if (handlingParking.login(user, pass)) {
                printText("Acceso concedido. ¡Bienvenido " + user + "!");
                return true;
            }
            printError("Credenciales inválidas. Intentos restantes: " + (i - 1));
        }
        return false;
    }

    /*menus*/
    private void printMainMenu() {
        printText("\n===== PARQUEADERO - MENÚ PRINCIPAL =====");
        printText("1) Parqueo (iniciar / finalizar)");
        printText("2) Vehículos (crear / actualizar / eliminar / listar)");
        printText("3) Reportes");
        printText("4) Cupos disponibles");
        printText("5) Ver tarifas (CSV)");
        printText("6) Usuarios (crear)");
        printText("0) Salir");
    }

    private void menuUsuarios() {
        printText("\n--- USUARIOS ---");
        String u  = getTextValidate("Usuario: ");
        String p1 = getTextValidate("Contraseña: ");
        String p2 = getTextValidate("Repite la contraseña: ");
        if (!p1.equals(p2)) {
            printError("Las contraseñas no coinciden.");
            return;
        }
        boolean ok = handlingParking.createUser(u, p1);
        if (ok) printText("Usuario creado.");
        else printError("No se pudo crear (duplicado o inválido).");
    }

    private void showparkingg() {
        while (true) {
            printText("\n--- PARQUEO ---");
            printText("1) Iniciar parqueo");
            printText("2) Finalizar parqueo");
            printText("3) Listar registros");
            printText("0) Volver");
            String op = getText("Opción: ").trim();
            switch (op) {
                case "1": 
                    startParking(); 
                    break;
                case "2": 
                    finalizarParqueo(); 
                    break;
                case "3": 
                    listarRegistros(); 
                    break;
                case "0": 
                    return;
                default: 
                    printError("Opción inválida.");
            }
        }
    }

    private void showMenuVehicles() {
        while (true) {
            printText("\n--- VEHÍCULOS ---");
            printText("1) Crear vehículo");
            printText("2) Actualizar vehículo");
            printText("3) Eliminar vehículo");
            printText("4) Listar vehículos");
            printText("0) Volver");
            String opc = getText("Opción: ").trim();
            switch (opc) {
                case "1": 
                    crearVehiculo(); 
                    break;
                case "2": 
                    actualizarVehiculo(); 
                    break;
                case "3": 
                    eliminarVehiculo(); 
                    break;
                case "4": 
                    listarVehiculos(); 
                    break;
                case "0": 
                    return;
                default: 
                    printError("Opción inválida.");
            }
        }
    }

    private void showReports() {
        while (true) {
            printText("\n--- REPORTES ---");
            printText("1) Número de vehículos por día");
            printText("2) Total recaudado por día");
            printText("0) Volver");
            String opc = getText("Opción: ").trim();
            switch (opc) {
                case "1": 
                    reporteCantidadPorDia(); 
                    break;
                case "2": 
                    reporteTotalPorDia(); 
                    break;
                case "0": 
                    return;
                default: 
                    printError("Opción inválida.");
            }
        }
    }

    /*parkeadero general*/
    private void startParking() {
        printText("\n> Iniciar parqueo");
        String plate = getTextValidate("Placa (ej: ABC123): ").toUpperCase();
        String type  = getTextValidate("Tipo de vehículo (según CSV, ej: car, motorcycle, truck): ").trim();
        LocalDateTime when = readDateTime("Fecha y hora de ENTRADA (dd/MM/yyyy HH:mm): ");
        if (when == null) return;

        boolean ban = handlingParking.startParking(plate, type, when);
        if (!ban) {
            printError("No se pudo iniciar: revise cupos, tarifa CSV, solapes o registro abierto.");
            return;
        }
        printText("Parqueo iniciado para " + plate + ".");
    }

    private void finalizarParqueo() {
        printText("\n> Finalizar parqueo");
        String plate = getTextValidate("Placa: ").toUpperCase();
        LocalDateTime dep = readDateTime("Fecha y hora de SALIDA (dd/MM/yyyy HH:mm): ");
        if (dep == null) return;

        double total = handlingParking.finishParking(plate, dep);
        if (total < 0) {
            printError("No se pudo finalizar (sin abierto, salida <= entrada o solape).");
            return;
        }
        printText("Parqueo finalizado. Total: " + total);
    }

    private void listarRegistros() {
        printText("\n> Registros de parqueo (todos)");
        List<RecordParking> list = new ArrayList<>(handlingParking.getRecords());
        if (list.isEmpty()) { printText("(Sin registros)"); return; }
        for (RecordParking recordParking : list) {
            String dep = (recordParking.getDepartureTime() == null || recordParking.getDepartureTime().isBlank()) ? "(abierto)" : recordParking.getDepartureTime();
            printText("- Placa: " + recordParking.getLicensePlate()
                    + " | Tipo: " + recordParking.getTypeVehicle()
                    + " | Entrada: " + recordParking.getEntryTime()
                    + " | Salida: " + dep
                    + " | Total: " + recordParking.getTotal());
        }
    }

    /*Vehiculos */
    private void crearVehiculo() {
        printText("\n> Crear vehículo");
        String plate = getTextValidate("Placa (única): ").toUpperCase();
        String type  = getTextValidate("Tipo (coincida con CSV): ").trim();
        String owner = getTextValidate("Propietario: ");
        String model = getTextValidate("Modelo: ");
        String color = getTextValidate("Color: ");

        boolean ban = handlingParking.addVehicle(plate, type, owner, model, color);
        if (!ban) {
            printError("No se pudo crear (placa existente o tipo sin tarifa CSV).");
            return;
        }
        printText("Vehículo creado (XML actualizado).");
    }

    private void actualizarVehiculo() {
        printText("\n> Actualizar vehículo");
        String plate = getTextValidate("Placa: ").toUpperCase();
        String owner = getText("Propietario [ENTER = mantener]: ");
        String model = getText("Modelo [ENTER = mantener]: ");
        String color = getText("Color [ENTER = mantener]: ");
        String type  = getText("Tipo [ENTER = mantener]: ");

        boolean ok = handlingParking.updateVehicle(plate, type, owner, model, color);
        if (!ok) {
            printError("No se pudo actualizar (placa no existe o tipo inválido en CSV).");
            return;
        }
        printText("Vehículo actualizado (XML).");
    }

    private void eliminarVehiculo() {
        printText("\n> Eliminar vehículo");
        String plate = getTextValidate("Placa: ").toUpperCase();
        boolean ok = handlingParking.deleteVehicle(plate);
        if (!ok) {
            printError("No se pudo eliminar (placa inexistente o registro ABIERTO).");
            return;
        }
        printText("Vehículo eliminado (XML).");
    }

    private void listarVehiculos() {
        printText("\n> Vehículos");
        List<Vehicle> list = new ArrayList<>(handlingParking.getVehicles());
        if (list.isEmpty()) { printText("(Sin vehículos)"); return; }
        for (Vehicle vehicle : list) {
            printText(" Placa: " + vehicle.getLicensePlate()
                    + " Tipo: " + vehicle.getTypeVehicle()
                    + " Propietario: " + vehicle.getOwner()
                    + " Modelo: " + vehicle.getModel()
                    + " Color: " + vehicle.getColor()
                    + " Precio por hora: " + vehicle.getPriceHour());
        }
    }

    /* Reportes*/
    private void reporteCantidadPorDia() {
        printText("\n> Cantidad de vehículos por día");
        LocalDate day = readDate("Fecha (dd/MM/yyyy): ");
        if (day == null) return;
        long count = handlingParking.vehiclesCountByDay(day);
        printText("Ingresos en " + day.format(DF_DATE) + ": " + count + " vehículos.");
    }

    private void reporteTotalPorDia() {
        printText("\n> Total recaudado por día");
        LocalDate day = readDate("Fecha (dd/MM/yyyy): ");
        if (day == null) return;
        double total = handlingParking.incomeByDay(day);
        printText("Total recaudado en " + day.format(DF_DATE) + ": " + total);
    }

    private void mostrarCupos() {
        int libres = handlingParking.getAvailableSpaces();
        printText("\n> Cupos disponibles: " + libres);
    }

    private void listarTarifas() {
        printText("\n> Tarifas (CSV)");
        List<VehicleRate> rates = new ArrayList<>(handlingParking.getRates());
        if (rates.isEmpty()) { printText("(Sin tarifas cargadas)"); return; }
        for (VehicleRate r : rates) {
            printText("- Tipo: " + r.getTypeVehicle() + " | Precio por hora: " + r.getPrice());
        }
    }

    /*utilidades */
    private String getText(String text) { System.out.print(text); return sc.nextLine(); }
    private String getTextValidate(String text) {
        while (true) {
            String chain = getText(text);
            if (chain != null && !chain.trim().isEmpty())  return chain.trim();
            printError("Campo obligatorio.");
        }
    }
    private LocalDateTime readDateTime(String text) {
        String s = getTextValidate(text);
        try { return LocalDateTime.parse(s, DF_DATETIME); }
        catch (DateTimeParseException e) { printError("Formato inválido. Use dd/MM/yyyy HH:mm"); return null; }
    }
    private LocalDate readDate(String text) {
        String s = getTextValidate(text);
        try { return LocalDate.parse(s, DF_DATE); }
        catch (DateTimeParseException e) { printError("Formato inválido. Use dd/MM/yyyy"); return null; }
    }

    private void printText(String s) { System.out.println(s); }
    private void printError(String s) { System.out.println("(!) " + s); }
}
