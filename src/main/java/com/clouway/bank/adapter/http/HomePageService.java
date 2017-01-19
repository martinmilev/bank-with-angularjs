package com.clouway.bank.adapter.http;

import com.clouway.bank.core.AccountRepository;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Service
@At("/v1/transaction")
public class TransactionService {
  private final AccountRepository accountRepository;

  @Inject
  public TransactionService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Get
  public Reply<?> getAccount() {
    // logged user

    return Reply.with(accountRepository.getById("5880a4a111e4c742167cbb98").get()).as(Json.class);
  }

  // GET v1/users/53245/transactions
  // GET v1/users/12312/transactions


  @Post
  public Reply<?> deposit() {
    return null;
  }
}