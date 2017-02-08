package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.CurrenciesNotAvailableException;
import com.clouway.bank.core.CurrencyConverter;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;

import java.text.DecimalFormat;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Service
@At("/v1/operation")
public class OperationsService {
  private final AccountRepository accountRepository;
  private final UserSecurity userSecurity;
  private final CurrencyConverter converter;

  @Inject
  public OperationsService(AccountRepository accountRepository, UserSecurity userSecurity, CurrencyConverter converter) {
    this.accountRepository = accountRepository;
    this.userSecurity = userSecurity;
    this.converter = converter;
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
    Double requestedAmount = 0d;
    Double newBalance = 0d;
    DecimalFormat formater = new DecimalFormat(".####");

    try {
      if(operation.currency.equals("bitcoin")) {
        requestedAmount =  Double.valueOf(operation.amount);
      } else {
        requestedAmount = converter.convert(operation.currency, "BTC", Double.valueOf(operation.amount));
      }
    } catch (CurrenciesNotAvailableException e) {
      return Reply.saying().status(503);
    }

    if (account.balance < requestedAmount && operation.type.equals("withdraw")) {
      return Reply.saying().badRequest();
    }

    switch (operation.type) {
      case "deposit":
        newBalance = account.balance + requestedAmount;
        newBalance = Double.valueOf(formater.format(newBalance));
        accountRepository.update(account.id, newBalance, operation.type, requestedAmount);
        break;
      case "withdraw":
        newBalance = account.balance - requestedAmount;
        newBalance = Double.valueOf(formater.format(newBalance));
        accountRepository.update(account.id, newBalance, operation.type, requestedAmount);
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
    private String currency;

    public Operation(String amount, String type, String currency) {
      this.type = type;
      this.amount = amount;
      this.currency = currency;
    }

    public Operation() {
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

    public String getCurrency() {
      return currency;
    }

    public void setCurrency(String currency) {
      this.currency = currency;
    }
  }
}