package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Service
@At("/v1/operation")
public class OperationsService {
  private final AccountRepository accountRepository;

  @Inject
  public OperationsService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Post
  public Reply<?> issueOperation(Request request) {
    Operation operation = request.read(Operation.class).as(Json.class);
    Account account = accountRepository.findById("5880c362e4e63f70e073848a").get();
    Double newBalance = 0d;

    switch (operation.type) {
      case "deposit" :
        newBalance = account.balance + Double.valueOf(operation.amount);
      break;
      case "withdraw" :
        newBalance = account.balance - Double.valueOf(operation.amount);

    }
    accountRepository.update(account.id,newBalance);
    return Reply.saying().ok();
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
