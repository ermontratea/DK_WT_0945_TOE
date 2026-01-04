package pl.edu.agh.to.clinic.exceptions;

public class DutyNotFoundException extends RuntimeException {
    public DutyNotFoundException(Long id) {
        super("Duty with ID: " + id + " not found.");
    }
}
