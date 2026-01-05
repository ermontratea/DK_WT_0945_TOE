package pl.edu.agh.to.clinic.patient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldAddPatient() throws Exception {
        String json = """
        {
          "firstName": "Jan",
          "lastName": "Kowalski",
          "pesel": "12345678901",
          "address": "Kraków"
        }
        """;

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.pesel").value("12345678901"))
                .andExpect(jsonPath("$.address").value("Kraków"));
    }

    @Test
    void shouldReturnErrorWhenPeselExists() throws Exception {
        Patient p = new Patient("Jan", "Nowak", "12345678901", "Warszawa");
        patientRepository.saveAndFlush(p);

        String json = """
        {
          "firstName": "Jan",
          "lastName": "Kowalski",
          "pesel": "12345678901",
          "address": "Kraków"
        }
        """;

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
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
    void shouldReturnErrorForNonExistingPatient() throws Exception {
        mockMvc.perform(get("/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Patient with ID: 999 not found."));
    }

    @Test
    void shouldDeletePatient() throws Exception {
        Patient p = new Patient("Marta", "Zielińska", "22222222222", "Gdańsk");
        Patient saved = patientRepository.saveAndFlush(p);

        mockMvc.perform(delete("/patients/" + saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/patients/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenDeletingNonExistingPatient() throws Exception {
        mockMvc.perform(delete("/patients/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Patient with ID: 999 not found."));
    }

    @Test
    void shouldGetPatientsListWithoutAddressAndPesel() throws Exception {
        patientRepository.saveAndFlush(
                new Patient("Anna", "Nowak", "11111111111", "Warszawa"));
        patientRepository.saveAndFlush(
                new Patient("Jan", "Kowalski", "22222222222", "Kraków"));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].firstName", hasItems("Anna", "Jan")))
                .andExpect(jsonPath("$[*].lastName", hasItems("Nowak", "Kowalski")))
                .andExpect(jsonPath("$[*].address").doesNotExist())
                .andExpect(jsonPath("$[*].pesel").doesNotExist());
    }

    @Test
    void shouldGetPatientByIdWithAddressAndPesel() throws Exception {
        Patient p = new Patient("Anna", "Nowak", "33333333333", "Warszawa");
        Patient saved = patientRepository.saveAndFlush(p);

        mockMvc.perform(get("/patients/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Nowak"))
                .andExpect(jsonPath("$.address").value("Warszawa"))
                .andExpect(jsonPath("$.pesel").value("33333333333"));
    }
}
