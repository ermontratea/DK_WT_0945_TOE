package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class DoctorServiceTest {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorService doctorService;

    @Test
    void shouldAddDoctorSuccessfully() {
        Doctor doc = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");

        Doctor saved = doctorService.addDoctor(doc);

        assertNotNull(saved.getId());
        assertEquals("Jan", saved.getFirstName());
        assertEquals("Kowalski", saved.getLastName());
        assertEquals("12345678901", saved.getPesel());
        assertEquals(Specialization.CARDIOLOGY, saved.getSpecialization());
        assertEquals("Kraków", saved.getAddress());
    }

    @Test
    void shouldThrowIfPeselExists() {
        Doctor doc = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");
        doctorService.addDoctor(doc);

        Doctor duplicate = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");

        PeselDuplicationException exception = assertThrows(PeselDuplicationException.class,
                () -> doctorService.addDoctor(duplicate));

        assertEquals("Person with PESEL: 12345678901 already exists.", exception.getMessage());
    }

    @Test
    void shouldGetDoctorByIdSuccessfully() {
        Doctor doc = new Doctor("Anna", "Nowak", "11111111111", Specialization.DERMATOLOGY, "Warszawa");
        Doctor saved = doctorService.addDoctor(doc);

        Doctor found = doctorService.getDoctorById(saved.getId());

        assertEquals("Anna", found.getFirstName());
        assertEquals("Nowak", found.getLastName());
        assertEquals(Specialization.DERMATOLOGY, saved.getSpecialization());
        assertEquals("Warszawa", saved.getAddress());
    }

    @Test
    void shouldThrowIfDoctorNotFound() {
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.getDoctorById(999L));

        assertEquals("Doctor with ID: 999 not found.", exception.getMessage());
    }

    @Test
    void shouldDeleteDoctorSuccessfully() {
        Doctor doc = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");
        doctorService.addDoctor(doc);

        doctorService.deleteDoctorById(doc.getId());

        assertFalse(doctorRepository.findById(doc.getId()).isPresent());
    }

    @Test
    void shouldGetDoctors() {
        doctorService.addDoctor(new Doctor("A", "A", "11111111111", Specialization.CARDIOLOGY, "X"));
        doctorService.addDoctor(new Doctor("B", "B", "22222222222", Specialization.DERMATOLOGY, "Y"));

        List<Doctor> doctors = doctorService.getDoctors();

        assertTrue(doctors.size() >= 2);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingDoctor() {
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.deleteDoctorById(999L));

        assertEquals("Doctor with ID: 999 not found.", exception.getMessage());
    }
}
