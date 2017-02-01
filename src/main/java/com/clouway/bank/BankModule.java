package com.clouway.bank;

import com.clouway.bank.adapter.http.HomePageService;
import com.clouway.bank.adapter.http.LoginPage;
import com.clouway.bank.adapter.http.OperationsService;
import com.clouway.bank.adapter.http.SecurityFilter;
import com.clouway.bank.adapter.http.TransactionHistoryService;
import com.clouway.bank.adapter.persistence.PersistentAccountRepository;
import com.clouway.bank.adapter.persistence.PersistentSessionRepository;
import com.clouway.bank.core.AccountRepository;
import com.clouway.bank.core.SessionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserAuthentication;
import com.clouway.bank.core.UserSecurity;
import com.google.common.io.ByteStreams;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.util.Providers;
import com.google.sitebricks.SitebricksModule;
import com.google.sitebricks.SitebricksServletModule;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class BankModule extends SitebricksModule {
  private MongoClient client;
  private String accountId;

  public BankModule(MongoClient client, String accountId) {
    this.client = client;
    this.accountId = accountId;
  }

  @Override
  protected SitebricksServletModule servletModule() {
    return new SitebricksServletModule() {
      @Override
      protected void configureCustomServlets() {
        Provider<MongoDatabase> database = Providers.of(getDatabase());
        filter("/*").through(new SecurityFilter(new PersistentSessionRepository(database)));
        serve("/*").with(new HttpServlet() {
          @Override
          protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html; charset=UTF-8");
            ByteStreams.copy(BankModule.class.getResourceAsStream("MainPage.html"), resp.getOutputStream());
          }
        });
      }
    };
  }

  @Override
  protected void configureSitebricks() {
    at("/login").show(LoginPage.class);
    at("/v1/useraccount").serve(HomePageService.class);
    at("/v1/operation").serve(OperationsService.class);
    at("/v1/transactions").serve(TransactionHistoryService.class);

    Map<String, User> demoUsers = new HashMap<String, User>() {{
      put("test", new User(accountId, "test", "123123"));
    }};

    bind(UserSecurity.class).toInstance(new UserSecurity() {
      @Override
      public User currentUser() {
        return demoUsers.get("test");
      }
    });
    bind(AccountRepository.class).to(PersistentAccountRepository.class);
    bind(SessionRepository.class).to(PersistentSessionRepository.class);
    bind(UserAuthentication.class).toInstance(new UserAuthentication() {
      @Override
      public Optional<User> authenticate(String name) {
        if (demoUsers.containsKey(name)) {
          return Optional.of(demoUsers.get(name));
        }
        return Optional.empty();
      }
    });
  }

  @Provides
  public MongoDatabase getDatabase() {
    return client.getDatabase("bankApp");
  }
}