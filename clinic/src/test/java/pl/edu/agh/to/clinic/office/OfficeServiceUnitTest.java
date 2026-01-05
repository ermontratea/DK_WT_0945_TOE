package pl.edu.agh.to.clinic.office;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.agh.to.clinic.duty.Duty;

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
    void deleteOfficeShouldThrowWhenOfficeHasDuties() throws Exception {
        Office office = new Office();

        Duty duty = mock(Duty.class); // nie musi być w pełni "prawdziwy"

        Field dutiesField = Office.class.getDeclaredField("duties");
        dutiesField.setAccessible(true);
        dutiesField.set(office, List.of(duty));

        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> officeService.deleteOfficeById(1L));

        assertEquals("You can't delete an office with assigned duties ", ex.getMessage());

        verify(officeRepository, never()).delete(any(Office.class));
    }
}
