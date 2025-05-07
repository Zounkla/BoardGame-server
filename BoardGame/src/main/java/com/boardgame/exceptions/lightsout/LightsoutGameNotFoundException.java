package com.boardgame.exceptions.lightsout;

public class LightsoutGameNotFoundException extends Exception {
    public LightsoutGameNotFoundException(String message) {
        super(message);
    }
    public LightsoutGameNotFoundException(String message, Throwable cause) { super(message, cause);}
}
