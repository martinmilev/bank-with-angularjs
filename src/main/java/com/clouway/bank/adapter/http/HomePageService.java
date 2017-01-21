package com.clouway.bank.adapter.http;

import com.clouway.bank.core.AccountRepository;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.client.transport.Json;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Service
@At("/v1/")
public class HomePageService {
  private final AccountRepository accountRepository;

  @Inject
  public HomePageService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Get
  public Reply<?> getAccount() {
    return Reply.with(accountRepository.findById(new UserID().id).get()).as(Json.class);
  }

  private static class UserID {
    private String id = "5880c362e4e63f70e073848a";

    public UserID() {}

    public UserID(String id) {
      this.id = id;
    }


    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  // GET v1/users/53245/transactions
  // GET v1/users/12312/transactions
}