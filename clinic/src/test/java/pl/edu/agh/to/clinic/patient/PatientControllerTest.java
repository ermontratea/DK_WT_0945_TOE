package pl.edu.agh.to.clinic.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.to.clinic.exceptions.GlobalExceptionHandler;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
@Import(GlobalExceptionHandler.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllPatients() throws Exception {
        PatientListDto p1 = new PatientListDto();
        p1.setId(1L);
        p1.setFirstName("Jan");
        p1.setLastName("Kowalski");
//        p1.setPesel("12345678901");
        p1.setAddress("Kraków");

        PatientListDto p2 = new PatientListDto();
        p2.setId(2L);
        p2.setFirstName("Anna");
        p2.setLastName("Nowak");
//        p2.setPesel("11111111111");
        p2.setAddress("Warszawa");

        when(patientService.getPatients()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jan")))
                .andExpect(jsonPath("$[1].firstName", is("Anna")));
    }

    @Test
    void shouldGetPatientById() throws Exception {
        PatientListDto dto = new PatientListDto();
        dto.setId(5L);
        dto.setFirstName("Anna");
        dto.setLastName("Nowak");
//        dto.setPesel("33333333333");
        dto.setAddress("Warszawa");

        when(patientService.getPatientById(5L)).thenReturn(dto);

        mockMvc.perform(get("/patients/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.firstName", is("Anna")))
                .andExpect(jsonPath("$.lastName", is("Nowak")))
//                .andExpect(jsonPath("$.pesel", is("33333333333")))
                .andExpect(jsonPath("$.address", is("Warszawa")));
    }

    @Test
    void shouldReturnNotFoundWhenPatientMissing() throws Exception {
        when(patientService.getPatientById(999L))
                .thenThrow(new PatientNotFoundException(999L));

        mockMvc.perform(get("/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Patient with ID: 999 not found."));
    }

    @Test
    void shouldCreatePatient() throws Exception {
        PatientDto request = new PatientDto();
        request.setFirstName("Jan");
        request.setLastName("Kowalski");
        request.setPesel("12345678901");
        request.setAddress("Kraków");

        PatientDto response = new PatientDto();
        response.setId(10L);
        response.setFirstName("Jan");
        response.setLastName("Kowalski");
        response.setPesel("12345678901");
        response.setAddress("Kraków");

        when(patientService.addPatient(any(PatientDto.class))).thenReturn(response);

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.firstName", is("Jan")))
                .andExpect(jsonPath("$.lastName", is("Kowalski")));
    }

    @Test
    void shouldReturnConflictWhenPeselDuplicated() throws Exception {
        PatientDto request = new PatientDto();
        request.setFirstName("Jan");
        request.setLastName("Kowalski");
        request.setPesel("12345678901");
        request.setAddress("Kraków");

        when(patientService.addPatient(any(PatientDto.class)))
                .thenThrow(new PeselDuplicationException("12345678901"));

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Person with PESEL: 12345678901 already exists."));
    }

    @Test
    void shouldReturnValidationErrorsForInvalidPatient() throws Exception {
        String json = """
                {
                  "firstName": "",
                  "lastName": "",
                  "pesel": "123",
                  "address": ""
                }
                """;

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("firstName: First name cannot be blank")))
                .andExpect(content().string(containsString("lastName: Last name cannot be blank")))
                .andExpect(content().string(containsString("pesel: PESEL must have exactly 11 digits")))
                .andExpect(content().string(containsString("address: Address cannot be blank")));
    }

    @Test
    void shouldDeletePatient() throws Exception {
        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingPatient() throws Exception {
        doThrow(new PatientNotFoundException(999L))
                .when(patientService).deletePatientById(999L);

        mockMvc.perform(delete("/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Patient with ID: 999 not found."));
    }
}
