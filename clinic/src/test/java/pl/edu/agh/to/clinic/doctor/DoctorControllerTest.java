package pl.edu.agh.to.clinic.doctor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.GlobalExceptionHandler;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DoctorController.class)
@Import(GlobalExceptionHandler.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllDoctors() throws Exception {
        DoctorDto d1 = new DoctorDto();
        d1.setId(1L);
        d1.setFirstName("Jan");
        d1.setLastName("Kowalski");
        d1.setPesel("12345678901");
        d1.setAddress("Kraków");
        d1.setSpecialization(Specialization.CARDIOLOGY);

        DoctorDto d2 = new DoctorDto();
        d2.setId(2L);
        d2.setFirstName("Anna");
        d2.setLastName("Nowak");
        d2.setPesel("11111111111");
        d2.setAddress("Warszawa");
        d2.setSpecialization(Specialization.DERMATOLOGY);

        when(doctorService.getDoctors()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jan")))
                .andExpect(jsonPath("$[1].firstName", is("Anna")));
    }

    @Test
    void shouldGetDoctorById() throws Exception {
        DoctorDto dto = new DoctorDto();
        dto.setId(5L);
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setPesel("12345678901");
        dto.setAddress("Kraków");
        dto.setSpecialization(Specialization.CARDIOLOGY);

        when(doctorService.getDoctorById(5L)).thenReturn(dto);

        mockMvc.perform(get("/doctors/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.lastName", is("Kowalski")))
                .andExpect(jsonPath("$.pesel", is("12345678901")))
                .andExpect(jsonPath("$.address", is("Kraków")))
                .andExpect(jsonPath("$.specialization", is("CARDIOLOGY")));
    }

    @Test
    void shouldReturnNotFoundWhenDoctorMissing() throws Exception {
        when(doctorService.getDoctorById(999L))
                .thenThrow(new DoctorNotFoundException(999L));

        mockMvc.perform(get("/doctors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Doctor with ID: 999 not found."));
    }

    @Test
    void shouldCreateDoctor() throws Exception {
        DoctorDto request = new DoctorDto();
        request.setFirstName("Jan");
        request.setLastName("Kowalski");
        request.setPesel("12345678901");
        request.setAddress("Kraków");
        request.setSpecialization(Specialization.CARDIOLOGY);

        DoctorDto response = new DoctorDto();
        response.setId(10L);
        response.setFirstName("Jan");
        response.setLastName("Kowalski");
        response.setPesel("12345678901");
        response.setAddress("Kraków");
        response.setSpecialization(Specialization.CARDIOLOGY);

        when(doctorService.addDoctor(any(DoctorDto.class))).thenReturn(response);

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.lastName", is("Kowalski")));
    }

    @Test
    void shouldReturnConflictWhenPeselDuplicated() throws Exception {
        DoctorDto request = new DoctorDto();
        request.setFirstName("Jan");
        request.setLastName("Kowalski");
        request.setPesel("12345678901");
        request.setAddress("Kraków");
        request.setSpecialization(Specialization.CARDIOLOGY);

        when(doctorService.addDoctor(any(DoctorDto.class)))
                .thenThrow(new PeselDuplicationException("12345678901"));

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Person with PESEL: 12345678901 already exists."));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidDoctor() throws Exception {
        String json = """
                {
                  "firstName": "",
                  "lastName": "",
                  "pesel": "123",
                  "address": "",
                  "specialization": null
                }
                """;

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("firstName: First name cannot be blank")))
                .andExpect(content().string(containsString("lastName: Last name cannot be blank")))
                .andExpect(content().string(containsString("pesel: PESEL must have exactly 11 digits")))
                .andExpect(content().string(containsString("address: Address cannot be blank")))
                .andExpect(content().string(containsString("specialization: Specialization cannot be null")));
    }

    @Test
    void shouldReturnConflictWhenDeletingDoctorWithDuties() throws Exception {
        doThrow(new IllegalStateException("You can't delete a doctor with assigned duties "))
                .when(doctorService).deleteDoctorById(1L);

        mockMvc.perform(delete("/doctors/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("You can't delete a doctor with assigned duties "));
    }
}
