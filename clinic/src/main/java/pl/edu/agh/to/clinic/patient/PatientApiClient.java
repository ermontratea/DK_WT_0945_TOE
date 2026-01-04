package pl.edu.agh.to.clinic.patient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PatientNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PatientApiClient {
    public static final String BASE_URL = "http://localhost:8080/patients";
    private final HttpClient client=HttpClient.newHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    // GET PATIENT LIST
    public List<Patient> getPatients() throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Server returned error: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<Patient>>(){});
    }

    // GET ONE PATIENT BY ID
    public Patient getPatientById(long id) throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            throw new PatientNotFoundException(id);
        }

        return mapper.readValue(response.body(),Patient.class);
    }

    // ADD ONE PATIENT
    public Patient addPatient(Patient patient) throws InterruptedException, IOException {
        String json=mapper.writeValueAsString(patient);
        HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 409) {
            throw new PeselDuplicationException(patient.getPesel());
        }else if (response.statusCode() == 400) {
            throw new RuntimeException(response.body());
        }
        return mapper.readValue(response.body(),Patient.class);
    }

    // DELETE ONE PATIENT BY ID
    public void deletePatientById(long id) throws InterruptedException, IOException {
        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            throw new DoctorNotFoundException(id);
        }
    }
}
