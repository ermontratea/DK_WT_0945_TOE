package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

    @Test
    void shouldBuildDoctorAndUseGetters() {
        Doctor d = new Doctor("A", "B", "123", Specialization.CARDIOLOGY, "Addr");

        assertEquals("A", d.getFirstName());
        assertEquals("B", d.getLastName());
        assertEquals("123", d.getPesel());
        assertEquals(Specialization.CARDIOLOGY, d.getSpecialization());
        assertEquals("Addr", d.getAddress());
    }

    @Test
    void shouldUpdateFieldsWithSetters() {
        Doctor d = new Doctor("A", "B", "123", Specialization.CARDIOLOGY, "Addr");

        d.setFirstName("X");
        d.setLastName("Y");
        d.setPesel("999");
        d.setSpecialization(Specialization.DERMATOLOGY);
        d.setAddress("New");

        assertEquals("X", d.getFirstName());
        assertEquals("Y", d.getLastName());
        assertEquals("999", d.getPesel());
        assertEquals(Specialization.DERMATOLOGY, d.getSpecialization());
        assertEquals("New", d.getAddress());
    }

    @Test
    void toStringShouldReturnFullName() {
        Doctor d = new Doctor("Jan", "Kowalski", "123", Specialization.CARDIOLOGY, "Addr");
        assertEquals("Jan Kowalski", d.toString());
    }

    @Test
    void shouldUseNoArgsConstructorAndSetters() {
        Doctor d = new Doctor();

        assertNull(d.getId());

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
