package pl.edu.agh.to.clinic.office;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.to.clinic.exceptions.GlobalExceptionHandler;
import pl.edu.agh.to.clinic.exceptions.OfficeNotFoundException;
import pl.edu.agh.to.clinic.exceptions.RoomNumberDuplicationException;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OfficeController.class)
@Import(GlobalExceptionHandler.class)
class OfficeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfficeService officeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllOffices() throws Exception {
        OfficeDto o1 = new OfficeDto();
        o1.setId(1L);
        o1.setRoomNumber(101);

        OfficeDto o2 = new OfficeDto();
        o2.setId(2L);
        o2.setRoomNumber(102);

        when(officeService.getOffices()).thenReturn(List.of(o1, o2));

        mockMvc.perform(get("/offices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].roomNumber", is(101)))
                .andExpect(jsonPath("$[1].roomNumber", is(102)));
    }

    @Test
    void shouldGetOfficeById() throws Exception {
        OfficeDto dto = new OfficeDto();
        dto.setId(1L);
        dto.setRoomNumber(101);

        when(officeService.getOfficeById(1L)).thenReturn(dto);

        mockMvc.perform(get("/offices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.roomNumber", is(101)));
    }

    @Test
    void shouldReturnNotFoundWhenOfficeMissing() throws Exception {
        when(officeService.getOfficeById(999L))
                .thenThrow(new OfficeNotFoundException(999L));

        mockMvc.perform(get("/offices/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Office with ID: 999 not found."));
    }

    @Test
    void shouldCreateOffice() throws Exception {
        OfficeDto request = new OfficeDto();
        request.setRoomNumber(101);

        OfficeDto response = new OfficeDto();
        response.setId(5L);
        response.setRoomNumber(101);

        when(officeService.addOffice(any(OfficeDto.class))).thenReturn(response);

        mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.roomNumber", is(101)));
    }

    @Test
    void shouldReturnConflictWhenRoomNumberDuplicated() throws Exception {
        OfficeDto request = new OfficeDto();
        request.setRoomNumber(101);

        when(officeService.addOffice(any(OfficeDto.class)))
                .thenThrow(new RoomNumberDuplicationException(101));

        mockMvc.perform(post("/offices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("Office with room number: 101 already exists."));
    }

    @Test
    void shouldDeleteOffice() throws Exception {
        mockMvc.perform(delete("/offices/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingOffice() throws Exception {
        doThrow(new OfficeNotFoundException(999L))
                .when(officeService).deleteOfficeById(999L);

        mockMvc.perform(delete("/offices/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Office with ID: 999 not found."));
    }
}
