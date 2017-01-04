package com.clouway.bank;

import com.google.sitebricks.SitebricksModule;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class BankModule extends SitebricksModule {
  @Override
  protected void configureSitebricks() {
    at("/").show(MainPage.class);
  }
}
