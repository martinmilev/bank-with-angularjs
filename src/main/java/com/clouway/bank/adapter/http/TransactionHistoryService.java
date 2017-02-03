package com.clouway.bank.adapter.http;

import com.clouway.bank.core.TransactionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import java.util.Optional;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
@Service
@At("/v1/transactions")
  public class TransactionHistoryService {
  private final TransactionRepository transactionRepository;
  private final Integer limit = 20;
  private UserSecurity security;

  @Inject
  public TransactionHistoryService(TransactionRepository transactionRepository, UserSecurity security) {
    this.transactionRepository = transactionRepository;
    this.security = security;
  }

  @Get
  public Reply<?> retrieveTransactionHistory(Request request) {
    String startingFromCursor = request.param("startingFromCursor");
    String isNext = request.param("isNext");
    Optional<User> account = security.currentUser();
    return Reply.with(
            transactionRepository.retrieveTransactions(account.get().id, startingFromCursor, Boolean.valueOf(isNext), limit)
    ).as(Json.class);
  }
}
