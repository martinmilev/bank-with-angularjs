package com.clouway.bank.core;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public interface CurrencyConverter {
  Double convert(String from, String to, Double amount) throws CurrenciesNotAvailableException;
}
