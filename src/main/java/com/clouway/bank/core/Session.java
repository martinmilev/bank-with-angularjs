package com.clouway.bank.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class Session {
  private final String sessionId;
  private int durationInMinutes;

  public Session(String sessionId, int durationInMinutes) {
    this.sessionId = sessionId;
    this.durationInMinutes = durationInMinutes;
  }

  public String id() {
    return sessionId;
  }

  public int durationInSeconds() {
    return durationInMinutes * 60;
  }
}
