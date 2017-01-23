package com.clouway.bank.adapter.persistence;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentAccountRepository implements AccountRepository {
  private final Provider<MongoDatabase> db;

  @Inject
  public PersistentAccountRepository(Provider<MongoDatabase> db) {
    this.db = db;
  }

  @Override
  public Account register(String name, Double initialBalance) {
    Document document = new Document();
    document.put("name", name);
    document.put("balance", initialBalance);
    accounts().insertOne(document);
    ObjectId id = (ObjectId) document.get("_id");
    return new Account(id.toHexString(), name, initialBalance);
  }

  @Override
  public Optional<Account> findUserAccount(String id) {
    MongoCursor<Document> cursor = accounts().find(new Document("_id", new ObjectId(id))).iterator();
    if (cursor.hasNext()) {
      Document document = cursor.next();
      return Optional.of((new Account(
              document.getObjectId("_id").toHexString(),
              document.getString("name"),
              document.getDouble("balance"))));
    }
    return Optional.empty();
  }

  @Override
  public void update(String id, Double amount) {
    accounts().
            findOneAndUpdate(new Document("_id", new ObjectId(id)), new Document("$set", new Document("balance", amount)));
  }

  private MongoCollection<Document> accounts() {
    return db.get().getCollection("accounts");
  }
}
