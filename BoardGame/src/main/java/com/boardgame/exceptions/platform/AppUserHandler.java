package com.boardgame.exceptions.platform;

import com.boardgame.dto.ErrorResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Primary
public class AppUserHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception ex, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now());
        error.setHttpStatus(status.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        return createErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            InvalidLoginException.class,
            InvalidCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(Exception ex) {
        return createErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
