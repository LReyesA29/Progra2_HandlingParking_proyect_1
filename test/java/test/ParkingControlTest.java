package test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Logic.ParkingControl;
import Persistence.HandlingPersistence;

public class ParkingControlTest {

    private Path tempDir;

    @BeforeEach
    public void setup() throws Exception {
        tempDir = Path.of("target/test-data/pc");
        if (Files.exists(tempDir)) {
            Files.walk(tempDir).sorted((a,b)->b.compareTo(a)).forEach(p->p.toFile().delete());
        }
        Files.createDirectories(tempDir);
        Files.writeString(tempDir.resolve("vehicleRate.csv"), "Carro;2000\n");
        Files.writeString(tempDir.resolve("recordParking.json"), "[]\n");
        Files.write(tempDir.resolve("loginUsers.ser"), new byte[0]);
        System.setProperty("app.config.path.files", tempDir.toString()+"/");
        System.setProperty("app.config.sizeParking", "1"); // small capacity
        HandlingPersistence.resetInstance();
    }

    @AfterEach
    public void teardown() throws Exception {
        System.clearProperty("app.config.path.files");
        System.clearProperty("app.config.sizeParking");
        HandlingPersistence.resetInstance();
    }

    @Test
    public void testCapacityLimit() {
        ParkingControl control = new ParkingControl();
        boolean first = control.parkVehicle("AAA111", "Carro", java.time.LocalDateTime.now());
        assertTrue(first, "Primera entrada debe ser permitida");
        boolean second = control.parkVehicle("BBB222", "Carro", java.time.LocalDateTime.now());
        assertFalse(second, "Segunda entrada debe ser rechazada por falta de cupo");
    }
}
