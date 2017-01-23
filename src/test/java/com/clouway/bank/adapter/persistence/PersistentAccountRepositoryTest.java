package com.clouway.bank.adapter.persistence;

import com.clouway.bank.core.Account;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentAccountRepositoryTest {

  @Rule
  public DatastoreRule datastoreRule = new DatastoreRule("accounts");

  private PersistentAccountRepository accountRepository;

  @Before
  public void setUp() {
     accountRepository = new PersistentAccountRepository(datastoreRule.getDatabase());
  }

  @Test
  public void findAccountThatWasRegistered() throws Exception {
    Account actual = accountRepository.register("A", 10d);
    Account expected = accountRepository.findUserAccount(actual.id).get();

    assertThat(actual, is(expected));
  }

  @Test
  public void accountWasNotFound() throws Exception {
    Optional<Account> actual = accountRepository.findUserAccount("507f1f77bcf86cd799439011");

    assertTrue(!actual.isPresent());
  }

  @Test
  public void updateAccountBalance() throws Exception {
    Account account = accountRepository.register("A", 10d);
    accountRepository.update(account.id, 5d);
    Double balance = accountRepository.findUserAccount(account.id).get().balance;
    assertThat(balance, is(5d));
  }
}