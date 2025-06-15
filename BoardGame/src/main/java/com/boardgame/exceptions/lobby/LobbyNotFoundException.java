package com.boardgame.exceptions.lobby;

public class LobbyNotFoundException extends Exception {
  public LobbyNotFoundException(String message) {
    super(message);
  }
  public LobbyNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}