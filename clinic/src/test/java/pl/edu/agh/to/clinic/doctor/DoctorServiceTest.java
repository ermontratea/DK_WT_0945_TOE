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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private void setId(Doctor doctor, long id) throws Exception {
        Field idField = doctor.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(doctor, id);
    }

    @Test
    void shouldAddDoctorSuccessfully() throws Exception {
        DoctorDto dto = new DoctorDto();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setPesel("12345678901");
        dto.setAddress("Kraków");
        dto.setSpecialization(Specialization.CARDIOLOGY);

        when(doctorRepository.existsByPesel("12345678901")).thenReturn(false);

        Doctor saved = new Doctor("Jan", "Kowalski", "12345678901",
                Specialization.CARDIOLOGY, "Kraków");
        setId(saved, 1L);

        when(doctorRepository.save(any(Doctor.class))).thenReturn(saved);

        DoctorDto result = doctorService.addDoctor(dto);

        assertEquals(1L, result.getId());
        assertEquals("Jan", result.getFirstName());
        assertEquals("Kowalski", result.getLastName());
        assertEquals("12345678901", result.getPesel());
        assertEquals("Kraków", result.getAddress());
        assertEquals(Specialization.CARDIOLOGY, result.getSpecialization());

        verify(doctorRepository).existsByPesel("12345678901");
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void shouldThrowWhenPeselAlreadyExists() {
        DoctorDto dto = new DoctorDto();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setPesel("12345678901");
        dto.setAddress("Kraków");
        dto.setSpecialization(Specialization.CARDIOLOGY);

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

        List<DoctorListDto> result = doctorService.getDoctors();

        assertEquals(2, result.size());
        assertEquals("Anna", result.get(0).getFirstName());
        assertEquals("Jan", result.get(1).getFirstName());
        verify(doctorRepository).findAll();
    }

    @Test
    void shouldGetDoctorById() throws Exception {
        Doctor doctor = new Doctor("Anna", "Nowak", "33333333333",
                Specialization.DERMATOLOGY, "Warszawa");
        setId(doctor, 5L);

        when(doctorRepository.findById(5L)).thenReturn(Optional.of(doctor));

        DoctorListDto result = doctorService.getDoctorById(5L);

        assertEquals("Anna", result.getFirstName());
        assertEquals("Nowak", result.getLastName());
        assertEquals("Warszawa", result.getAddress());
        assertEquals(Specialization.DERMATOLOGY, result.getSpecialization());
        verify(doctorRepository).findById(5L);
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
    void shouldDeleteDoctorWhenNoDuties() {
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
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(
                DoctorNotFoundException.class,
                () -> doctorService.deleteDoctorById(1L)
        );

        assertEquals("Doctor with ID: 1 not found.", ex.getMessage());
        verify(doctorRepository).findById(1L);
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
