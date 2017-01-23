package com.clouway.bank.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class Account {
  public final String id;
  public final String name;
  public final Double balance;

  public Account(String id, String name, Double balance) {
    this.name = name;
    this.id = id;
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;

    if (id != null ? !id.equals(account.id) : account.id != null) return false;
    if (name != null ? !name.equals(account.name) : account.name != null) return false;
    return balance != null ? balance.equals(account.balance) : account.balance == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (balance != null ? balance.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Account{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", balance='" + balance + '\'' +
            '}';
  }
}
