package pl.edu.agh.to.clinic.duty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorRepository;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.exceptions.DutyNotFoundException;
import pl.edu.agh.to.clinic.office.Office;
import pl.edu.agh.to.clinic.office.OfficeRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class DutyServiceTest {

    @Autowired
    private DutyRepository dutyRepository;

    @Autowired
    private DutyService dutyService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OfficeRepository officeRepository;

    private Office createOffice(int roomNumber) throws Exception {
        Office office = new Office();
        Field roomField = Office.class.getDeclaredField("roomNumber");
        roomField.setAccessible(true);
        roomField.setInt(office, roomNumber);
        return officeRepository.saveAndFlush(office);
    }

    private Duty buildDuty(Doctor doctor, Office office,
                           LocalDateTime start, LocalDateTime end) throws Exception {
        Duty duty = new Duty();

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

        return duty;
    }

    private Duty createAndSaveDuty(Doctor doctor, Office office,
                                   LocalDateTime start, LocalDateTime end) throws Exception {
        Duty duty = buildDuty(doctor, office, start, end);
        return dutyRepository.saveAndFlush(duty);
    }

    @Test
    void shouldAddDutySuccessfully() throws Exception {
        Doctor doctor = doctorRepository.saveAndFlush(
                new Doctor("Jan", "Kowalski", "12345678901", Specialization.CARDIOLOGY, "Kraków"));
        Office office = createOffice(100);

        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0);
        LocalDateTime end = start.plusHours(4);

        Duty duty = buildDuty(doctor, office, start, end);

        Duty saved = dutyService.addDuty(duty);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(doctor.getId(), saved.getDoctor().getId());
        assertEquals(office.getRoomNumber(), saved.getOffice().getRoomNumber());
    }

    @Test
    void shouldThrowIfDoctorBusy() throws Exception {
        Doctor doctor = doctorRepository.saveAndFlush(
                new Doctor("Jan", "Kowalski", "22222222222", Specialization.CARDIOLOGY, "Kraków"));
        Office office1 = createOffice(200);
        Office office2 = createOffice(201);

        LocalDateTime start1 = LocalDateTime.now().plusDays(1).withHour(8);
        LocalDateTime end1 = start1.plusHours(4);

        createAndSaveDuty(doctor, office1, start1, end1);

        LocalDateTime start2 = start1.plusHours(1);
        LocalDateTime end2 = start2.plusHours(3);

        Duty overlapping = buildDuty(doctor, office2, start2, end2);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> dutyService.addDuty(overlapping));

        assertEquals("Doctor already has a duty assigned during these hours!", ex.getMessage());
    }

    @Test
    void shouldThrowIfOfficeBusy() throws Exception {
        Doctor doctor1 = doctorRepository.saveAndFlush(
                new Doctor("Jan", "Kowalski", "33333333333", Specialization.CARDIOLOGY, "Kraków"));
        Doctor doctor2 = doctorRepository.saveAndFlush(
                new Doctor("Anna", "Nowak", "44444444444", Specialization.DERMATOLOGY, "Warszawa"));
        Office office = createOffice(300);

        LocalDateTime start1 = LocalDateTime.now().plusDays(2).withHour(9);
        LocalDateTime end1 = start1.plusHours(3);

        createAndSaveDuty(doctor1, office, start1, end1);

        LocalDateTime start2 = start1.plusHours(1);
        LocalDateTime end2 = start2.plusHours(2);

        Duty overlapping = buildDuty(doctor2, office, start2, end2);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> dutyService.addDuty(overlapping));

        assertEquals("Office is already occupied during these hours!", ex.getMessage());
    }

    @Test
    void shouldGetDuties() throws Exception {
        Doctor doctor = doctorRepository.saveAndFlush(
                new Doctor("Jan", "Kowalski", "55555555555", Specialization.CARDIOLOGY, "Kraków"));
        Office office = createOffice(400);
        LocalDateTime start = LocalDateTime.now().plusDays(3).withHour(10);
        LocalDateTime end = start.plusHours(2);

        createAndSaveDuty(doctor, office, start, end);

        List<Duty> duties = dutyService.getDuties();

        assertFalse(duties.isEmpty());
    }

    @Test
    void shouldGetDutyByIdSuccessfully() throws Exception {
        Doctor doctor = doctorRepository.saveAndFlush(
                new Doctor("Jan", "Kowalski", "66666666666", Specialization.CARDIOLOGY, "Kraków"));
        Office office = createOffice(500);
        LocalDateTime start = LocalDateTime.now().plusDays(4).withHour(11);
        LocalDateTime end = start.plusHours(2);

        Duty duty = createAndSaveDuty(doctor, office, start, end);

        Duty found = dutyService.getDutyById(duty.getId());

        assertEquals(duty.getId(), found.getId());
        assertEquals(doctor.getId(), found.getDoctor().getId());
        assertEquals(office.getRoomNumber(), found.getOffice().getRoomNumber());
    }

    @Test
    void shouldThrowIfDutyNotFound() {
        DutyNotFoundException ex = assertThrows(DutyNotFoundException.class,
                () -> dutyService.getDutyById(999L));

        assertEquals("Duty with ID: 999 not found.", ex.getMessage());
    }

    @Test
    void shouldDeleteDutySuccessfully() throws Exception {
        Doctor doctor = doctorRepository.saveAndFlush(
                new Doctor("Jan", "Kowalski", "77777777777", Specialization.CARDIOLOGY, "Kraków"));
        Office office = createOffice(600);
        LocalDateTime start = LocalDateTime.now().plusDays(5).withHour(12);
        LocalDateTime end = start.plusHours(2);

        Duty duty = createAndSaveDuty(doctor, office, start, end);

        dutyService.deleteDutyById(duty.getId());

        assertFalse(dutyRepository.findById(duty.getId()).isPresent());
    }

    @Test
    void shouldThrowWhenDeletingNonExistingDuty() {
        DutyNotFoundException ex = assertThrows(DutyNotFoundException.class,
                () -> dutyService.deleteDutyById(999L));

        assertEquals("Duty with ID: 999 not found.", ex.getMessage());
    }
}
