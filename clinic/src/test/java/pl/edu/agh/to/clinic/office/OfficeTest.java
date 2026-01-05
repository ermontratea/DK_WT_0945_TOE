package pl.edu.agh.to.clinic.office;

import org.junit.jupiter.api.Test;
import pl.edu.agh.to.clinic.duty.Duty;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OfficeTest {

    @Test
    void shouldUseGetters() throws Exception {
        Office office = new Office();

        Field roomField = Office.class.getDeclaredField("roomNumber");
        roomField.setAccessible(true);
        roomField.setInt(office, 101);

        Field dutiesField = Office.class.getDeclaredField("duties");
        dutiesField.setAccessible(true);
        dutiesField.set(office, List.<Duty>of());

        assertEquals(101, office.getRoomNumber());
        assertNotNull(office.getDuties());
    }
}
