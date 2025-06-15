package com.boardgame.exceptions.lobby;

public class LobbyFullException extends Exception{
    public LobbyFullException(String message) {
        super(message);
    }
    public LobbyFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
