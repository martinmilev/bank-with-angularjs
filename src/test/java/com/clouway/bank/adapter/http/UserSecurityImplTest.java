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

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class UserSecurityImplTest {
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private UserRepository userRepository = context.mock(UserRepository.class);
  private SessionRepository sessionRepository = context.mock(SessionRepository.class);
  private FakeHttpServletRequest request = new FakeHttpServletRequest();

  private UserSecurityImpl userSecurity = new UserSecurityImpl(userRepository, sessionRepository, Providers.of(request));

  @Test
  public void happyPath() throws Exception {
    request.addCookie(new Cookie("SID","value"));
    User user = new User("::any id::", "username", "::any pswd::");

    context.checking(new Expectations(){{
      oneOf(sessionRepository).findSessionAvailableAt(with(any(String.class)), with(any(LocalDateTime.class)));
      will(returnValue(Optional.of(new Session("value", 10,user.name))));
      oneOf(userRepository).findByUserName(user.name);
      will(returnValue(Optional.of(user)));
    }});

    User actual = userSecurity.currentUser().get();

    assertThat(actual,is(user));
  }

  @Test
  public void noPresentSession() throws Exception {
    request.addCookie(new Cookie("SID","id"));

    context.checking(new Expectations(){{
      oneOf(sessionRepository).findSessionAvailableAt(with(any(String.class)), with(any(LocalDateTime.class)));
      will(returnValue(Optional.empty()));
    }});

    assertFalse(userSecurity.currentUser().isPresent());
  }

  @Test
  public void missingCookie() throws Exception {
    assertFalse(userSecurity.currentUser().isPresent());
  }
}