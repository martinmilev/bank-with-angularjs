package com.clouway.bank.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class User {
  public final String userId;
  public final String name;
  public final String password;

  public User(String userId, String name, String password) {
    this.userId = userId;
    this.name = name;
    this.password = password;
  }
}