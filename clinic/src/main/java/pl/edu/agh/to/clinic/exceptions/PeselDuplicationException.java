package pl.edu.agh.to.clinic.exceptions;

public class PeselDuplicationException extends Exception {
    public PeselDuplicationException(String pesel) {
        super("Person with PESEL: " + pesel + "already exists.");
    }
}
