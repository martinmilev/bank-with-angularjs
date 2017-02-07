package com.clouway.bank.core;

import java.time.LocalDateTime;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SessionsCleaner implements Runnable {
  private final SessionRepository sessionRepository;

  public SessionsCleaner(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override
  public void run() {
    sessionRepository.clearExpiredSessions(LocalDateTime.now());

  }
}
