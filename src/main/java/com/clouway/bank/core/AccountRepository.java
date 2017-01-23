package com.clouway.bank.core;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface AccountRepository {
  Account register(String pesho, Double initialBalance);

  Optional<Account> findUserAccount(String id);

  void update(String id, Double amount);
}
