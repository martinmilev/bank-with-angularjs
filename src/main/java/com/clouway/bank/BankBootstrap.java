package com.clouway.bank;

import com.clouway.bank.core.Account;
import com.clouway.bank.adapter.persistence.PersistentAccountRepository;
import com.google.inject.util.Providers;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BankBootstrap {

  public static void main(String[] args) {

    System.out.println(Arrays.asList(args));
    String databaseHost = "127.0.0.1:27017";
    if (args.length > 0 && "--db-host".equalsIgnoreCase(args[0])) {
      databaseHost = args[1];
    }

    Logger mongoLogger = Logger.getLogger( "org.mongodb" );
    mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.

    final int httpPort = 8081;

    MongoClientOptions options = MongoClientOptions.builder()
            .connectionsPerHost(5)
            .serverSelectionTimeout(1000)
            .socketTimeout(1000)
            .heartbeatConnectTimeout(1000)
            .socketTimeout(4000)
            .build();

    MongoClient client = null;

    boolean successfullyConnnected = false;

    for (int attempt = 1; attempt <= 10; attempt++) {
      try {

        client = new MongoClient(databaseHost, options);
        List<ServerAddress> serverAddressList = client.getServerAddressList();

        System.out.printf("got connected to %s\n", serverAddressList);

        successfullyConnnected = true;

        break;
      } catch (MongoException e) {
        System.out.printf("attempt to connect to %s for %d time, but got: %s\n", databaseHost, attempt, e.getMessage());
        continue;
      }

    }
    if (!successfullyConnnected) {
      System.out.println("unable to connect to the database.");
      System.exit(-1);
    }

    PersistentAccountRepository accountRepository = new PersistentAccountRepository(Providers.of(client.getDatabase("bankApp")));
    Account account = accountRepository.register("A", 24d);

    Jetty jetty = new Jetty(httpPort, client, account.id);
    jetty.start();

    System.out.println(String.format("Bank is up and running on: %d", httpPort));
  }
}
