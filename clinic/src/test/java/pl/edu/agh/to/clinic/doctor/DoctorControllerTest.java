package pl.edu.agh.to.clinic.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.*;

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
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"))
                .andExpect(jsonPath("$.pesel").value("12345678901"))
                .andExpect(jsonPath("$.specialization").value("CARDIOLOGY"))
                .andExpect(jsonPath("$.address").value("Kraków"));
    }


    @Test
    void shouldReturnErrorWhenPeselExists() throws Exception {
        Doctor doc = new Doctor("Jan", "Nowak", "12345678901", Specialization.CARDIOLOGY, "Warszawa");
        doctorRepository.saveAndFlush(doc);

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
        Doctor saved = doctorRepository.saveAndFlush(doc);

        mockMvc.perform(delete("/doctors/" + saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/doctors/" + saved.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetDoctorsListWithoutAddressField() throws Exception {
        Doctor d1 = new Doctor("Anna", "Nowak", "11111111111", Specialization.DERMATOLOGY, "Warszawa");
        Doctor d2 = new Doctor("Jan", "Kowalski", "22222222222", Specialization.CARDIOLOGY, "Kraków");
        doctorRepository.saveAndFlush(d1);
        doctorRepository.saveAndFlush(d2);

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].firstName", hasItems("Anna", "Jan")))
                .andExpect(jsonPath("$[*].lastName", hasItems("Nowak", "Kowalski")))
                .andExpect(jsonPath("$[*].specialization", hasItems("DERMATOLOGY", "CARDIOLOGY")))
                .andExpect(jsonPath("$[*].address").doesNotExist());
    }

    @Test
    void shouldGetDoctorByIdWithAddressBecauseOfDetailsView() throws Exception {
        Doctor doc = new Doctor("Anna", "Nowak", "33333333333", Specialization.DERMATOLOGY, "Warszawa");
        Doctor saved = doctorRepository.saveAndFlush(doc);

        mockMvc.perform(get("/doctors/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Nowak"))
                .andExpect(jsonPath("$.address").value("Warszawa"));
    }

    @Test
    void shouldReturnErrorWhenDeletingNonExistingDoctor() throws Exception {
        mockMvc.perform(delete("/doctors/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Doctor with id: 999 not found"));
    }
}
