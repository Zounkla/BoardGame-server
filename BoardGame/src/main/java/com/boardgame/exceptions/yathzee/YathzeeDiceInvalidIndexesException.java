package com.boardgame.exceptions.yathzee;

public class YathzeeDiceInvalidIndexesException extends Exception{
    public YathzeeDiceInvalidIndexesException(String message) {
        super(message);
    }
    public YathzeeDiceInvalidIndexesException(String message, Throwable cause) {
        super(message, cause);
    }
}
