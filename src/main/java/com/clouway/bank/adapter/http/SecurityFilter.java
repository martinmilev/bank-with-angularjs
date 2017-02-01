package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Session;
import com.clouway.bank.core.SessionRepository;
import com.google.inject.Singleton;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Singleton
public class SecurityFilter implements Filter {

  private SessionRepository sessionRepository;

  public SecurityFilter(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest req = ((HttpServletRequest) servletRequest);
    HttpServletResponse resp = ((HttpServletResponse) servletResponse);
    // User is authenticated, then request need to be passed to sitebricks and servlets
    if (hasValidCookie(req.getCookies()) || req.getRequestURI().equals("/login")) {
      filterChain.doFilter(req, resp);
      return;
    }
    resp.sendRedirect("/login");
  }

  @Override
  public void destroy() {
  }

  public boolean hasValidCookie(Cookie[] cookies) {
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("SID")) {
        Optional<Session> possibleSession = sessionRepository.findSessionAvailableAt(
                cookie.getValue(), LocalDateTime.now()
        );

        if (possibleSession.isPresent()) {
          return true;
        }
      }
    }
    return false;
  }
}