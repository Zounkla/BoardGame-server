package com.boardgame.exceptions.lightsout;

public class LightsoutGameOverException extends Exception {
    public LightsoutGameOverException(String message) {
        super(message);
    }
    public LightsoutGameOverException(String message, Throwable cause) {
        super(message, cause);
    }
}
