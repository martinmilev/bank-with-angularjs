package com.clouway.bank.persistence;

import com.clouway.bank.core.Account;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class PersistentAccountRepositoryTest {
  private PersistentAccountRepository accountRepository = new PersistentAccountRepository(new DBProvider());

  @Before
  public void setUp() throws Exception {
    new DBProvider().get().getCollection("accounts").drop();
  }

  @Test
  public void happyPath() throws Exception {
    Account actual = accountRepository.register("A", 10d);
    Account expected = accountRepository.getById(actual.id).get();

    assertThat(actual, is(expected));
  }

  @Test
  public void getUnexistingAccount() throws Exception {
    Optional<Account> actual = accountRepository.getById("507f1f77bcf86cd799439011");

    assertTrue(!actual.isPresent());
  }

  @Test
  public void updateAccountBalance() throws Exception {
    Account account = accountRepository.register("A", 10d);
    Double expected = 5d;
    accountRepository.update(account.id, expected);
    Double actual = accountRepository.getById(account.id).get().balance;
    assertThat(actual, is(expected));
  }
}