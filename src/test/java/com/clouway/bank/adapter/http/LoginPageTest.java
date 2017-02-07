package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Session;
import com.clouway.bank.core.SessionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserRepository;
import com.google.inject.util.Providers;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class LoginPageTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();
  private SessionRepository sessionRepository = context.mock(SessionRepository.class);
  private UserRepository userRepository = context.mock(UserRepository.class);

  private FakeHttpServletResponse response = new FakeHttpServletResponse();
  private FakeRequest request = new FakeRequest(Optional.empty());
  private LoginPage loginPage = new LoginPage(Providers.of(response), userRepository, sessionRepository);

  @Test
  public void loginAuthorisedUser() throws Exception {
    String name = "test";
    String pswd = "test";
    Optional<User> user = Optional.of(new User("::any id::", name, BCrypt.hashpw(pswd, BCrypt.gensalt())));
    request.setParameter("name", name);
    request.setParameter("password", pswd);

    context.checking(new Expectations() {{
      oneOf(userRepository).findByUserName(name);
      will(returnValue(user));
      oneOf(sessionRepository).startSession(with(any(LocalDateTime.class)), with(any(String.class)));
      will(returnValue(new Session("::any id::", 30, name)));
    }});

    String redirect = loginPage.login(request);

    assertThat(redirect, is("/"));
  }

  @Test
  public void userDoesNotExist() throws Exception {
    request.setParameter("name", "::any name::");
    request.setParameter("password", "::any password::");

    context.checking(new Expectations() {{
      oneOf(userRepository).findByUserName("::any name::");
      will(returnValue(Optional.empty()));
    }});

    loginPage.login(request);
    String message = loginPage.getErrorMessage();

    assertThat(message, is("Incorrect username."));
  }

  @Test
  public void tryToLoginWithIncorrectPassword() throws Exception {
    String name = "test";
    String pswd = "test";
    Optional<User> user = Optional.of(new User("::any id::", name, BCrypt.hashpw(pswd, BCrypt.gensalt())));
    request.setParameter("name", "test");
    request.setParameter("password", "::any password::");

    context.checking(new Expectations() {{
      oneOf(userRepository).findByUserName(name);
      will(returnValue(user));
    }});

    loginPage.login(request);
    String message = loginPage.getErrorMessage();

    assertThat(message, is("Incorrect password."));
  }
}