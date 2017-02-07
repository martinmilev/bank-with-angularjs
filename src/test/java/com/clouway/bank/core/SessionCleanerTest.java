package com.clouway.bank.core;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SessionCleanerTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private SessionRepository sessions = context.mock(SessionRepository.class);

  @Test
  public void happyPath() throws Exception {
    context.checking(new Expectations() {{
      oneOf(sessions).clearExpiredSessions(with(any(LocalDateTime.class)));
    }});

    new SessionCleaner(sessions, 1).runOneIteration();
  }
}