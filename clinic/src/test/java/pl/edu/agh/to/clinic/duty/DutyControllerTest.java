package pl.edu.agh.to.clinic.duty;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.to.clinic.exceptions.DutyNotFoundException;
import pl.edu.agh.to.clinic.exceptions.GlobalExceptionHandler;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DutyController.class)
@Import(GlobalExceptionHandler.class)
class DutyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DutyService dutyService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldGetAllDuties() throws Exception {
        Duty duty = new Duty();
        when(dutyService.getDuties()).thenReturn(List.of(duty));

        mockMvc.perform(get("/duties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(dutyService).getDuties();
    }

    @Test
    void shouldGetDutyById() throws Exception {
        Duty duty = new Duty();
        when(dutyService.getDutyById(1L)).thenReturn(duty);

        mockMvc.perform(get("/duties/1"))
                .andExpect(status().isOk());

        verify(dutyService).getDutyById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDutyDoesNotExist() throws Exception {
        when(dutyService.getDutyById(999L))
                .thenThrow(new DutyNotFoundException(999L));

        mockMvc.perform(get("/duties/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Duty with ID: 999 not found."));
    }

    @Test
    void shouldAddDuty() throws Exception {
        String json = """
        {
          "doctor": {
            "firstName": "Jan",
            "lastName": "Kowalski",
            "pesel": "12345678901",
            "address": "Krakow",
            "specialization": "CARDIOLOGY"
          },
          "office": {
            "roomNumber": 101
          },
          "startTime": "2025-01-01T10:00:00",
          "endTime": "2025-01-01T12:00:00"
        }
        """;

        Duty returned = new Duty();
        when(dutyService.addDuty(any(Duty.class))).thenReturn(returned);

        mockMvc.perform(post("/duties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(dutyService).addDuty(any(Duty.class));
    }

    @Test
    void shouldReturnConflictWhenAddingDutyFails() throws Exception {
        String json = """
        {
          "doctor": {
            "firstName": "Jan",
            "lastName": "Kowalski",
            "pesel": "12345678901",
            "address": "Krakow",
            "specialization": "CARDIOLOGY"
          },
          "office": {
            "roomNumber": 101
          },
          "startTime": "2025-01-01T10:00:00",
          "endTime": "2025-01-01T12:00:00"
        }
        """;

        when(dutyService.addDuty(any(Duty.class)))
                .thenThrow(new IllegalStateException("Office is already occupied during these hours!"));

        mockMvc.perform(post("/duties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Office is already occupied during these hours!"));
    }

    @Test
    void shouldDeleteDuty() throws Exception {
        mockMvc.perform(delete("/duties/1"))
                .andExpect(status().isOk());

        verify(dutyService).deleteDutyById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingDuty() throws Exception {
        doThrow(new DutyNotFoundException(999L))
                .when(dutyService).deleteDutyById(999L);

        mockMvc.perform(delete("/duties/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Duty with ID: 999 not found."));
    }
}
