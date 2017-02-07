package com.clouway.bank.adapter.http;

import com.clouway.bank.adapter.http.UserPasswordChangingService.Query;
import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
import com.google.sitebricks.headless.Reply;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

import static com.clouway.bank.matchers.SitebricksMatchers.isBadRequest;
import static com.clouway.bank.matchers.SitebricksMatchers.isOk;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class UserPasswordChangingServiceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private UserSecurity security = context.mock(UserSecurity.class);
  private AccountRepository accountRepository = context.mock(AccountRepository.class);
  private UserPasswordChangingService service = new UserPasswordChangingService(accountRepository, security);

  @Test
  public void changePassword() {
    String oldPassword = "oldPassword";
    String newPassword = "newPassword";
    final Query query = new Query(oldPassword, newPassword);
    FakeRequest request = new FakeRequest(query);

    context.checking(new Expectations() {{
      oneOf(security).currentUser();
      will(returnValue(Optional.of(new User("123", "John", BCrypt.hashpw(oldPassword, BCrypt.gensalt())))));

      oneOf(accountRepository).updatePassword(with(any(String.class)), with(any(String.class)));
    }});

    Reply<?> reply = service.changePassword(request);

    assertThat(reply, isOk());
  }

  @Test
  public void failToChangePassword() {
    String oldPassword = "wrongOldPassword";
    String newPassword = "newPassword";
    final Query query = new Query(oldPassword, newPassword);
    FakeRequest request = new FakeRequest(query);

    context.checking(new Expectations() {{
      oneOf(security).currentUser();
      will(returnValue(Optional.of(new User("123", "John", BCrypt.hashpw("oldPassword", BCrypt.gensalt())))));
    }});

    Reply<?> reply = service.changePassword(request);

    assertThat(reply, isBadRequest());
  }

}
