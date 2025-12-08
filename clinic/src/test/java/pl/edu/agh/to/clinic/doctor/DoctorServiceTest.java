package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
    }

    @Test
    void shouldThrowIfPeselExists() {
        Doctor doc = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");
        doctorService.addDoctor(doc);

        Doctor duplicate = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> doctorService.addDoctor(duplicate));

        assertEquals("Doctor with this pesel already exists", exception.getMessage());
    }

    @Test
    void shouldGetDoctorByIdSuccessfully() {
        Doctor doc = new Doctor("Anna", "Nowak", "11111111111", Specialization.DERMATOLOGY, "Warszawa");
        Doctor saved = doctorService.addDoctor(doc);

        Doctor found = doctorService.getDoctorById(saved.getId());

        assertEquals("Anna", found.getFirstName());
    }

    @Test
    void shouldThrowIfDoctorNotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> doctorService.getDoctorById(999L));

        assertEquals("Doctor with id: 999 not found", exception.getMessage());
    }

    @Test
    void shouldDeleteDoctorSuccessfully() {
        Doctor doc = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");
        doctorService.addDoctor(doc);

        doctorService.deleteDoctorById(doc.getId());

        assertFalse(doctorRepository.findById(doc.getId()).isPresent());
    }
}
