package com.clouway.bank.adapter.http;

//import com.clouway.bank.adapter.http.ExchangeRatesService.Rates;

import com.clouway.bank.adapter.http.ExchangeRatesService.Rate;
import com.clouway.bank.core.CurrencyConverter;
import com.google.common.collect.ImmutableList;
import com.google.sitebricks.headless.Reply;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.clouway.bank.matchers.SitebricksMatchers.isOk;
import static com.clouway.bank.matchers.SitebricksMatchers.sameAs;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class ExchangeRatesServiceTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();

  private CurrencyConverter converter = context.mock(CurrencyConverter.class);

  private ExchangeRatesService service = new ExchangeRatesService(converter);

  @Test
  public void happyPath() throws Exception {

    context.checking(new Expectations() {{
      oneOf(converter).convert("BTC", "EUR", 1d);
      will(returnValue(1d));
      oneOf(converter).convert("BTC", "USD", 1d);
      will(returnValue(1d));
      oneOf(converter).convert("BTC", "GBP", 1d);
      will(returnValue(1d));
    }});

    Reply<?> reply = service.getExchangeRates();
    List<Rate> expected = ImmutableList.of(
            new Rate("EUR", 1d),
            new Rate("USD", 1d),
            new Rate("GBP", 1d)
    );

    assertThat(reply, isOk());
    assertThat(reply, sameAs(expected));
  }
}