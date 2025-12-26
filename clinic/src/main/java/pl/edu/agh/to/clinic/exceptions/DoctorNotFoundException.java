package pl.edu.agh.to.clinic.exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Long id) {
        super("Doctor with ID: " + id + " not found.");
    }
}
