package pl.edu.agh.to.clinic.office;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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
class OfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldAddOffice() throws Exception {
        String json = """
        { "roomNumber": 101 }
        """;

        mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.roomNumber").value(101));
    }

    @Test
    void shouldReturnErrorWhenRoomNumberExists() throws Exception {
        String json = """
        { "roomNumber": 200 }
        """;

        mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Office with room number: 200 already exists."));
    }

    @Test
    void shouldGetOfficesList() throws Exception {
        String json1 = "{ \"roomNumber\": 300 }";
        String json2 = "{ \"roomNumber\": 301 }";

        mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON).content(json1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON).content(json2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/offices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].roomNumber", hasItems(300, 301)))
                .andExpect(jsonPath("$[*].duties").doesNotExist());
    }

    @Test
    void shouldGetOfficeById() throws Exception {
        String json = "{ \"roomNumber\": 400 }";

        String body = mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long id = mapper.readTree(body).get("id").asLong();

        mockMvc.perform(get("/offices/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value(400));
    }

    @Test
    void shouldReturnErrorForNonExistingOffice() throws Exception {
        mockMvc.perform(get("/offices/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Office with ID: 999 not found."));
    }

    @Test
    void shouldDeleteOffice() throws Exception {
        String json = "{ \"roomNumber\": 500 }";

        String body = mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long id = mapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/offices/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/offices/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenDeletingNonExistingOffice() throws Exception {
        mockMvc.perform(delete("/offices/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Office with ID: 999 not found."));
    }
}
