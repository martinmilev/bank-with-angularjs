package com.clouway.bank.adapter.http;

import com.clouway.bank.core.Transaction;
import com.clouway.bank.core.TransactionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.core.UserSecurity;
import com.clouway.bank.matchers.JsonBuilder;
import com.google.common.collect.ImmutableList;
import com.google.sitebricks.headless.Reply;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static com.clouway.bank.matchers.SitebricksMatchers.containsJson;
import static com.clouway.bank.matchers.SitebricksMatchers.isOk;
import static org.junit.Assert.*;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
@SuppressWarnings("unchecked")
public class TransactionHistoryServiceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TransactionRepository transactionRepository = context.mock(TransactionRepository.class);
  private UserSecurity security = context.mock(UserSecurity.class);
  private TransactionHistoryService service = new TransactionHistoryService(transactionRepository, security);

  private final String startingFromCursor = "12345";
  private final Boolean isNext = false;
  private final Integer limit = 20;
  private final Date date = new java.sql.Date(new Timestamp(System.currentTimeMillis()).getTime());

  @Test
  public void retrieveUserTransactions() {
    FakeRequest request = new FakeRequest(Optional.empty());
    request.setParameter("startingFromCursor", "12345");
    request.setParameter("isNext", "false");

    context.checking(new Expectations() {{
      oneOf(security).currentUser();
      will(returnValue(Optional.of(new User("123", "testUser", "pass"))));

      oneOf(transactionRepository).retrieveTransactions("123", startingFromCursor, isNext, limit);
      will(returnValue(ImmutableList.of(new Transaction(startingFromCursor, date, "123", "deposit", 100d),
              new Transaction(startingFromCursor, date, "123", "deposit", 105d))));
    }});

    Reply<?> actual = service.retrieveTransactionHistory(request);

    assertThat(actual, isOk());
    assertThat(actual, containsJson(JsonBuilder.aNewJsonArray().withElements(
            JsonBuilder.aNewJson().add("id", startingFromCursor).add("date", date.toString()).add("userID", "123").add("type", "deposit").add("amount", 100d),
            JsonBuilder.aNewJson().add("id", startingFromCursor).add("date", date.toString()).add("userID", "123").add("type", "deposit").add("amount", 105d)
    )));
  }
}