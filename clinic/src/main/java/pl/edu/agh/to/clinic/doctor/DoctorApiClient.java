package pl.edu.agh.to.clinic.doctor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DoctorApiClient {
    public static final String BASE_URL = "http://localhost:8080/doctors";
    private final HttpClient client=HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    // GET DOCTOR LIST
    public List<Doctor> getDoctors() throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), new TypeReference<List<Doctor>>(){});
    }

    // GET ONE DOCTOR BY ID
    public Doctor getDoctorById(long id) throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(),Doctor.class);
    }

    // ADD ONE DOCTOR
    public Doctor addDoctor(Doctor doctor) throws InterruptedException, IOException {
        String json=mapper.writeValueAsString(doctor);
        HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(),Doctor.class);
    }

    // DELETE ONE DOCTOR BY ID
    public void deleteDoctorById(long id) throws InterruptedException, IOException {
        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).DELETE().build();
        client.send(request,HttpResponse.BodyHandlers.discarding());
    }


}
