package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Service
@At("/v1/useraccount")
public class HomePageService {
  private final AccountRepository accountRepository;
  private UserSecurity userSecurity;

  @Inject
  public HomePageService(AccountRepository accountRepository, UserSecurity userSecurity) {
    this.accountRepository = accountRepository;
    this.userSecurity = userSecurity;
  }

  @Get
  public Reply<?> getAccount() {
    Optional<User> possibleUser = userSecurity.currentUser();

    if (!possibleUser.isPresent()) {
      return Reply.saying().unauthorized();
    }

    Optional<Account> possibleAccount = accountRepository.findAccountByID(possibleUser.get().id);
    return Reply.with(possibleAccount.get()).as(Json.class);
  }
}