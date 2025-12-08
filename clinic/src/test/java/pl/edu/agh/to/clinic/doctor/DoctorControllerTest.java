package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorRepository doctorRepository;

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

    @Test
    void shouldReturnErrorWhenPeselExists() throws Exception {
        Doctor doc = new Doctor("Jan", "Nowak", "12345678901", Specialization.CARDIOLOGY, "Warszawa");
        doctorRepository.save(doc);

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
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Doctor with this pesel already exists"));
    }

    @Test
    void shouldReturnErrorForNonExistingDoctor() throws Exception {
        mockMvc.perform(get("/doctors/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Doctor with id: 999 not found"));
    }

    @Test
    void shouldDeleteDoctor() throws Exception {
        Doctor doc = new Doctor("Marta", "Zielińska", "22222222222", Specialization.PEDIATRICS, "Gdańsk");
        doctorRepository.save(doc);

        mockMvc.perform(delete("/doctors/" + doc.getId()))
                .andExpect(status().isOk());
    }
}
