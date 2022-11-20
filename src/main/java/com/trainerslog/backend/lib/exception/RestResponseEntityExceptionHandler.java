package com.trainerslog.backend.lib.exception;


import com.trainerslog.backend.lib.util.ResponseBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler {

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    protected void handleSQLException() {}

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ClientException.class)
    protected void handleClientException() {}

    @ExceptionHandler(value = ResponseStatusException.class)
    protected ResponseEntity<?> handleAccessDenied() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(value = DuplicateUserRoleException.class)
    protected ResponseEntity<?> handleDuplicateUserRoleException(RuntimeException exception) {
        return ResponseBuilder.conflict(exception.getMessage());
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<?> handleUserNotFoundException(RuntimeException exception) {
        return ResponseBuilder.notFound(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseBuilder.badRequest(errors);
    }

}
