package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Logic.RecordParkingService;
import Persistence.HandlingPersistence;
import Model.RecordParking;

public class RecordParkingServiceTest {

    private Path tempDir;

    @BeforeEach
    public void setup() {
        tempDir = Path.of("target/test-data/rpsvc");
        if (Files.exists(tempDir)) {
            Files.walk(tempDir).sorted((a,b)->b.compareTo(a)).forEach(p->p.toFile().delete());
        }
        Files.createDirectories(tempDir);
        // create minimal files
        Files.writeString(tempDir.resolve("vehicleRate.csv"), "Carro;2000\n");
        Files.writeString(tempDir.resolve("recordParking.json"), "[]\n");
        // create empty loginUsers.ser
        Files.write(tempDir.resolve("loginUsers.ser"), new byte[0]);
        // set system property so Config points here
        System.setProperty("app.config.path.files", tempDir.toString()+"/");
        System.setProperty("app.config.sizeParking", "10");
        HandlingPersistence.resetInstance();
    }

   

    @Test
    public void testStartAndFinishParkingCalculatesTotal() {
        RecordParkingService service = new RecordParkingService();
        boolean started = service.startParking("ABC123", "Carro", LocalDateTime.of(2025,9,8,8,0));
        assertTrue(started, "Debe permitir iniciar parking");

        double total = service.finishParking("ABC123", LocalDateTime.of(2025,9,8,9,30));
        // 1h30 -> 2 hours * 2000
        assertEquals(4000.0, total, 0.001);
    }
}
