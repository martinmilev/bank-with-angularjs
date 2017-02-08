package com.clouway.bank.core;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface AccountRepository {

  Optional<Account> findAccountByID(String id);

  void updatePassword(String id, String password);

  void update(String id, Double amount, String operationType, Double operationAmount);

}
