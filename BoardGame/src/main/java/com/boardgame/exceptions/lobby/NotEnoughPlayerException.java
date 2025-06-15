package com.boardgame.exceptions.lobby;

public class NotEnoughPlayerException extends Exception {
    public NotEnoughPlayerException(String message) {
        super(message);
    }
    public NotEnoughPlayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
