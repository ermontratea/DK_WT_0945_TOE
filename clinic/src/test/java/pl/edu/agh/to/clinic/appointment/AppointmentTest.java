package pl.edu.agh.to.clinic.appointment;

import org.junit.jupiter.api.Test;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.patient.Patient;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentTest {

    @Test
    void shouldExposeFieldsFromConstructor() {
        Patient p = new Patient("Jan", "Kowalski", "12345678901", "Krakow");
        Doctor d = new Doctor("Anna", "Nowak", "00000000000",
                pl.edu.agh.to.clinic.doctor.Specialization.CARDIOLOGY, "A 1");
        Office o = new Office(101);

        LocalDate date = LocalDate.of(2026, 1, 20);
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(8, 15);

        Appointment a = new Appointment(p, d, o, date, start, end);

        assertEquals(p, a.getPatient());
        assertEquals(d, a.getDoctor());
        assertEquals(o, a.getOffice());
        assertEquals(date, a.getDate());
        assertEquals(start, a.getStartTime());
        assertEquals(end, a.getEndTime());
    }
}
