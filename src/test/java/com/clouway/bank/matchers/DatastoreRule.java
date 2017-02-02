package com.clouway.bank.matchers;

import com.google.inject.Provider;
import com.google.inject.util.Providers;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.junit.rules.ExternalResource;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class DatastoreRule extends ExternalResource {

  public Provider<MongoDatabase> getDatabase() {
    return Providers.of(new MongoClient("localhost", 27017).getDatabase("testBankApp"));
  }

  @Override
  protected void before() throws Throwable {
    MongoIterable<String> collections = getDatabase().get().listCollectionNames();
    for (String collection : collections) {
      if(!collection.equals("system.indexes")) {
        getDatabase().get().getCollection(collection).drop();
      }
    }
  }

  @Override
  protected void after() {
    super.after();
  }
}