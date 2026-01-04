package pl.edu.agh.to.clinic.exceptions;

public class OfficeNotFoundException extends RuntimeException {
    public OfficeNotFoundException(Long id) {
        super("Office with ID: " + id + " not found.");
    }
}
