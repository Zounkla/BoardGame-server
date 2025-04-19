package com.boardgame.exceptions.yathzee;

public class GameStartedException extends Exception {
    public GameStartedException(String message) {
        super(message);
    }
    public GameStartedException(String message, Throwable cause) {
        super(message, cause);
    }
}
