package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
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
  private UserSecurity userSecurity;

  @Inject
  public OperationsService(AccountRepository accountRepository, UserSecurity userSecurity) {
    this.accountRepository = accountRepository;
    this.userSecurity = userSecurity;
  }

  @Post
  public Reply<?> issueOperation(Request request) {
    Operation operation = request.read(Operation.class).as(Json.class);
    // Logged user ...
    User user = userSecurity.currentUser();

    Optional<Account> possibleAccount = accountRepository.findUserAccount(user.userId());
    if (!possibleAccount.isPresent()) {
      return Reply.saying().notFound();
    }

    Account account = possibleAccount.get();
    Double newBalance = 0d;

    switch (operation.type) {
      case "deposit" :
        newBalance = account.balance + Double.valueOf(operation.amount);
      break;
      case "withdraw" :
        newBalance = account.balance - Double.valueOf(operation.amount);

    }
    accountRepository.update(account.id,newBalance);

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
