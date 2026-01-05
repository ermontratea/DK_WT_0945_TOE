package pl.edu.agh.to.clinic.office;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.to.clinic.doctor.Doctor;
import pl.edu.agh.to.clinic.doctor.DoctorRepository;
import pl.edu.agh.to.clinic.doctor.Specialization;
import pl.edu.agh.to.clinic.duty.Duty;
import pl.edu.agh.to.clinic.duty.DutyRepository;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class OfficeServiceTest {

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DutyRepository dutyRepository;

    private Office createOffice(int roomNumber) throws Exception {
        Office office = new Office();
        Field roomField = Office.class.getDeclaredField("roomNumber");
        roomField.setAccessible(true);
        roomField.setInt(office, roomNumber);
        return officeRepository.saveAndFlush(office);
    }

    @Test
    void shouldAddOfficeSuccessfully() throws Exception {
        Office office = new Office();
        Field roomField = Office.class.getDeclaredField("roomNumber");
        roomField.setAccessible(true);
        roomField.setInt(office, 101);

        Office saved = officeService.addOffice(office);

        assertNotNull(saved);
        assertNotNull(saved.getRoomNumber());
        assertEquals(101, saved.getRoomNumber());
    }

    @Test
    void shouldThrowIfRoomNumberExists() throws Exception {
        Office office1 = createOffice(200);

        Office office2 = new Office();
        Field roomField = Office.class.getDeclaredField("roomNumber");
        roomField.setAccessible(true);
        roomField.setInt(office2, 200);

        RoomNumberDuplicationException ex = assertThrows(RoomNumberDuplicationException.class,
                () -> officeService.addOffice(office2));

        assertEquals("Office with room number: 200 already exists.", ex.getMessage());
    }

    @Test
    void shouldGetOfficeByIdSuccessfully() throws Exception {
        Office office = createOffice(300);

        Office found = officeService.getOfficeById(office.getId());

        assertEquals(300, found.getRoomNumber());
    }

    @Test
    void shouldThrowIfOfficeNotFound() {
        OfficeNotFoundException ex = assertThrows(OfficeNotFoundException.class,
                () -> officeService.getOfficeById(999L));

        assertEquals("Office with ID: 999 not found.", ex.getMessage());
    }

    @Test
    void shouldGetOffices() throws Exception {
        createOffice(400);
        createOffice(401);

        List<Office> offices = officeService.getOffices();

        assertTrue(offices.size() >= 2);
    }

    @Test
    void shouldDeleteOfficeSuccessfullyWhenNoDuties() throws Exception {
        Office office = createOffice(500);

        officeService.deleteOfficeById(office.getId());

        assertFalse(officeRepository.findById(office.getId()).isPresent());
    }

    @Test
    void shouldThrowWhenDeletingNonExistingOffice() {
        OfficeNotFoundException ex = assertThrows(OfficeNotFoundException.class,
                () -> officeService.deleteOfficeById(999L));

        assertEquals("Office with ID: 999 not found.", ex.getMessage());
    }
}
