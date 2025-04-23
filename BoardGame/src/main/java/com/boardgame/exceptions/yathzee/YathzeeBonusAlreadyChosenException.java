package com.boardgame.exceptions.yathzee;

public class YathzeeBonusAlreadyChosenException extends Exception {
    public YathzeeBonusAlreadyChosenException(String message) {
        super(message);
    }
    public YathzeeBonusAlreadyChosenException(String message, Throwable cause) {
        super(message, cause);
    }

}
