package com.clouway.bank.adapter.http;

import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Request;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Post;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
@Service
@At("/v1/useraccount/changePassword")
public class UserPasswordChangingService {
  private final AccountRepository accountRepository;
  private UserSecurity security;

  @Inject
  public UserPasswordChangingService(AccountRepository accountRepository, UserSecurity security) {
    this.accountRepository = accountRepository;
    this.security = security;
  }

  @Post
  public Reply<?> changePassword(Request request) {
    Query query = request.read(Query.class).as(Json.class);
    Optional<User> possibleUser = security.currentUser();

    if (possibleUser.isPresent() && BCrypt.checkpw(query.oldPass, possibleUser.get().password)) {

      String newHashedPassword = BCrypt.hashpw(query.newPass, BCrypt.gensalt());
      accountRepository.updatePassword(possibleUser.get().id, newHashedPassword);

      return Reply.saying().ok();
    } else {
      return Reply.saying().badRequest();
    }
  }

  public static class Query {
    public final String oldPass;
    public final String newPass;

    public Query(String oldPass, String newPass) {
      this.oldPass = oldPass;
      this.newPass = newPass;
    }
  }
}
