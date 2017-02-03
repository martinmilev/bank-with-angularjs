package com.clouway.bank.core;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface UserRepository {
  User register(String name, String password);

  Optional<User> findByUserName(String name);
}
