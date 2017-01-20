package com.clouway.bank.persistence;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */

public class DBProvider implements Provider<MongoDatabase> {

  @Override
  public MongoDatabase get() {
    MongoClient mongo =
            new MongoClient("localhost", 27017);

    MongoClient client = new MongoClient(Lists.newArrayList(
            new ServerAddress("localhost:10001"),
            new ServerAddress("localhost:10002"),
            new ServerAddress("localhost:10003")
    ));
    return mongo.getDatabase("bankApp");
  }
}