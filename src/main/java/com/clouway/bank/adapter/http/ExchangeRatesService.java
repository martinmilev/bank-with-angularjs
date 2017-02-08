package com.clouway.bank.adapter.http;

import com.clouway.bank.core.CurrenciesNotAvailableException;
import com.clouway.bank.core.CurrencyConverter;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
@Service
@At("/v1/exchangerates")
public class ExchangeRatesService {
  private CurrencyConverter converter;

  @Inject
  public ExchangeRatesService(CurrencyConverter converter) {
    this.converter = converter;
  }

  @Get
  public Reply<?> getExchangeRates() {
    try {
      return Reply.with(ImmutableList.of(
            new Rate("EUR", converter.convert("BTC", "EUR", 1D)),
            new Rate("USD", converter.convert("BTC", "USD", 1D)),
            new Rate("GBP", converter.convert("BTC", "GBP", 1D))
    )).as(Json.class);
    } catch (CurrenciesNotAvailableException e) {
      return Reply.saying().status(503);
    }
  }

  public static class Rate {
    private String currency;
    private Double value;

    public Rate(String currency, Double value) {
      this.currency = currency;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Rate rate = (Rate) o;
      return Objects.equal(currency, rate.currency) &&
              Objects.equal(value, rate.value);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(currency, value);
    }

    @Override
    public String toString() {
      return "Rate{" +
              "currency='" + currency + '\'' +
              ", value=" + value +
              '}';
    }
  }
}
