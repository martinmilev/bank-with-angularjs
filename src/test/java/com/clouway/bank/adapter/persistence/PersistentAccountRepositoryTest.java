package com.clouway.bank.adapter.persistence;

import com.clouway.bank.core.Account;
import com.clouway.bank.core.Transaction;
import com.clouway.bank.core.TransactionRepository;
import com.clouway.bank.core.User;
import com.clouway.bank.matchers.DatastoreRule;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentAccountRepositoryTest {

  @Rule
  public DatastoreRule datastoreRule = new DatastoreRule();
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private TransactionRepository transactionRepository = context.mock(TransactionRepository.class);

  private PersistentAccountRepository accountRepository;

  @Before
  public void setUp() {
     accountRepository = new PersistentAccountRepository(datastoreRule.getDatabase(), transactionRepository);
  }

  @Test
  public void findRegisteredUser() throws Exception {
    User actual = accountRepository.register("::any username::", "::any password::");
    User expected = accountRepository.findByUserName(actual.name).get();

    assertThat(actual, is(expected));
  }

  @Test
  public void findAccountByID() throws Exception {
    User user = accountRepository.register("::any username::", "::any password::");
    Account expected = accountRepository.findAccountByID(user.id).get();
    Account actual = new Account(user.id, "::any username::", 0d);

    assertThat(actual, is(expected));
  }

  @Test
  public void userWasNotFoundByName() throws Exception {
    Optional<User> actual = accountRepository.findByUserName("::any username::");

    assertTrue(!actual.isPresent());
  }

  @Test
  public void accountWasNotFoundByID() throws Exception {
    Optional<Account> actual = accountRepository.findAccountByID("507f1f77bcf86cd799439011");

    assertTrue(!actual.isPresent());
  }

  @Test
  public void updateAccountBalance() throws Exception {

    User user = accountRepository.register("A","password");

    context.checking(new Expectations() {{
      oneOf(transactionRepository).registerTransaction(
              with(any(Transaction.class)));
    }});

    accountRepository.update(user.id, 5d, "withdraw", 5d);

    Double balance = accountRepository.findAccountByID(user.id).get().balance;

    assertThat(balance, is(5d));
  }

  @Test
  public void changePassword() {
    User user = accountRepository.register("John", "password");
    accountRepository.updatePassword(user.id, "newPassword");
    user = accountRepository.findByUserName(user.name).get();

    assertTrue(user.password.equals("newPassword"));
  }
}