package com.clouway.bank.adapter.http;

import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;
import com.google.sitebricks.client.Transport;
import com.google.sitebricks.headless.Request;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class FakeRequest implements Request {
  private Object requestedObject;

  public FakeRequest(Object requestedObject) {
    this.requestedObject = requestedObject;
  }

  @Override
  public <E> RequestRead<E> read(Class<E> aClass) {
    return new RequestRead<E>() {
      @Override
      public E as(Class<? extends Transport> aClass) {
        return (E) requestedObject;
      }
    };
  }

  @Override
  public <E> RequestRead<E> read(TypeLiteral<E> typeLiteral) {
    return null;
  }

  @Override
  public void readTo(OutputStream outputStream) throws IOException {

  }

  @Override
  public Multimap<String, String> headers() {
    return null;
  }

  @Override
  public Multimap<String, String> params() {
    return null;
  }

  @Override
  public Multimap<String, String> matrix() {
    return null;
  }

  @Override
  public String matrixParam(String s) {
    return null;
  }

  @Override
  public String param(String s) {
    return null;
  }

  @Override
  public String header(String s) {
    return null;
  }

  @Override
  public String uri() {
    return null;
  }

  @Override
  public String path() {
    return null;
  }

  @Override
  public String context() {
    return null;
  }

  @Override
  public String method() {
    return null;
  }

  @Override
  public void validate(Object o) {

  }
}
