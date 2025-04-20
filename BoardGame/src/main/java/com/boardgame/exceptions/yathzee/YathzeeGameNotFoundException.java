package com.boardgame.exceptions.yathzee;

public class YathzeeGameNotFoundException extends Exception {
    public YathzeeGameNotFoundException(String message) {
        super(message);
    }
    public YathzeeGameNotFoundException(String message, Throwable cause) { super(message, cause); }
}
