package com.clouway.bank.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class Session {
  private final String sessionId;
  private final int durationInMinutes;
  private final String username;

  public Session(String sessionId, int durationInMinutes, String username) {
    this.sessionId = sessionId;
    this.durationInMinutes = durationInMinutes;
    this.username = username;
  }

  public String id() {
    return sessionId;
  }

  public int durationInSeconds() {
    return durationInMinutes * 60;
  }

  public String getUsername() {
    return username;
  }
}
