package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Session;
import com.clouway.bank.core.SessionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserAuthentication;
import com.google.inject.util.Providers;
import com.google.sitebricks.headless.Request;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

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
  private UserAuthentication userAuthentication = context.mock(UserAuthentication.class);
  private Request request = context.mock((Request.class));

  private FakeHttpServletResponse response = new FakeHttpServletResponse();
  private LoginPage loginPage = new LoginPage(Providers.of(response), userAuthentication, sessionRepository);

  @Test
  public void loginAuthorisedUser() throws Exception {
    String name = "test";
    String pswd = "test";
    Optional<User> user = Optional.of(new User("::any id::", name, pswd));

    context.checking(new Expectations() {{
      oneOf(request).param("name");
      will(returnValue(name));
      oneOf(request).param("password");
      will(returnValue(pswd));
      oneOf(userAuthentication).authenticate(name);
      will(returnValue(user));
      oneOf(sessionRepository).startSession(with(any(LocalDateTime.class)));
      will(returnValue(new Session("::any id::", 30)));
    }});

    String redirect = loginPage.login((Request) request);

    assertThat(redirect, is("/"));
  }

  @Test
  public void userDoesNotExist() throws Exception {
    context.checking(new Expectations() {{
      oneOf(request).param("name");
      will(returnValue("::any name::"));
      oneOf(request).param("password");
      will(returnValue("::wrong password::"));
      oneOf(userAuthentication).authenticate("::any name::");
      will(returnValue(Optional.empty()));
    }});

    loginPage.login((Request) request);
    String message = loginPage.getMessage();

    assertThat(message, is("Incorrect username."));
  }

  @Test
  public void tryToLoginWithIncorrectPassword() throws Exception {
    String name = "test";
    String pswd = "test";
    Optional<User> user = Optional.of(new User("::any id::", name, pswd));

    context.checking(new Expectations() {{
      oneOf(request).param("name");
      will(returnValue(name));
      oneOf(request).param("password");
      will(returnValue("::wrong password::"));
      oneOf(userAuthentication).authenticate(name);
      will(returnValue(user));
    }});

    loginPage.login((Request) request);
    String message = loginPage.getMessage();

    assertThat(message, is("Incorrect password."));

  }
}