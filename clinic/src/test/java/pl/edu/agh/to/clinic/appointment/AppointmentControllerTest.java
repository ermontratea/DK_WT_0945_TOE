package pl.edu.agh.to.clinic.appointment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.to.clinic.exceptions.AppointmentNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllAppointments() throws Exception {
        when(appointmentService.getAppointments())
                .thenReturn(List.of(new AppointmentDto()));

        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAppointmentById() throws Exception {
        AppointmentDto dto = new AppointmentDto();
        dto.setId(1L);

        when(appointmentService.getAppointmentById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenAppointmentNotFound() throws Exception {
        when(appointmentService.getAppointmentById(1L))
                .thenThrow(new AppointmentNotFoundException(1L));

        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddAppointment() throws Exception {
        AppointmentDto dto = new AppointmentDto();
        dto.setPatientId(1L);
        dto.setDoctorId(2L);
        dto.setOfficeId(3L);
        dto.setDate(LocalDate.now());
        dto.setStartTime(LocalTime.of(8, 0));
        dto.setEndTime(LocalTime.of(8, 15));

        when(appointmentService.addAppointment(dto))
                .thenReturn(dto);

        mockMvc.perform(post("/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteAppointment() throws Exception {
        mockMvc.perform(delete("/appointments/1"))
                .andExpect(status().isOk());
    }
}
