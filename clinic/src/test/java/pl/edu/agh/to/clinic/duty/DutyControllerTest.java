package pl.edu.agh.to.clinic.duty;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.to.clinic.exceptions.DutyNotFoundException;
import pl.edu.agh.to.clinic.exceptions.GlobalExceptionHandler;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DutyController.class)
@Import(GlobalExceptionHandler.class)
class DutyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DutyService dutyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllDuties() throws Exception {
        DutyDto d1 = new DutyDto();
        d1.setId(1L);
        d1.setDoctorId(1L);
        d1.setOfficeId(2L);
        d1.setStartTime(LocalTime.of(8,0));
        d1.setEndTime(LocalTime.of(12,0));

        when(dutyService.getDuties()).thenReturn(List.of(d1));

        mockMvc.perform(get("/duties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].doctorId", is(1)))
                .andExpect(jsonPath("$[0].officeId", is(2)));
    }

    @Test
    void shouldGetDutyById() throws Exception {
        DutyDto dto = new DutyDto();
        dto.setId(5L);
        dto.setDoctorId(1L);
        dto.setOfficeId(2L);
        dto.setStartTime(LocalTime.of(8,0));
        dto.setEndTime(LocalTime.of(12,0));

        when(dutyService.getDutyById(5L)).thenReturn(dto);

        mockMvc.perform(get("/duties/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.officeId", is(2)));
    }

    @Test
    void shouldReturnNotFoundWhenDutyMissing() throws Exception {
        when(dutyService.getDutyById(999L))
                .thenThrow(new DutyNotFoundException(999L));

        mockMvc.perform(get("/duties/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Duty with ID: 999 not found."));
    }

    @Test
    void shouldAddDuty() throws Exception {
        DutyDto request = new DutyDto();
        request.setDoctorId(1L);
        request.setOfficeId(2L);
        request.setStartTime(LocalTime.of(8,0));
        request.setEndTime(LocalTime.of(12,0));

        DutyDto response = new DutyDto();
        response.setId(10L);
        response.setDoctorId(1L);
        response.setOfficeId(2L);
        response.setStartTime(request.getStartTime());
        response.setEndTime(request.getEndTime());
        request.setDayOfWeek(DayOfWeek.MONDAY);

        when(dutyService.addDuty(any(DutyDto.class))).thenReturn(response);

        mockMvc.perform(post("/duties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.officeId", is(2)));
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
