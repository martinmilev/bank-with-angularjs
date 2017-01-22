package com.clouway.bank.adapter.http;

import com.clouway.bank.adapter.http.OperationsService.Operation;
import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Request.RequestRead;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static com.clouway.bank.matchers.SitebricksMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class OperationsServiceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private Request request = context.mock(Request.class);
  private AccountRepository accountRepository = context.mock(AccountRepository.class);
  private OperationsService homePageService = new OperationsService(accountRepository);
  private final Optional<Account> possibleAccount = Optional.of(new Account("id", "A", 1d));
  private String id = "5880c362e4e63f70e073848a";

  @Test
  public void happyPath() throws Exception {
    final Optional<Account> possibleAccount = Optional.of(new Account("id", "A", 1d));
    String id = "5880c362e4e63f70e073848a";


    context.checking(new Expectations() {{
      oneOf(request).read(Operation.class);
      will(returnValue((RequestRead) aClass -> new Operation("0", "deposit")));
      oneOf(accountRepository).findById(id);
      will(returnValue(possibleAccount));
      oneOf(accountRepository).update("id", 1.0);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }


  @Test
  public void depisit() throws Exception {

    context.checking(new Expectations() {{
      oneOf(request).read(Operation.class);
      will(returnValue((RequestRead) aClass -> new Operation("1", "deposit")));
      oneOf(accountRepository).findById(id);
      will(returnValue(possibleAccount));
      oneOf(accountRepository).update("id", 2.0);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }

  @Test
  public void withdraw() throws Exception {

    context.checking(new Expectations() {{
      oneOf(request).read(Operation.class);
      will(returnValue((RequestRead) aClass -> new Operation("1", "withdraw")));
      oneOf(accountRepository).findById(id);
      will(returnValue(possibleAccount));
      oneOf(accountRepository).update("id", 0.0);
    }});

    Reply<?> reply = homePageService.issueOperation(request);

    assertThat(reply, isOk());
  }
}