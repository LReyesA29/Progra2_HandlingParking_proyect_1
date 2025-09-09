package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.User;
import Persistence.HandlingPersistence;

public class HandlingPersistenceTest {

    private Path tempDir;

    @BeforeEach
    public void setup() throws Exception {
        tempDir = Path.of("target/test-data/hp");
        if (Files.exists(tempDir)) {
            Files.walk(tempDir).sorted((a,b)->b.compareTo(a)).forEach(p->p.toFile().delete());
        }
        Files.createDirectories(tempDir);
        Files.writeString(tempDir.resolve("vehicleRate.csv"), "Carro;2000\n");
        Files.writeString(tempDir.resolve("recordParking.json"), "[]\n");
        Files.write(tempDir.resolve("loginUsers.ser"), new byte[0]);
        System.setProperty("app.config.path.files", tempDir.toString()+"/");
        System.setProperty("app.config.sizeParking", "5");
        HandlingPersistence.resetInstance();
    }

    @AfterEach
    public void teardown() throws Exception {
        System.clearProperty("app.config.path.files");
        System.clearProperty("app.config.sizeParking");
        HandlingPersistence.resetInstance();
    }

    @Test
    public void testLoadAndDumpUsers() throws Exception {
        HandlingPersistence h = HandlingPersistence.getInstance();
        // start empty
        assertTrue(h.getUsers().isEmpty());
        // add a user and dump all
        h.getUsers().add(new User("u1","p1"));
        h.dumpAll();
        // ensure file exists and was written
        Path p = tempDir.resolve("loginUsers.ser");
        assertTrue(Files.exists(p));
        long size = Files.size(p);
        assertTrue(size > 0, "El archivo .ser debe tener contenido tras dumpAll()");
        // reload instance to verify load works
        HandlingPersistence.resetInstance();
        HandlingPersistence h2 = HandlingPersistence.getInstance();
        assertTrue(!h2.getUsers().isEmpty(), "Al recargar debe encontrarse el usuario");
    }
}
