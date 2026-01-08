package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

    @Test
    void shouldBuildDoctorAndUseGetters() {
        Doctor d = new Doctor("A", "B", "12345678901", Specialization.CARDIOLOGY, "Addr");

        assertEquals("A", d.getFirstName());
        assertEquals("B", d.getLastName());
        assertEquals("12345678901", d.getPesel());
        assertEquals(Specialization.CARDIOLOGY, d.getSpecialization());
        assertEquals("Addr", d.getAddress());
        assertEquals("A B", d.toString());
    }

    @Test
    void shouldUpdateDoctorWithSetters() {
        Doctor d = new Doctor("X", "Y", "99999999999", Specialization.DERMATOLOGY, "Old");

        d.setFirstName("Jan");
        d.setLastName("Kowalski");
        d.setPesel("12345678901");
        d.setSpecialization(Specialization.CARDIOLOGY);
        d.setAddress("Kraków");

        assertEquals("Jan", d.getFirstName());
        assertEquals("Kowalski", d.getLastName());
        assertEquals("12345678901", d.getPesel());
        assertEquals(Specialization.CARDIOLOGY, d.getSpecialization());
        assertEquals("Kraków", d.getAddress());
        assertEquals("Jan Kowalski", d.toString());
    }
}
