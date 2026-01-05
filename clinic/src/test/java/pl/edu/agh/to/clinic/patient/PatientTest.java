package pl.edu.agh.to.clinic.patient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void shouldBuildPatientAndUseGetters() {
        Patient p = new Patient("Jan", "Kowalski", "12345678901", "Kraków");

        assertEquals("Jan", p.getFirstName());
        assertEquals("Kowalski", p.getLastName());
        assertEquals("12345678901", p.getPesel());
        assertEquals("Kraków", p.getAddress());
        assertEquals("Jan Kowalski", p.toString());
    }

    @Test
    void shouldUseNoArgsConstructorAndSetters() {
        Patient p = new Patient();

        assertNull(p.getId());

        p.setFirstName("Anna");
        p.setLastName("Nowak");
        p.setPesel("11111111111");
        p.setAddress("Warszawa");

        assertEquals("Anna", p.getFirstName());
        assertEquals("Nowak", p.getLastName());
        assertEquals("11111111111", p.getPesel());
        assertEquals("Warszawa", p.getAddress());
        assertEquals("Anna Nowak", p.toString());
    }
}
