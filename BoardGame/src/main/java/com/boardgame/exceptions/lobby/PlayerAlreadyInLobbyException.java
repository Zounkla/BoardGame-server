package com.boardgame.exceptions.lobby;

public class PlayerAlreadyInLobbyException extends Exception {
    public PlayerAlreadyInLobbyException(String message) {
        super(message);
    }
    public PlayerAlreadyInLobbyException(String message, Throwable cause) { super(message, cause); }
}
