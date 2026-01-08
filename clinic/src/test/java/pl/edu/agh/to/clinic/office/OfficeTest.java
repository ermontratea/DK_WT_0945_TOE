package pl.edu.agh.to.clinic.office;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfficeTest {

    @Test
    void shouldUseConstructorAndGetters() {
        Office office = new Office(101);

        assertEquals(101, office.getRoomNumber());
        assertEquals("Office 101", office.toString());
    }
}
