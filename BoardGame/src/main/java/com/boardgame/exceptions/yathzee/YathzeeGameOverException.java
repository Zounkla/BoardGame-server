package com.boardgame.exceptions.yathzee;

public class YathzeeGameOverException extends Exception {
    public YathzeeGameOverException(String message) {
        super(message);
    }
    public YathzeeGameOverException(String message, Throwable cause) {
        super(message, cause);
    }
}
