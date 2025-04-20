package com.boardgame.exceptions.yathzee;

public class YathzeeRollsException extends Exception {
    public YathzeeRollsException(String message) {
        super(message);
    }
    public YathzeeRollsException(String message, Throwable cause) { super(message, cause); }
}
