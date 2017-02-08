package com.clouway.bank.adapter.http;

import com.clouway.bank.core.CurrenciesNotAvailableException;
import com.clouway.bank.core.CurrencyConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class YahooCurrencyConverter implements CurrencyConverter {

  public Double convert(String from, String to, Double amount) throws CurrenciesNotAvailableException {
    String targetUrl = "http://finance.yahoo.com/d/quotes.csv?f=l1&s=" + from + to + "=X";
    try {
      URL url = new URL(targetUrl);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String line = reader.readLine();
      if (line.length() > 0) {
        return Double.parseDouble(line) * amount;
      }
      return 0d;
    } catch (MalformedURLException e) {
      throw new IllegalStateException(String.format("bad url '%s' was provided for the yahoo currency converter", targetUrl), e);
    } catch (IOException e) {
      throw new CurrenciesNotAvailableException();
    }
  }
}
