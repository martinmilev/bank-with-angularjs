package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Session;
import com.clouway.bank.core.SessionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserRepository;
import com.clouway.bank.core.UserSecurity;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class UserSecurityImpl implements UserSecurity {
  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final Provider<HttpServletRequest> requestProvider;

  @Inject
  public UserSecurityImpl(UserRepository userRepository, SessionRepository sessionRepository, Provider<HttpServletRequest> requestProvider) {
    this.userRepository = userRepository;
    this.requestProvider = requestProvider;
    this.sessionRepository = sessionRepository;
  }

  @Override
  public Optional<User> currentUser() {
    Cookie currentCookie = null;
    for (Cookie cookie : requestProvider.get().getCookies()) {
      if (cookie.getName().equals("SID")) {
        currentCookie = cookie;
      }
    }

    if(currentCookie != null) {
      Optional<Session> session = sessionRepository.findSessionAvailableAt(currentCookie.getValue(), LocalDateTime.now());
      if (session.isPresent()) {
        return userRepository.findByUserName(session.get().getUsername());
      }
    }

    return Optional.empty();
  }
}
