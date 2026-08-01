package pl.klejczyk.tpm.machine.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.klejczyk.tpm.machine.domain.MachineNotFound;
import pl.klejczyk.tpm.machine.domain.UnauthorizedOperation;

import java.util.Map;

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(MachineNotFound.class)
    ResponseEntity<Map<String, String>> notFound(MachineNotFound exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(UnauthorizedOperation.class)
    ResponseEntity<Map<String, String>> forbidden(UnauthorizedOperation exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> unprocessable(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("error", exception.getMessage()));
    }
}
