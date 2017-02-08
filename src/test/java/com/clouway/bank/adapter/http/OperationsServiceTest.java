package com.clouway.bank.adapter.http;

import com.clouway.bank.adapter.http.OperationsService.Operation;
import com.clouway.bank.core.*;
import com.google.sitebricks.headless.Reply;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static com.clouway.bank.matchers.SitebricksMatchers.isBadRequest;
import static com.clouway.bank.matchers.SitebricksMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class OperationsServiceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private AccountRepository accountRepository = context.mock(AccountRepository.class);
  private UserSecurity userSecurity = context.mock(UserSecurity.class);
  private CurrencyConverter converter = context.mock(CurrencyConverter.class);

  private OperationsService homePageService = new OperationsService(accountRepository, userSecurity, converter);

  @Test
  public void happyPath() throws Exception {
    final Operation operation = new Operation("0", "deposit", "USD");
    final Optional<Account> possibleAccount = Optional.of(new Account("cursor", "A", 1d));
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(Optional.of(new User("::any user id::", "", ""))));
      oneOf(converter).convert("USD", "BTC", 0d);
      will(returnValue(0d));
      oneOf(accountRepository).findAccountByID("::any user id::");
      will(returnValue(possibleAccount));
      oneOf(accountRepository).update("cursor", 1d, "deposit", 0d);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }

  @Test
  public void depositFromAccount() throws Exception {
    final Operation operation = new Operation("1", "deposit", "USD");
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(Optional.of(new User("::any user id::", "name", "password"))));
      oneOf(converter).convert("USD", "BTC", 1d);
      will(returnValue(1d));
      oneOf(accountRepository).findAccountByID("::any user id::");
      will(returnValue(Optional.of(new Account("id", "A", 1d))));
      oneOf(accountRepository).update("id", 2d, "deposit", 1d);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }

  @Test
  public void withdrawFromAccount() throws Exception {
    final Operation operation = new Operation("1", "withdraw", "USD");
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(Optional.of(new User("::any user id::", "::any name::", "::any password::"))));
      oneOf(converter).convert("USD", "BTC", 1d);
      will(returnValue(1d));
      oneOf(accountRepository).findAccountByID("::any user id::");
      will(returnValue(Optional.of(new Account("::any account id::", "A", 1d))));
      oneOf(accountRepository).update("::any account id::", 0d, "withdraw", 1d);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }

  @Test
  public void insuficientFunds() throws Exception {
    final Operation operation = new Operation("10", "withdraw", "USD");
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(Optional.of(new User("::any user id::", "::any name::", "::any password::"))));
      oneOf(converter).convert("USD", "BTC", 10d);
      will(returnValue(10d));
      oneOf(accountRepository).findAccountByID("::any user id::");
      will(returnValue(Optional.of(new Account("::any account id::", "A", 5d))));
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isBadRequest());
  }
}