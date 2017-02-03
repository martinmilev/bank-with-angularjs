package com.clouway.bank.adapter.http;

import com.clouway.bank.core.*;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Service
@At("/v1/operation")
public class OperationsService {
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private UserSecurity userSecurity;

  @Inject
  public OperationsService(AccountRepository accountRepository, TransactionRepository transactionRepository, UserSecurity userSecurity) {
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
    this.userSecurity = userSecurity;
  }

  @Post
  public Reply<?> issueOperation(Request request) {
    Operation operation = request.read(Operation.class).as(Json.class);

    Optional<User> possibleUser = userSecurity.currentUser();
    if (!possibleUser.isPresent()) {
      return Reply.saying().unauthorized();
    }

    Optional<Account> possibleAccount = accountRepository.findAccountByID(possibleUser.get().id);

    Account account = possibleAccount.get();
    Double requestedAmount = Double.valueOf(operation.amount);
    Double newBalance = 0d;

    if (account.balance < requestedAmount && operation.type.equals("withdraw")) {
      return Reply.saying().badRequest();
    }
    switch (operation.type) {
      case "deposit":
        newBalance = account.balance + Double.valueOf(operation.amount);
        accountRepository.update(account.id, newBalance, operation.type, operation.amount);
        break;
      case "withdraw":
        newBalance = account.balance - Double.valueOf(operation.amount);
        accountRepository.update(account.id, newBalance, operation.type, operation.amount);
        break;
    }

    return Reply.with(new DepositResult(newBalance)).as(Json.class);
  }

  private static class DepositResult {
    private Double balance;

    public DepositResult(Double balance) {
      this.balance = balance;
    }
  }

  public static class Operation {
    private String amount;
    private String type;

    public Operation() {
    }

    public Operation(String amount, String type) {
      this.type = type;
      this.amount = amount;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getAmount() {
      return amount;
    }

    public void setAmount(String amount) {
      this.amount = amount;
    }
  }
}
