package pl.edu.agh.to.clinic.office;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.edu.agh.to.clinic.exceptions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OfficeApiClient{
    public static final String BASE_URL = "http://localhost:8080/offices";
    private final HttpClient client=HttpClient.newHttpClient();
    ObjectMapper mapper;

    public OfficeApiClient() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    // GET OFFICE LIST
    public List<Office> getOffices() throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Server returned error: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<Office>>(){});
    }

    // GET ONE OFFICE BY ID
    public Office getOfficeById(long id) throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            throw new PatientNotFoundException(id);
        }

        return mapper.readValue(response.body(),Office.class);
    }

    // ADD ONE OFFICE
    public Office addOffice(Office office) throws InterruptedException, IOException {
        String json=mapper.writeValueAsString(office);
        HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 409) {
            throw new RoomNumberDuplicationException(office.getRoomNumber());
        }else if (response.statusCode() == 400) {
            throw new RuntimeException(response.body());
        }
        return mapper.readValue(response.body(),Office.class);
    }

    // DELETE ONE OFFICE BY ID
    public void deleteOfficeById(long id) throws InterruptedException, IOException {
        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            throw new OfficeNotFoundException(id);
        } else if (response.statusCode()==409) {
            throw new IllegalStateException("You can't delete an office with assigned duties ");
        }
    }
}
