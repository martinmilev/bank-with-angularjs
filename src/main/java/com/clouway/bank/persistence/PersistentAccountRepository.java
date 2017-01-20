package com.clouway.bank.persistence;

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
  private Provider<MongoDatabase> db;

  @Inject
  public PersistentAccountRepository(Provider<MongoDatabase> db) {
    this.db = db;
  }

  @Override
  public Account register(String name, Double balance) {
    MongoCollection<Document> collection = db.get().getCollection("accounts");
    Document document = new Document();
    document.put("name", name);
    document.put("balance", balance);
    collection.insertOne(document);
    ObjectId id = (ObjectId) document.get("_id");
    return new Account(id.toHexString(), name, balance);
  }

  @Override
  public Optional<Account> getById(String id) {
    MongoCollection<Document> collection = db.get().getCollection("accounts");
    MongoCursor<Document> cursor = collection.find(new Document("_id", new ObjectId(id))).iterator();
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
    MongoCollection<Document> collection = db.get().getCollection("accounts");
    collection.findOneAndUpdate(new Document("_id", new ObjectId(id)), new Document("$set", new Document("balance", amount)));
  }
}
