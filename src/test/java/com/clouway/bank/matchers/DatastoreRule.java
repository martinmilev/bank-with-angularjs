package com.clouway.bank.matchers;

import com.google.inject.Provider;
import com.google.inject.util.Providers;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.rules.ExternalResource;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class DatastoreRule extends ExternalResource {
  private String[] collections;

  public DatastoreRule(String... collections) {
    this.collections = collections;
  }

  public Provider<MongoDatabase> getDatabase() {
    return Providers.of(new MongoClient("localhost", 27017).getDatabase("testBankApp"));
  }

  @Override
  protected void before() throws Throwable {
    for (String collection : collections) {
      getDatabase().get().getCollection(collection).drop();
    }
  }

  @Override
  protected void after() {
    super.after();
  }
}