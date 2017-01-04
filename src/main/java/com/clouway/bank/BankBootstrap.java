package com.clouway.bank;

/**
 * @author Miroslav Genov (miroslav.genov@clouway.com)
 */
public class BankBootstrap {

  public static void main(String[] args) {
    final int httpPort = 8080;
    Jetty jetty = new Jetty(httpPort);
    jetty.start();

    System.out.println(String.format("Bank is up and running on: %d", httpPort));
  }
}
