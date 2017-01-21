package com.clouway.bank.core;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface AccountRepository {
  Account register(String pesho, Double v);

  Optional<Account> findById(String id);

  void update(String id, Double amount);
}
