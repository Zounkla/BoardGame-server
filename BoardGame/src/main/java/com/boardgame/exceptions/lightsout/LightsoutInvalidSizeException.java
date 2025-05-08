package com.boardgame.exceptions.lightsout;

public class LightsoutInvalidSizeException extends Exception {
    public LightsoutInvalidSizeException(String message) {
        super(message);
    }
    public LightsoutInvalidSizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
