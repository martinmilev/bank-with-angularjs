package com.clouway.bank;

import com.google.sitebricks.At;
import com.google.sitebricks.Show;
import com.google.sitebricks.http.Get;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@At("/")
@Show("MainPage.html")
public class MainPage {
  @Get
  public void show(){

  }
}
