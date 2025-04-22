package com.boardgame.exceptions.yathzee;

public class YathzeeBonusNotFoundException extends Exception {
    public YathzeeBonusNotFoundException(String message) {
        super(message);
    }
    public YathzeeBonusNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
