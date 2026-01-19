package pl.edu.agh.to.clinic.doctor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.edu.agh.to.clinic.exceptions.DoctorNotFoundException;
import pl.edu.agh.to.clinic.exceptions.PeselDuplicationException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DoctorApiClient {
    public static final String BASE_URL = "http://localhost:8080/doctors";
    private final HttpClient client=HttpClient.newHttpClient();
    ObjectMapper mapper;

    public DoctorApiClient() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        //this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // GET DOCTOR LIST
    public List<DoctorListDto> getDoctors() throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new RuntimeException("Server returned error: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<DoctorListDto>>(){});
    }

    // GET ONE DOCTOR BY ID
    public DoctorListDto getDoctorById(long id) throws InterruptedException, IOException {

        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).GET().build();

        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            throw new DoctorNotFoundException(id);
        }

        return mapper.readValue(response.body(),DoctorListDto.class);
    }

    // ADD ONE DOCTOR
    public DoctorDto addDoctor(DoctorDto doctor) throws InterruptedException, IOException {
        String json=mapper.writeValueAsString(doctor);
        HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response=client.send(request,HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 409) {
            throw new PeselDuplicationException(doctor.getPesel());
        }else if (response.statusCode() == 400) {
            throw new RuntimeException(response.body());
        }
        return mapper.readValue(response.body(),DoctorDto.class);
    }

    // DELETE ONE DOCTOR BY ID
    public void deleteDoctorById(long id) throws InterruptedException, IOException {
        HttpRequest request= HttpRequest.newBuilder(URI.create(BASE_URL + "/" + id)).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            throw new DoctorNotFoundException(id);
        }else if(response.statusCode()==409){
            throw new RuntimeException(response.body());
        }
    }

    // GET DOCTOR LIST BY SPECIALIZATION
    public List<DoctorListDto> getDoctorsBySpecialization(Specialization specialization) throws InterruptedException, IOException {

        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + "?specialization=" + specialization.name())).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Server returned error: " + response.statusCode());
        }

        return mapper.readValue(response.body(), new TypeReference<List<DoctorListDto>>() {});
    }


}
