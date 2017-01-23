package com.clouway.bank.adapter.http;

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
import static com.clouway.bank.matchers.SitebricksMatchers.sameAs;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class HomePageServiceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private AccountRepository userRepository = context.mock(AccountRepository.class);
  private UserSecurity userSecurity = context.mock(UserSecurity.class);

  private HomePageService homePageService = new HomePageService(userRepository, userSecurity);

  @Test
  public void happyPath() {
    final Optional<Account> possibleAccount = Optional.of(new Account("id", "A", 1d));

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(new User("id")));
      oneOf(userRepository).findUserAccount("id");
      will(returnValue(possibleAccount));
    }});

    Reply<?> reply = homePageService.getAccount();
    Account expected = possibleAccount.get();

    assertThat(reply, isOk());
    assertThat(reply, sameAs(expected));
  }

  @Test
  public void notExistignAccount() {
    final Optional<Account> possibleAccount = Optional.empty();

    context.checking(new Expectations() {{
      oneOf(userSecurity).currentUser();
      will(returnValue(new User("id")));
      oneOf(userRepository).findUserAccount("id");
      will(returnValue(possibleAccount));
    }});

    Reply<?> reply = homePageService.getAccount();

    assertThat(reply, isBadRequest());
  }
}
