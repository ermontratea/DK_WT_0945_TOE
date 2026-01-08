package pl.edu.agh.to.clinic.duty;

import org.junit.jupiter.api.Test;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.office.Office;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DutyTest {

    @Test
    void shouldUseConstructorAndGetters() {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901",
                Specialization.CARDIOLOGY, "Kraków");
        Office office = new Office(10);
        LocalDateTime start = LocalDateTime.of(2025,1,1,8,0);
        LocalDateTime end   = LocalDateTime.of(2025,1,1,12,0);

        Duty duty = new Duty(doctor, office, start, end);

        assertEquals(doctor, duty.getDoctor());
        assertEquals(office, duty.getOffice());
        assertEquals(start, duty.getStartTime());
        assertEquals(end, duty.getEndTime());
        assertEquals(10, duty.getOffice().getRoomNumber());
    }
}
