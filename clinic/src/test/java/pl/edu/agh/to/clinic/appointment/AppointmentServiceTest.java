package pl.edu.agh.to.clinic.appointment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorRepository;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.duty.DutyRepository;
import pl.edu.agh.to.clinic.exceptions.AppointmentNotFoundException;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.office.OfficeRepository;
import pl.edu.agh.to.clinic.patient.Patient;
import pl.edu.agh.to.clinic.patient.PatientRepository;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private OfficeRepository officeRepository;
    @Mock private DutyRepository dutyRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void shouldReturnAllAppointments() {
        Patient p = new Patient("Jan", "Kowalski", "12345678901", "Krakow");
        Doctor d = new Doctor("Anna", "Nowak", "00000000000", Specialization.CARDIOLOGY, "A 1");
        Office o = new Office(101);

        setId(p, 1L);
        setId(d, 2L);
        setId(o, 3L);

        Appointment appt = new Appointment(
                p, d, o,
                LocalDate.of(2026, 1, 20),
                LocalTime.of(8, 0),
                LocalTime.of(8, 15)
        );
        setId(appt, 10L);

        when(appointmentRepository.findAll()).thenReturn(List.of(appt));

        List<AppointmentDto> result = appointmentService.getAppointments();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(1L, result.get(0).getPatientId());
        assertEquals(2L, result.get(0).getDoctorId());
        assertEquals(3L, result.get(0).getOfficeId());

        verify(appointmentRepository).findAll();
    }

    @Test
    void shouldReturnAppointmentById() {
        Patient p = new Patient("Jan", "Kowalski", "12345678901", "Krakow");
        Doctor d = new Doctor("Anna", "Nowak", "00000000000", Specialization.CARDIOLOGY, "A 1");
        Office o = new Office(101);

        setId(p, 1L);
        setId(d, 2L);
        setId(o, 3L);

        Appointment appt = new Appointment(
                p, d, o,
                LocalDate.of(2026, 1, 20),
                LocalTime.of(8, 0),
                LocalTime.of(8, 15)
        );
        setId(appt, 10L);

        when(appointmentRepository.findById(10L)).thenReturn(Optional.of(appt));

        AppointmentDto dto = appointmentService.getAppointmentById(10L);

        assertEquals(10L, dto.getId());
        assertEquals(1L, dto.getPatientId());
        assertEquals(2L, dto.getDoctorId());
        assertEquals(3L, dto.getOfficeId());

        verify(appointmentRepository).findById(10L);
    }

    @Test
    void shouldThrowWhenAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentNotFoundException ex = assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.getAppointmentById(1L)
        );

        assertEquals("Appointment with ID: 1 not found.", ex.getMessage());
        verify(appointmentRepository).findById(1L);
    }

    @Test
    void shouldAddAppointment() {
        AppointmentDto dto = new AppointmentDto();
        dto.setPatientId(1L);
        dto.setDoctorId(2L);
        dto.setOfficeId(3L);
        dto.setDate(LocalDate.of(2026, 1, 20)); // wtorek
        dto.setStartTime(LocalTime.of(8, 0));
        dto.setEndTime(LocalTime.of(8, 15));

        Patient patient = new Patient("Jan", "Kowalski", "12345678901", "Krakow");
        Doctor doctor = new Doctor("Anna", "Nowak", "00000000000", Specialization.CARDIOLOGY, "A 1");
        Office office = new Office(101);

        setId(patient, 1L);
        setId(doctor, 2L);
        setId(office, 3L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(3L)).thenReturn(Optional.of(office));

        when(dutyRepository.existsByDoctorAndOfficeAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                eq(doctor),
                eq(office),
                eq(DayOfWeek.TUESDAY),
                eq(dto.getStartTime()),
                eq(dto.getEndTime())
        )).thenReturn(true);

        when(appointmentRepository.existsByDoctorAndOfficeAndDateAndStartTimeBeforeAndEndTimeAfter(
                eq(doctor),
                eq(office),
                eq(dto.getDate()),
                eq(dto.getEndTime()),
                eq(dto.getStartTime())
        )).thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AppointmentDto result = appointmentService.addAppointment(dto);

        assertEquals(1L, result.getPatientId());
        assertEquals(2L, result.getDoctorId());
        assertEquals(3L, result.getOfficeId());
        assertEquals(dto.getDate(), result.getDate());
        assertEquals(dto.getStartTime(), result.getStartTime());
        assertEquals(dto.getEndTime(), result.getEndTime());

        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldDeleteAppointment() {
        when(appointmentRepository.existsById(10L)).thenReturn(true);

        assertDoesNotThrow(() -> appointmentService.deleteAppointmentById(10L));

        verify(appointmentRepository).existsById(10L);
        verify(appointmentRepository).deleteById(10L);
    }


    @Test
    void shouldThrowWhenDeletingMissingAppointment() {
        when(appointmentRepository.existsById(999L)).thenReturn(false);

        AppointmentNotFoundException ex = assertThrows(
                AppointmentNotFoundException.class,
                () -> appointmentService.deleteAppointmentById(999L)
        );

        assertEquals("Appointment with ID: 999 not found.", ex.getMessage());
        verify(appointmentRepository).existsById(999L);
        verify(appointmentRepository, never()).deleteById(anyLong());
    }


    private void setId(Object entity, long id) {
        try {
            Class<?> cls = entity.getClass();
            Field idField = null;

            while (cls != null && idField == null) {
                try {
                    idField = cls.getDeclaredField("id");
                } catch (NoSuchFieldException ignored) {
                    cls = cls.getSuperclass();
                }
            }

            if (idField == null) throw new IllegalStateException("No id field found");

            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
