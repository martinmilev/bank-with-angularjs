package com.clouway.bank.adapter.http;

import com.clouway.bank.adapter.http.OperationsService.Operation;
import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
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

  private OperationsService homePageService = new OperationsService(accountRepository, userSecurity);


  @Test
  public void happyPath() throws Exception {
    final Operation operation = new Operation("0", "deposit");
    final Optional<Account> possibleAccount = Optional.of(new Account("id", "A", 1d));
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(new User("::any user id::", "", "")));
      oneOf(accountRepository).findUserAccount("::any user id::");
      will(returnValue(possibleAccount));
      oneOf(accountRepository).update("id", 1.0);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }

  @Test
  public void depositFromAccount() throws Exception {
    final Operation operation = new Operation("1", "deposit");
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(new User("::any user id::", "name", "password")));
      oneOf(accountRepository).findUserAccount("::any user id::");
      will(returnValue(Optional.of(new Account("::any user id::", "A", 1d))));
      oneOf(accountRepository).update("::any user id::", 2.0);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }

  @Test
  public void withdrawFromAccount() throws Exception {
    final Operation operation = new Operation("1", "withdraw");
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(new User("::any user id::", "name", "password")));
      oneOf(accountRepository).findUserAccount("::any user id::");
      will(returnValue(Optional.of(new Account("::any account id::", "A", 1d))));
      oneOf(accountRepository).update("::any account id::", 0.0);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }

  @Test
  public void insuficientFunds() throws Exception {
    final Operation operation = new Operation("10", "withdraw");
    FakeRequest request = new FakeRequest(operation);

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(new User("::any user id::", "name", "password")));
      oneOf(accountRepository).findUserAccount("::any user id::");
      will(returnValue(Optional.of(new Account("::any account id::", "A", 5d))));
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isBadRequest());
  }
}