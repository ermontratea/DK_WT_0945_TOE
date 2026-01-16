package pl.edu.agh.to.clinic.office;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.clinic.duty.Duty;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeServiceUnitTest {

    @Mock
    private OfficeRepository officeRepository;

    @InjectMocks
    private OfficeService officeService;

    @Test
    void shouldAddOfficeSuccessfully() throws Exception {
        OfficeDto dto = new OfficeDto();
        dto.setRoomNumber(101);

        when(officeRepository.existsByRoomNumber(101)).thenReturn(false);

        Office saved = new Office(101);
        Field idField = Office.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(saved, 1L);

        when(officeRepository.save(any(Office.class))).thenReturn(saved);

        OfficeDto result = officeService.addOffice(dto);

        assertEquals(1L, result.getId());
        assertEquals(101, result.getRoomNumber());

        verify(officeRepository).existsByRoomNumber(101);
        verify(officeRepository).save(any(Office.class));
    }

    @Test
    void shouldThrowWhenRoomNumberDuplicated() {
        OfficeDto dto = new OfficeDto();
        dto.setRoomNumber(101);

        when(officeRepository.existsByRoomNumber(101)).thenReturn(true);

        RoomNumberDuplicationException ex = assertThrows(
                RoomNumberDuplicationException.class,
                () -> officeService.addOffice(dto)
        );

        assertEquals("Office with room number: 101 already exists.", ex.getMessage());
        verify(officeRepository).existsByRoomNumber(101);
        verify(officeRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllOffices() {
        Office o1 = new Office(101);
        Office o2 = new Office(102);

        when(officeRepository.findAll()).thenReturn(List.of(o1, o2));

        List<OfficeDto> result = officeService.getOffices();

        assertEquals(2, result.size());
        assertEquals(101, result.get(0).getRoomNumber());
        assertEquals(102, result.get(1).getRoomNumber());

        verify(officeRepository).findAll();
    }

    @Test
    void shouldGetOfficeById() {
        Office office = new Office(101);

        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));

        OfficeDto result = officeService.getOfficeById(1L);

        assertEquals(101, result.getRoomNumber());
        verify(officeRepository).findById(1L);
    }

    @Test
    void getOfficeByIdShouldThrowWhenNotFound() {
        when(officeRepository.findById(999L)).thenReturn(Optional.empty());

        OfficeNotFoundException ex = assertThrows(
                OfficeNotFoundException.class,
                () -> officeService.getOfficeById(999L)
        );

        assertEquals("Office with ID: 999 not found.", ex.getMessage());
        verify(officeRepository).findById(999L);
    }

    @Test
    void shouldDeleteOfficeWhenNoDuties() throws Exception {
        Office office = new Office(101);

        // duties == null albo pusta lista -> kasujemy
        Field dutiesField = Office.class.getDeclaredField("duties");
        dutiesField.setAccessible(true);
        dutiesField.set(office, List.of());

        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));

        assertDoesNotThrow(() -> officeService.deleteOfficeById(1L));

        verify(officeRepository).findById(1L);
        verify(officeRepository).delete(office);
    }

    @Test
    void deleteOfficeShouldThrowWhenNotFound() {
        when(officeRepository.findById(999L)).thenReturn(Optional.empty());

        OfficeNotFoundException ex = assertThrows(
                OfficeNotFoundException.class,
                () -> officeService.deleteOfficeById(999L)
        );

        assertEquals("Office with ID: 999 not found.", ex.getMessage());
        verify(officeRepository).findById(999L);
        verify(officeRepository, never()).delete(any());
    }

    @Test
    void deleteOfficeShouldThrowWhenOfficeHasDuties() throws Exception {
        Office office = new Office(101);
        Duty duty = mock(Duty.class);

        Field dutiesField = Office.class.getDeclaredField("duties");
        dutiesField.setAccessible(true);
        dutiesField.set(office, List.of(duty));

        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> officeService.deleteOfficeById(1L)
        );

        assertEquals("You can't delete an office with assigned duties ", ex.getMessage());
        verify(officeRepository, never()).delete(any(Office.class));
    }
}
