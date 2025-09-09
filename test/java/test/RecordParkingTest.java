package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import Model.RecordParking;
import org.junit.jupiter.api.Test;

public class RecordParkingTest {

    @Test
    public void testHoursParkedRoundedUp() {
        RecordParking r = new RecordParking("ABC123", "Carro",
                "08/09/2025 08:00", "08/09/2025 09:30", 0);

        long hours = r.getHoursParkedRoundedUp();

        assertEquals(2, hours, "Debe redondear hacia arriba a 2 horas");
    }
}
