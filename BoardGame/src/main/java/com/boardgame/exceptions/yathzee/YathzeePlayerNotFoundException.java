package com.boardgame.exceptions.yathzee;

public class YathzeePlayerNotFoundException extends Exception {
    public YathzeePlayerNotFoundException(String message) {
        super(message);
    }
    public YathzeePlayerNotFoundException(String message, Throwable cause) { super(message, cause); }
}
