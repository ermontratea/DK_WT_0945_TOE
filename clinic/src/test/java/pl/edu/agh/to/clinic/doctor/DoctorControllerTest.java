package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

    @BeforeEach
    void setup() {
        doctorRepository.deleteAll();
    }

    @Test
    void shouldAddDoctor() throws Exception {
        String json = """
        {
            "firstName": "Jan",
            "lastName": "Kowalski",
            "pesel": "12345678901",
            "specialization": "CARDIOLOGY",
            "address": "Kraków"
        }
        """;

        mockMvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.pesel").value("12345678901"));
    }

    @Test
    void shouldGetDoctorById() throws Exception {
        Doctor doc = new Doctor("Anna", "Nowak", "11111111111", Specialization.DERMATOLOGY, "Warszawa");
        doctorRepository.save(doc);

        mockMvc.perform(get("/doctors/" + doc.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Nowak"));
    }

}
