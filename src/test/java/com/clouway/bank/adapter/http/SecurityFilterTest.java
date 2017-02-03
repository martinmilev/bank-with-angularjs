package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Session;
import com.clouway.bank.core.SessionRepository;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class SecurityFilterTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private FilterChain chain = context.mock(FilterChain.class);
  private SessionRepository sessionRepository = context.mock(SessionRepository.class);

  private FakeHttpServletRequest request;
  private FakeHttpServletResponse response;

  @Before
  public void setUp() throws Exception {
    request = new FakeHttpServletRequest();
    response = new FakeHttpServletResponse();
  }

  @Test
  public void authorisedUser() throws Exception {
    SecurityFilter filter = new SecurityFilter(sessionRepository);
    request.addCookie(new Cookie("SID", ":: any id ::"));
    request.setRequestURI("/");

    context.checking(new Expectations() {{
      oneOf(sessionRepository).findSessionAvailableAt(with(any(String.class)), with(any(LocalDateTime.class)));
      will(returnValue(Optional.of(new Session(":: any id ::", 1, "::any username::"))));
      oneOf(chain).doFilter(request, response);
    }});

    filter.doFilter(request, response, chain);
  }

  @Test
  public void unauthorisedUser() throws Exception {
    SecurityFilter filter = new SecurityFilter(sessionRepository);
    request.setRequestURI("/");

    filter.doFilter(request, response, chain);
    assertThat(response.getRedirect(), is("/login"));
  }
}