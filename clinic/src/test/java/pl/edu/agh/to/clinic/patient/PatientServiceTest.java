package pl.edu.agh.to.clinic.patient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void shouldAddPatientSuccessfully() {
        PatientDto dto = new PatientDto();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setPesel("12345678901");
        dto.setAddress("Kraków");

        when(patientRepository.existsByPesel("12345678901")).thenReturn(false);
                when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Patient.class));

        PatientDto saved = patientService.addPatient(dto);

        assertEquals("Jan", saved.getFirstName());
        assertEquals("Kowalski", saved.getLastName());
        assertEquals("12345678901", saved.getPesel());
        assertEquals("Kraków", saved.getAddress());

        verify(patientRepository).existsByPesel("12345678901");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void shouldThrowIfPeselExists() {
        PatientDto dto = new PatientDto();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setPesel("12345678901");
        dto.setAddress("Kraków");

        when(patientRepository.existsByPesel("12345678901")).thenReturn(true);

        PeselDuplicationException ex = assertThrows(
                PeselDuplicationException.class,
                () -> patientService.addPatient(dto)
        );

        assertEquals("Person with PESEL: 12345678901 already exists.", ex.getMessage());
        verify(patientRepository).existsByPesel("12345678901");
        verify(patientRepository, never()).save(any());
    }

    @Test
    void shouldGetPatientById() {
        Patient patient = new Patient("Anna", "Nowak", "11111111111", "Warszawa");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDto found = patientService.getPatientById(1L);

        assertEquals("Anna", found.getFirstName());
        assertEquals("Nowak", found.getLastName());
        assertEquals("11111111111", found.getPesel());
        assertEquals("Warszawa", found.getAddress());

        verify(patientRepository).findById(1L);
    }

    @Test
    void shouldThrowIfPatientNotFound() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        PatientNotFoundException ex = assertThrows(
                PatientNotFoundException.class,
                () -> patientService.getPatientById(999L)
        );

        assertEquals("Patient with ID: 999 not found.", ex.getMessage());
        verify(patientRepository).findById(999L);
    }

    @Test
    void shouldDeletePatientById() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> patientService.deletePatientById(1L));

        verify(patientRepository).existsById(1L);
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void deleteShouldThrowWhenPatientNotFound() {
        when(patientRepository.existsById(999L)).thenReturn(false);

        PatientNotFoundException ex = assertThrows(
                PatientNotFoundException.class,
                () -> patientService.deletePatientById(999L)
        );

        assertEquals("Patient with ID: 999 not found.", ex.getMessage());
        verify(patientRepository).existsById(999L);
        verify(patientRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldReturnAllPatientsAsDtos() {
        Patient p1 = new Patient("Jan", "Kowalski", "12345678901", "Kraków");
        Patient p2 = new Patient("Anna", "Nowak", "11111111111", "Warszawa");

        when(patientRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PatientDto> result = patientService.getPatients();

        assertEquals(2, result.size());
        assertEquals("Jan", result.get(0).getFirstName());
        assertEquals("Anna", result.get(1).getFirstName());

        verify(patientRepository).findAll();
    }
}
