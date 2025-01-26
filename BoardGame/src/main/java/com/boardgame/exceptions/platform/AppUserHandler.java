package com.boardgame.exceptions.platform;

import com.boardgame.entity.platform.Error.ErrorEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class AppUserHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ErrorEntity> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.CONFLICT.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(error);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ErrorEntity> handleInvalidLoginException(InvalidLoginException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.UNAUTHORIZED.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(error);
    }
}
