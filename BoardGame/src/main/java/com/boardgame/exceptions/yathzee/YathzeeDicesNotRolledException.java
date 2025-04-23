package com.boardgame.exceptions.yathzee;

public class YathzeeDicesNotRolledException extends Exception {
    public YathzeeDicesNotRolledException(String message) {
        super(message);
    }
    public YathzeeDicesNotRolledException(String message, Throwable cause) {
      super(message, cause);
    }
}
