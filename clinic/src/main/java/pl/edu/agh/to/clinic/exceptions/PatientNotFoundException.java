package pl.edu.agh.to.clinic.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) {
        super("Patient with ID: " + id + " not found.");
    }
}
