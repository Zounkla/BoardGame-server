package com.boardgame.exceptions.lobby;

public class LobbyAlreadyExistsException extends Exception {
    public LobbyAlreadyExistsException(String message) {
        super(message);
    }
    public LobbyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}