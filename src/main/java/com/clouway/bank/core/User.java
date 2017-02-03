package com.clouway.bank.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class User {
  public final String id;
  public final String name;
  public final String password;

  public User(String userId, String name, String password) {
    this.id = userId;
    this.name = name;
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    User user = (User) o;

    if (id != null ? !id.equals(user.id) : user.id != null) return false;
    if (name != null ? !name.equals(user.name) : user.name != null) return false;
    return password != null ? password.equals(user.password) : user.password == null;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    return result;
  }
}