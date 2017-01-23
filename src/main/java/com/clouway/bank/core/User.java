package com.clouway.bank.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class User {
  private String userId;

  public User(String userId) {
    this.userId = userId;
  }

  public String userId() {
    return userId;
  }
}
