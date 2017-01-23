package com.clouway.bank.adapter.persistence;

import com.google.inject.Provider;
import com.google.inject.util.Providers;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.rules.ExternalResource;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class DatastoreRule extends ExternalResource {
  private String collection = "";

  public DatastoreRule(String collection) {
    this.collection = collection;
  }

  public Provider<MongoDatabase> getDatabase() {
    return Providers.of(new MongoClient("localhost", 27017).getDatabase("testBankApp"));
  }

  @Override
  protected void before() throws Throwable {
    getDatabase().get().getCollection(collection).drop();
  }

  @Override
  protected void after() {
    super.after();
  }
}
