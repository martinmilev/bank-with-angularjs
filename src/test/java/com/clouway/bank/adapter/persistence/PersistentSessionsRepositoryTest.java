package com.clouway.bank.adapter.persistence;

import com.clouway.bank.matchers.DatastoreRule;
import com.clouway.bank.core.Session;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentSessionsRepositoryTest {
  private PersistentSessionRepository sessions;

  @Rule
  public DatastoreRule datastoreRule = new DatastoreRule();

  @Before
  public void setUp() throws Exception {
    sessions = new PersistentSessionRepository(datastoreRule.getDatabase());
  }

  @Test
  public void unknownSession() {
    Optional<Session> possibleSession = sessions.findSessionAvailableAt("::any unknown session::", LocalDateTime.of(2017, 1, 30, 9, 0));

    assertThat(possibleSession.isPresent(), is(false));
  }

  @Test
  public void findNotExpiredSession() {
    Session session = sessions.startSession(LocalDateTime.of(2017, 1, 30, 9, 0), "::any username::");
    Optional<Session> possibleSession = sessions.findSessionAvailableAt(session.id(), LocalDateTime.of(2017, 1, 30, 9, 0));

    assertThat(possibleSession.isPresent(), is(true));
    assertThat(possibleSession.get().id(), is(equalTo(session.id())));
  }

  @Test
  public void findExpiredSession() {
    Session session = sessions.startSession(LocalDateTime.of(2017, 1, 30, 9, 0), "::any username::");

    Optional<Session> possibleSession = sessions.findSessionAvailableAt(session.id(), LocalDateTime.of(2017, 1, 30, 10, 0));

    assertThat(possibleSession.isPresent(), is(false));
  }


  @Test
  public void findMultipleNotExpiredSessions() {
    Session firstSession = sessions.startSession(LocalDateTime.of(2017, 1, 30, 9, 0), "::any username::");
    Session secondSession = sessions.startSession(LocalDateTime.of(2017, 1, 30, 9, 3), "::any username::");

    Optional<Session> possibleFs = sessions.findSessionAvailableAt(firstSession.id(), LocalDateTime.of(2017, 1, 30, 9, 15));
    Optional<Session> possibleSs = sessions.findSessionAvailableAt(secondSession.id(), LocalDateTime.of(2017, 1, 30, 9, 15));

    assertThat(possibleFs.isPresent(), is(true));
    assertThat(possibleSs.isPresent(), is(true));
    assertThat(possibleFs.get().id(), is(equalTo(firstSession.id())));
    assertThat(possibleSs.get().id(), is(equalTo(secondSession.id())));
  }


  @Test
  public void clearExpiredSessions() {
    Session firstSession = sessions.startSession(LocalDateTime.of(2017, 1, 30, 9, 0), "::any username::");
    Session secondSession = sessions.startSession(LocalDateTime.of(2017, 1, 30, 9, 3), "::any username::");

    sessions.clearExpiredSessions(LocalDateTime.of(2017, 1, 30, 10, 0));

    Optional<Session> possibleFs = sessions.findSessionAvailableAt(firstSession.id(), LocalDateTime.of(2017, 1, 30, 10, 15));
    Optional<Session> possibleSs = sessions.findSessionAvailableAt(secondSession.id(), LocalDateTime.of(2017, 1, 30, 10, 15));

    assertThat(possibleFs.isPresent(), is(false));
    assertThat(possibleSs.isPresent(), is(false));
  }
}