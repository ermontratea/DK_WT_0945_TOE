package pl.edu.agh.to.clinic.exceptions;

public class RoomNumberDuplicationException extends RuntimeException {
    public RoomNumberDuplicationException(int roomNumber) {
        super("Office with room number: " + roomNumber + " already exists.");
    }
}
