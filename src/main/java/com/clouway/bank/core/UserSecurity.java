package com.clouway.bank.core;

import java.util.Optional;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface UserSecurity {

  Optional<User> currentUser();
}
