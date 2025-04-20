package com.boardgame.exceptions.yathzee;

public class YathzeeActivePlayerException extends Exception {
    public YathzeeActivePlayerException(String message) {
        super(message);
    }
    public YathzeeActivePlayerException(String message, Throwable cause) { super(message, cause); }
}
