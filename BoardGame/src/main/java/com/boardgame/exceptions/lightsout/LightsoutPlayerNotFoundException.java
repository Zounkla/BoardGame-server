package com.boardgame.exceptions.lightsout;

public class LightsoutPlayerNotFoundException extends Exception {
    public LightsoutPlayerNotFoundException(String message) {
        super(message);
    }
  public LightsoutPlayerNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
