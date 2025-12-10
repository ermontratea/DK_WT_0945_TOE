package pl.edu.agh.to.clinic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class PeselDuplicationExceptionHandler {
//    @ExceptionHandler(PeselDuplicationException.class)
//    public ResponseEntity<String> handlePeselDuplication(PeselDuplicationException e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//    }
    @ExceptionHandler(PeselDuplicationException.class)
    public ResponseEntity<Map<String, String>> handlePeselDuplication(PeselDuplicationException e) {

    Map<String, String> body = new HashMap<>();
    body.put("error", e.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
}
}
