package com.serjltt.moshi.adapters;

import java.lang.annotation.Annotation;

public final class UnwrapJsonFactory {
  public static UnwrapJson create(final String... values) {
    return new UnwrapJson() {
      @Override public String[] value() {
        return values;
      }

      @Override public Class<? extends Annotation> annotationType() {
        return UnwrapJson.class;
      }
    };
  }

  private UnwrapJsonFactory() {
    throw new AssertionError("No instances.");
  }
}
