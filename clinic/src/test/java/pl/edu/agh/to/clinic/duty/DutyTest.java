package pl.edu.agh.to.clinic.duty;

import org.junit.jupiter.api.Test;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.office.Office;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DutyTest {

    @Test
    void shouldUseGetters() throws Exception {
        Doctor doctor = new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków");
        Office office = new Office();

        Field roomField = Office.class.getDeclaredField("roomNumber");
        roomField.setAccessible(true);
        roomField.setInt(office, 10);

        Duty duty = new Duty();

        LocalDateTime start = LocalDateTime.now().withHour(8).withMinute(0);
        LocalDateTime end = start.plusHours(4);

        Field doctorField = Duty.class.getDeclaredField("doctor");
        doctorField.setAccessible(true);
        doctorField.set(duty, doctor);

        Field officeField = Duty.class.getDeclaredField("office");
        officeField.setAccessible(true);
        officeField.set(duty, office);

        Field startField = Duty.class.getDeclaredField("startTime");
        startField.setAccessible(true);
        startField.set(duty, start);

        Field endField = Duty.class.getDeclaredField("endTime");
        endField.setAccessible(true);
        endField.set(duty, end);

        assertEquals(doctor, duty.getDoctor());
        assertEquals(office, duty.getOffice());
        assertEquals(start, duty.getStartTime());
        assertEquals(end, duty.getEndTime());
        assertEquals(10, duty.getOffice().getRoomNumber());
    }
}
