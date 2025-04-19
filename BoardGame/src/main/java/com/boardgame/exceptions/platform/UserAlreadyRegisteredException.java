package com.boardgame.exceptions.platform;

public class UserAlreadyRegisteredException extends Exception {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
    public UserAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
