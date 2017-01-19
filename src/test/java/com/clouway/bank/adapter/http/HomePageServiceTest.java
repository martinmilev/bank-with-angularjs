package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.AccountRepository;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static com.clouway.bank.matchers.SitebricksMatchers.isOk;
import static com.clouway.bank.matchers.SitebricksMatchers.sameAs;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class TransactionServiceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private Request request = context.mock(Request.class);
  private AccountRepository userRepository = context.mock(AccountRepository.class);
  private HomePageService homePageService = new HomePageService(userRepository);

  @Test
  public void happyPath() {
    final Optional<Account> possibleAccount = Optional.of(new Account("id", "A", 1d));

    context.checking(new Expectations() {{
      oneOf(userRepository).getById(null);
      will(returnValue(possibleAccount));
    }});

    Reply<?> reply = homePageService.getAccount(request);
    Account expected = possibleAccount.get();

    assertThat(reply, isOk());
    assertThat(reply, sameAs(expected));
  }
}
