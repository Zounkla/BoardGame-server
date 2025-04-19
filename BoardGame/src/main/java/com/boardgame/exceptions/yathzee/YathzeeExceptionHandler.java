package com.boardgame.exceptions.yathzee;

import com.boardgame.entity.platform.Error.ErrorEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class YathzeeExceptionHandler {

    @ExceptionHandler(LobbyNotFoundException.class)
    public ResponseEntity<ErrorEntity> handleLobbyNotFoundException(LobbyNotFoundException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(LobbyInGameException.class)
    public ResponseEntity<ErrorEntity> handleLobbyInGameException(LobbyInGameException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PlayerAlreadyInLobbyException.class)
    public ResponseEntity<ErrorEntity> handlePlayerAlreadyInLobbyException(PlayerAlreadyInLobbyException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(LobbyFullException.class)
    public ResponseEntity<ErrorEntity> handleLobbyFullException(LobbyFullException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(GameStartedException.class)
    public ResponseEntity<ErrorEntity> handleGameStartedException(GameStartedException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(LobbyAlreadyExistsException.class)
    public ResponseEntity<ErrorEntity> handleLobbyAlreadyExistsException(LobbyAlreadyExistsException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotEnoughPlayerException.class)
    public ResponseEntity<ErrorEntity> handleNotEnoughPlayerException(NotEnoughPlayerException ex) {
        ErrorEntity error = new ErrorEntity(LocalDateTime.now());
        error.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
