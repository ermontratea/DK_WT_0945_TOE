package pl.edu.agh.to.clinic.duty;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.edu.agh.to.clinic.exceptions.DutyNotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DutyApiClient {
    private static final String BASE_URL = "http://localhost:8080/duties";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public DutyApiClient() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }
    public Duty getDutyById(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            throw new DutyNotFoundException(id);
        } else if (response.statusCode() >= 400) {
            throw new RuntimeException("Server returned error: " + response.statusCode());
        }

        return mapper.readValue(response.body(), Duty.class);
    }

    public List<Duty> getDuties() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public Duty addDuty(Duty duty) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(duty);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 409) {
            throw new RuntimeException("Conflict: " + response.body());
        } else if (response.statusCode() >= 400) {
            throw new RuntimeException("Server error: " + response.statusCode());
        }

        return mapper.readValue(response.body(), Duty.class);
    }

    public void deleteDuty(long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}