package com.marketplace.marketplace.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();
        List<String> errorsMessages = errors.stream()
                .map(
                        DefaultMessageSourceResolvable::getDefaultMessage
                ).toList();

        HashMap<String, String> errorsMap = new HashMap<>();
        for (String errorMessage : errorsMessages) {
            errorsMap.put("error", errorMessage);
        }

        return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsExceptions(BadCredentialsException exception) {
        return new ResponseEntity<>("Bad User Credentials", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DisabledException.class)
    public ResponseEntity<String> handleDisabledException() {
        return new ResponseEntity<>("User account is not enabled yet", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = InvalidOperationException.class)
    public ResponseEntity<String> handleInvalidOperationException(InvalidOperationException exception) {
        return new ResponseEntity<>(
                String.format("[Operation not allowed] %s", exception.getMessage()),
                HttpStatus.NOT_ACCEPTABLE
        );
    }

    @ExceptionHandler(value = {
            UserNotFoundException.class,
            ItemNotFoundException.class,
            ImageNotFoundException.class
    })
    public ResponseEntity<String> handleNotFoundException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EncryptionException.class)
    public ResponseEntity<String> handleEncryptionException(EncryptionException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
