package pl.edu.agh.to.clinic.duty;

import org.junit.jupiter.api.Test;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.office.Office;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DutyTest {

    @Test
    void shouldUseConstructorAndGetters() {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901",
                Specialization.CARDIOLOGY, "Kraków");
        Office office = new Office(10);
        LocalTime start = LocalTime.of(8,0);
        LocalTime end   = LocalTime.of(12,0);
        DayOfWeek day = LocalDate.now().getDayOfWeek();

        Duty duty = new Duty(doctor, office, day, start, end);

        assertEquals(doctor, duty.getDoctor());
        assertEquals(office, duty.getOffice());
        assertEquals(start, duty.getStartTime());
        assertEquals(end, duty.getEndTime());
        assertEquals(10, duty.getOffice().getRoomNumber());
    }
}
