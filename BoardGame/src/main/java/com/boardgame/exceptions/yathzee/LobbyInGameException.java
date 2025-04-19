package com.boardgame.exceptions.yathzee;

public class LobbyInGameException extends Exception {
    public LobbyInGameException(String message) {
        super(message);
    }
  public LobbyInGameException(String message, Throwable cause) {
    super(message, cause);
  }
}
