package com.clouway.bank.adapter.persistence;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.Transaction;
import com.clouway.bank.core.TransactionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserRepository;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentAccountRepository implements UserRepository, AccountRepository {
  private final Provider<MongoDatabase> db;
  private final TransactionRepository transactionRepository;

  @Inject
  public PersistentAccountRepository(Provider<MongoDatabase> db, TransactionRepository transactionRepository) {
    this.db = db;
    this.transactionRepository = transactionRepository;
  }

  @Override
  public User register(String name, String password) {
    Document document = new Document();
    document.put("name", name);
    document.put("password", password);
    document.put("balance", 0d);

    accounts().insertOne(document);
    ObjectId id = (ObjectId) document.get("_id");
    return new User(id.toHexString(), name, password);
  }

  @Override
  public Optional<Account> findAccountByID(String id) {
    Document document = accounts().find(new Document("_id", new ObjectId(id))).first();
    if (document != null) {
      return Optional.of((new Account(
              document.getObjectId("_id").toHexString(),
              document.getString("name"),
              document.getDouble("balance"))));
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> findByUserName(String name) {
    Document document = accounts().find(new Document("name", name)).first();
    if (document != null) {
      return Optional.of((new User(
              document.getObjectId("_id").toHexString(),
              document.getString("name"),
              document.getString("password"))));
    }
    return Optional.empty();
  }

  @Override
  public void update(String id, Double amount, String operationType, String operationAmount) {
    transactionRepository.registerTransaction(new Transaction(
            null,
            Date.from(Instant.now()),
            id,
            operationType,
            Double.valueOf(operationAmount)
    ));

    accounts().
            findOneAndUpdate(new Document("_id", new ObjectId(id)), new Document("$set", new Document("balance", amount)));
  }

  private MongoCollection<Document> accounts() {
    return db.get().getCollection("accounts");
  }
}
