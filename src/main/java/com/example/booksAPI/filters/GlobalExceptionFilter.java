package com.example.booksAPI.filters;

import com.example.booksAPI.dto.ErrorResponse;
import com.example.booksAPI.exceptions.BadRequestException;
import com.example.booksAPI.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionFilter {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(Exception ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage(), "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class,
            ConstraintViolationException.class, AuthenticationException.class})
    public ResponseEntity<?> handleBadRequestException(Exception ex) {
        switch (ex) {
            case MethodArgumentNotValidException methodArgumentNotValidException -> {
                Map<String, String> errors = new HashMap<>();
                methodArgumentNotValidException.getBindingResult().getAllErrors().forEach(error -> {
                    String field = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    errors.put(field, message);
                });
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            case ConstraintViolationException constraintViolationException -> {
                String[] messages = ex.getMessage().split(",");
                HashMap<String, List<String>> errors = new HashMap<>();
                errors.put("messages", new ArrayList<>());
                for (String msg : messages) {
                    String error = msg.split(":")[1].strip();
                    errors.get("messages").add(error);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            }
            default -> {
                ErrorResponse response = new ErrorResponse(ex.getMessage(), "400");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
    }


}
