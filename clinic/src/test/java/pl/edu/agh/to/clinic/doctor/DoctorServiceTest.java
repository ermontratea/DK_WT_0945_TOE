package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DoctorServiceTest {

    DoctorRepository repo = mock(DoctorRepository.class);
    DoctorService service = new DoctorService(repo);

    @Test
    void shouldThrowIfPeselExists() {
        Doctor doc = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");
        when(repo.existsByPesel("12345678901")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.addDoctor(doc));
    }
}

