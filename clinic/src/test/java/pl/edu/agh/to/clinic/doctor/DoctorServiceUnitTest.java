package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.clinic.duty.Duty;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.lang.reflect.Field;
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
    void shouldAddDoctorSuccessfully() throws Exception {
        DoctorDto dto = new DoctorDto();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setPesel("12345678901");
        dto.setSpecialization(Specialization.CARDIOLOGY);
        dto.setAddress("Kraków");

        when(doctorRepository.existsByPesel("12345678901")).thenReturn(false);

        Doctor saved = new Doctor("Jan", "Kowalski", "12345678901",
                Specialization.CARDIOLOGY, "Kraków");
        Field idField = saved.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(saved, 1L);

        when(doctorRepository.save(any(Doctor.class))).thenReturn(saved);

        DoctorDto result = doctorService.addDoctor(dto);

        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getFirstName());
        assertEquals("Kowalski", result.getLastName());
        assertEquals("12345678901", result.getPesel());
        assertEquals(Specialization.CARDIOLOGY, result.getSpecialization());
        assertEquals("Kraków", result.getAddress());

        verify(doctorRepository).existsByPesel("12345678901");
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void shouldThrowWhenPeselAlreadyExists() {
        DoctorDto dto = new DoctorDto();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setPesel("12345678901");
        dto.setSpecialization(Specialization.CARDIOLOGY);
        dto.setAddress("Kraków");

        when(doctorRepository.existsByPesel("12345678901")).thenReturn(true);

        PeselDuplicationException ex = assertThrows(
                PeselDuplicationException.class,
                () -> doctorService.addDoctor(dto)
        );

        assertEquals("Person with PESEL: 12345678901 already exists.", ex.getMessage());
        verify(doctorRepository).existsByPesel("12345678901");
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllDoctorsAsDtos() {
        Doctor d1 = new Doctor("Anna", "Nowak", "11111111111",
                Specialization.DERMATOLOGY, "Warszawa");
        Doctor d2 = new Doctor("Jan", "Kowalski", "22222222222",
                Specialization.CARDIOLOGY, "Kraków");

        when(doctorRepository.findAll()).thenReturn(List.of(d1, d2));

        List<DoctorDto> result = doctorService.getDoctors();

        assertEquals(2, result.size());
        assertEquals("Anna", result.get(0).getFirstName());
        assertEquals("Jan", result.get(1).getFirstName());

        verify(doctorRepository).findAll();
    }

    @Test
    void shouldGetDoctorById() {
        Doctor doctor = new Doctor("Anna", "Nowak", "33333333333",
                Specialization.DERMATOLOGY, "Warszawa");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorDto result = doctorService.getDoctorById(1L);

        assertEquals("Anna", result.getFirstName());
        assertEquals("Nowak", result.getLastName());
        assertEquals("33333333333", result.getPesel());
        assertEquals("Warszawa", result.getAddress());
        assertEquals(Specialization.DERMATOLOGY, result.getSpecialization());

        verify(doctorRepository).findById(1L);
    }

    @Test
    void getDoctorByIdShouldThrowWhenNotFound() {
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.getDoctorById(999L)
        );

        assertEquals("Doctor with ID: 999 not found.", ex.getMessage());
        verify(doctorRepository).findById(999L);
    }

    @Test
    void shouldDeleteDoctorByIdWhenNoDuties() {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901",
                Specialization.CARDIOLOGY, "Kraków");
        doctor.setDuties(List.of());

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        assertDoesNotThrow(() -> doctorService.deleteDoctorById(1L));

        verify(doctorRepository).findById(1L);
        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void deleteDoctorShouldThrowWhenNotFound() {
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.deleteDoctorById(999L)
        );

        assertEquals("Doctor with ID: 999 not found.", ex.getMessage());
        verify(doctorRepository).findById(999L);
        verify(doctorRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteDoctorShouldThrowWhenDoctorHasDuties() {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901",
                Specialization.CARDIOLOGY, "Kraków");
        Duty duty = mock(Duty.class);
        doctor.setDuties(List.of(duty));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> doctorService.deleteDoctorById(1L)
        );

        assertEquals("You can't delete a doctor with assigned duties ", ex.getMessage());
        verify(doctorRepository, never()).deleteById(anyLong());
    }
}
