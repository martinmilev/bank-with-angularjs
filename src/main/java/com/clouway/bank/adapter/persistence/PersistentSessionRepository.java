package com.clouway.bank.adapter.persistence;

import com.clouway.bank.core.SessionRepository;
import com.google.common.io.BaseEncoding;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.clouway.bank.core.Session;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentSessionRepository implements SessionRepository {
  private final Provider<MongoDatabase> db;
  private final int durationInMinutes = 30;

  @Inject
  public PersistentSessionRepository(Provider<MongoDatabase> db) {
    this.db = db;
  }

  @Override
  public Session startSession(LocalDateTime instant) {
    Date creationDate = Date.from(instant.atZone(ZoneId.systemDefault()).toInstant());
    String sessionId = BaseEncoding.base64().encode(UUID.randomUUID().toString().getBytes());
    db.get().getCollection("sessions").insertOne(
            new Document("_id", sessionId).append("createdOn", creationDate)
    );

    return new Session(sessionId, durationInMinutes);
  }

  @Override
  public Optional<Session> findSessionAvailableAt(String sessionId, LocalDateTime instant) {
    Date now = Date.from(instant.atZone(ZoneId.systemDefault()).toInstant());

    Document document = db.get().getCollection("sessions")
            .find(new Document("_id", sessionId)).first();
    if (document == null) {
      return Optional.empty();
    }
    Date createdOn = document.getDate("createdOn");
    int seconds = durationInMinutes * 60;
    int milliseconds = seconds * 1000;

    Date expirationTime = new Date(createdOn.getTime() + milliseconds);
    if (expirationTime.before(now)) {
      return Optional.empty();
    }

    return Optional.of(new Session(document.getString("_id"), durationInMinutes));

  }

  @Override
  public void clearExpiredSessions(LocalDateTime instant) {
    Date now = Date.from(instant.atZone(ZoneId.systemDefault()).toInstant());
    Date expTime = new Date(now.getTime() - (durationInMinutes * 6000));
    db.get().getCollection("sessions")
            .deleteMany(new Document("createdOn", new Document("$lte", expTime)));
  }
}