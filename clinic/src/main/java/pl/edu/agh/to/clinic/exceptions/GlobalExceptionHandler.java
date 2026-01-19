package pl.edu.agh.to.clinic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PeselDuplicationException.class, RoomNumberDuplicationException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleConflict(Exception e) {
        Map<String, String> body = new HashMap<>();
        body.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({DoctorNotFoundException.class, OfficeNotFoundException.class, PatientNotFoundException.class, DutyNotFoundException.class, AppointmentNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(Exception e) {
        Map<String, String> body = new HashMap<>();
        body.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "\n" + msg2)
                .orElse("Validation error");

        return ResponseEntity.badRequest().body(errorMessages);
    }

}
