package com.clouway.bank.core;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SessionCleaner extends AbstractScheduledService {
  private final SessionRepository sessionRepository;
  private final Integer initTime;

  @Inject
  public SessionCleaner(SessionRepository sessionRepository, Integer initTime) {
    this.sessionRepository = sessionRepository;
    this.initTime = initTime;
  }

  @Override
  protected void runOneIteration() throws Exception {
    sessionRepository.clearExpiredSessions(LocalDateTime.now());
  }

  @Override
  protected Scheduler scheduler() {
    return Scheduler.newFixedRateSchedule(0, initTime, TimeUnit.SECONDS);
  }
}
