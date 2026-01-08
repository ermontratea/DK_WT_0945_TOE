package pl.edu.agh.to.clinic.duty;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorRepository;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.DutyNotFoundException;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.office.OfficeRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DutyServiceTest {

    @Mock
    private DutyRepository dutyRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private DutyService dutyService;

    private void setId(Object entity, long id) throws Exception {
        Class<?> cls = entity.getClass();
        Field idField = null;
        while (cls != null && idField == null) {
            try {
                idField = cls.getDeclaredField("id");
            } catch (NoSuchFieldException ignored) {
                cls = cls.getSuperclass();
            }
        }
        if (idField == null) {
            throw new IllegalStateException("No id field found");
        }
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    @Test
    void shouldAddDutySuccessfully() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 1, 1, 12, 0);

        DutyDto dto = new DutyDto();
        dto.setDoctorId(1L);
        dto.setOfficeId(2L);
        dto.setStartTime(start);
        dto.setEndTime(end);

        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", null, "Kraków");
        Office office = new Office(101);

        setId(doctor, 1L);
        setId(office, 2L);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(2L)).thenReturn(Optional.of(office));
        when(dutyRepository.existsByDoctorAndStartTimeBeforeAndEndTimeAfter(
                any(), any(), any())
        ).thenReturn(false);
        when(dutyRepository.existsByOfficeAndStartTimeBeforeAndEndTimeAfter(
                any(), any(), any())
        ).thenReturn(false);

        Duty saved = new Duty(doctor, office, start, end);
        setId(saved, 10L);

        when(dutyRepository.save(any(Duty.class))).thenReturn(saved);

        DutyDto result = dutyService.addDuty(dto);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getDoctorId());
        assertEquals(2L, result.getOfficeId());
        assertEquals(start, result.getStartTime());
        assertEquals(end, result.getEndTime());

        verify(doctorRepository).findById(1L);
        verify(officeRepository).findById(2L);
        verify(dutyRepository).existsByDoctorAndStartTimeBeforeAndEndTimeAfter(
                doctor, end, start);
        verify(dutyRepository).existsByOfficeAndStartTimeBeforeAndEndTimeAfter(
                office, end, start);
        verify(dutyRepository).save(any(Duty.class));
    }

    @Test
    void shouldThrowWhenDoctorNotFound() {
        DutyDto dto = new DutyDto();
        dto.setDoctorId(1L);
        dto.setOfficeId(2L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusHours(4));

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorNotFoundException ex = assertThrows(
                DoctorNotFoundException.class,
                () -> dutyService.addDuty(dto)
        );

        assertEquals("Doctor with ID: 1 not found.", ex.getMessage());
        verify(doctorRepository).findById(1L);
        verifyNoInteractions(officeRepository);
        verify(dutyRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenOfficeNotFound() {
        DutyDto dto = new DutyDto();
        dto.setDoctorId(1L);
        dto.setOfficeId(2L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusHours(4));

        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", null, "Kraków");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(2L)).thenReturn(Optional.empty());

        OfficeNotFoundException ex = assertThrows(
                OfficeNotFoundException.class,
                () -> dutyService.addDuty(dto)
        );

        assertEquals("Office with ID: 2 not found.", ex.getMessage());
        verify(doctorRepository).findById(1L);
        verify(officeRepository).findById(2L);
        verify(dutyRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDoctorBusy() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(4);

        DutyDto dto = new DutyDto();
        dto.setDoctorId(1L);
        dto.setOfficeId(2L);
        dto.setStartTime(start);
        dto.setEndTime(end);

        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", null, "Kraków");
        Office office = new Office(101);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(2L)).thenReturn(Optional.of(office));
        when(dutyRepository.existsByDoctorAndStartTimeBeforeAndEndTimeAfter(
                doctor, end, start)
        ).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> dutyService.addDuty(dto)
        );

        assertEquals("Doctor already has a duty assigned during these hours!", ex.getMessage());
        verify(dutyRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenOfficeBusy() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(4);

        DutyDto dto = new DutyDto();
        dto.setDoctorId(1L);
        dto.setOfficeId(2L);
        dto.setStartTime(start);
        dto.setEndTime(end);

        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", null, "Kraków");
        Office office = new Office(101);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(2L)).thenReturn(Optional.of(office));
        when(dutyRepository.existsByDoctorAndStartTimeBeforeAndEndTimeAfter(
                doctor, end, start)
        ).thenReturn(false);
        when(dutyRepository.existsByOfficeAndStartTimeBeforeAndEndTimeAfter(
                office, end, start)
        ).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> dutyService.addDuty(dto)
        );

        assertEquals("Office is already occupied during these hours!", ex.getMessage());
        verify(dutyRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllDuties() {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", null, "Kraków");
        Office office = new Office(101);
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 1, 1, 12, 0);

        Duty duty = new Duty(doctor, office, start, end);

        when(dutyRepository.findAll()).thenReturn(List.of(duty));

        List<DutyDto> result = dutyService.getDuties();

        assertEquals(1, result.size());
        assertEquals(start, result.get(0).getStartTime());
        assertEquals(end, result.get(0).getEndTime());
        verify(dutyRepository).findAll();
    }

    @Test
    void shouldGetDutyById() {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", null, "Kraków");
        Office office = new Office(101);
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 8, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 1, 1, 12, 0);
        Duty duty = new Duty(doctor, office, start, end);

        when(dutyRepository.findById(1L)).thenReturn(Optional.of(duty));

        DutyDto result = dutyService.getDutyById(1L);

        assertEquals(start, result.getStartTime());
        assertEquals(end, result.getEndTime());
        verify(dutyRepository).findById(1L);
    }

    @Test
    void getDutyByIdShouldThrowWhenNotFound() {
        when(dutyRepository.findById(999L)).thenReturn(Optional.empty());

        DutyNotFoundException ex = assertThrows(
                DutyNotFoundException.class,
                () -> dutyService.getDutyById(999L)
        );

        assertEquals("Duty with ID: 999 not found.", ex.getMessage());
        verify(dutyRepository).findById(999L);
    }

    @Test
    void shouldDeleteDutyById() {
        when(dutyRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> dutyService.deleteDutyById(1L));

        verify(dutyRepository).existsById(1L);
        verify(dutyRepository).deleteById(1L);
    }

    @Test
    void deleteDutyShouldThrowWhenNotFound() {
        when(dutyRepository.existsById(999L)).thenReturn(false);

        DutyNotFoundException ex = assertThrows(
                DutyNotFoundException.class,
                () -> dutyService.deleteDutyById(999L)
        );

        assertEquals("Duty with ID: 999 not found.", ex.getMessage());
        verify(dutyRepository).existsById(999L);
        verify(dutyRepository, never()).deleteById(anyLong());
    }
}
