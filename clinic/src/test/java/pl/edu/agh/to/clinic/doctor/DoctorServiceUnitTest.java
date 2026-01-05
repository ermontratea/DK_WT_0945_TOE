package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.clinic.duty.Duty;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceUnitTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void deleteDoctorShouldThrowWhenDoctorHasDuties() {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");

        Duty duty = mock(Duty.class);
        doctor.setDuties(List.of(duty));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> doctorService.deleteDoctorById(1L));

        assertEquals("You can't delete a doctor with assigned duties ", ex.getMessage());

        verify(doctorRepository, never()).deleteById(anyLong());
    }
}

