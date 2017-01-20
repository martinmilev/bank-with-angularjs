package com.clouway.bank.adapter.http;

import com.clouway.bank.core.AccountRepository;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
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
  public Reply<?> issueOperation() {
    Operation operation = new Operation();
    accountRepository.update("5880c362e4e63f70e073848a", Double.valueOf(operation.amount));
    return Reply.saying().ok();
  }

  private static class Operation {
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
