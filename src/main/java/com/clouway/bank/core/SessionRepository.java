package com.clouway.bank.core;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface SessionRepository {

  Session startSession(LocalDateTime instant, String username);

  Optional<Session> findSessionAvailableAt(String sessionId, LocalDateTime instant);

  void clearExpiredSessions(LocalDateTime instant);
}