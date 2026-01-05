package pl.edu.agh.to.clinic.patient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class PatientServiceTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Test
    void shouldAddPatientSuccessfully() {
        Patient p = new Patient("Jan", "Kowalski", "12345678901", "Kraków");

        Patient saved = patientService.addPatient(p);

        assertNotNull(saved.getId());
        assertEquals("Jan", saved.getFirstName());
        assertEquals("Kowalski", saved.getLastName());
        assertEquals("12345678901", saved.getPesel());
        assertEquals("Kraków", saved.getAddress());
    }

    @Test
    void shouldThrowIfPeselExists() {
        Patient p1 = new Patient("Jan", "Kowalski", "12345678901", "Kraków");
        patientService.addPatient(p1);

        Patient p2 = new Patient("Anna", "Nowak", "12345678901", "Warszawa");

        PeselDuplicationException ex = assertThrows(PeselDuplicationException.class,
                () -> patientService.addPatient(p2));

        assertEquals("Person with PESEL: 12345678901 already exists.", ex.getMessage());
    }

    @Test
    void shouldGetPatientByIdSuccessfully() {
        Patient p = new Patient("Anna", "Nowak", "11111111111", "Warszawa");
        Patient saved = patientService.addPatient(p);

        Patient found = patientService.getPatientById(saved.getId());

        assertEquals("Anna", found.getFirstName());
        assertEquals("Nowak", found.getLastName());
        assertEquals("11111111111", found.getPesel());
        assertEquals("Warszawa", found.getAddress());
    }

    @Test
    void shouldThrowIfPatientNotFound() {
        PatientNotFoundException ex = assertThrows(PatientNotFoundException.class,
                () -> patientService.getPatientById(999L));

        assertEquals("Patient with ID: 999 not found.", ex.getMessage());
    }

    @Test
    void shouldGetPatients() {
        patientService.addPatient(new Patient("A", "A", "11111111111", "X"));
        patientService.addPatient(new Patient("B", "B", "22222222222", "Y"));

        List<Patient> patients = patientService.getPatients();

        assertTrue(patients.size() >= 2);
    }

    @Test
    void shouldDeletePatientSuccessfully() {
        Patient p = new Patient("Jan", "Kowalski", "33333333333", "Kraków");
        Patient saved = patientService.addPatient(p);

        patientService.deletePatientById(saved.getId());

        assertFalse(patientRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void shouldThrowWhenDeletingNonExistingPatient() {
        PatientNotFoundException ex = assertThrows(PatientNotFoundException.class,
                () -> patientService.deletePatientById(999L));

        assertEquals("Patient with ID: 999 not found.", ex.getMessage());
    }
}
