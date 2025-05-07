package com.boardgame.exceptions.lightsout;

public class LightsoutInvalidIndexException extends Exception {
    public LightsoutInvalidIndexException(String message) {
        super(message);
    }
    public LightsoutInvalidIndexException(String message, Throwable cause) {
    super(message, cause);
  }
}
