/*
 * Copyright 2016 Serj Lotutovici
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.serjltt.moshi.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class FallbackOnNullJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(FallbackOnNull.ADAPTER_FACTORY)
      .add(new Multiply.MultiplyAdapter())
      .build();

  @Test public void booleanFallbacks() throws Exception {
    assertForClass(WrapsBool.class, false, true, "{\"first\":false,\"second\":true}");
  }

  private static class WrapsBool implements Wrapper<Boolean> {
    @FallbackOnNull boolean first;
    @FallbackOnNull(fallbackBoolean = true) boolean second;

    @Override public Boolean first() {
      return first;
    }

    @Override public Boolean second() {
      return second;
    }
  }

  @Test public void byteFallbacks() throws Exception {
    assertForClass(WrapsByte.class, Byte.MIN_VALUE, (byte) 42, "{\"first\":128,\"second\":42}");
  }

  private static class WrapsByte implements Wrapper<Byte> {
    @FallbackOnNull byte first;
    @FallbackOnNull(fallbackByte = 42) byte second;

    @Override public Byte first() {
      return first;
    }

    @Override public Byte second() {
      return second;
    }
  }

  @Test public void charFallbacks() throws Exception {
    assertForClass(WrapsChar.class, '\u0000', 'a', "{\"first\":\"\\u0000\",\"second\":\"a\"}");
  }

  private static class WrapsChar implements Wrapper<Character> {
    @FallbackOnNull char first;
    @FallbackOnNull(fallbackChar = 'a') char second;

    @Override public Character first() {
      return first;
    }

    @Override public Character second() {
      return second;
    }
  }

  @Test public void doubleFallbacks() throws Exception {
    assertForClass(WrapsDouble.class, Double.MIN_VALUE, 12.0,
        "{\"first\":4.9E-324,\"second\":12.0}");
  }

  private static class WrapsDouble implements Wrapper<Double> {
    @FallbackOnNull double first;
    @FallbackOnNull(fallbackDouble = 12.0) double second;

    @Override public Double first() {
      return first;
    }

    @Override public Double second() {
      return second;
    }
  }

  @Test public void floatFallbacks() throws Exception {
    assertForClass(WrapsFloat.class, Float.MIN_VALUE, 16.0f,
        "{\"first\":1.4E-45,\"second\":16.0}");
  }

  private static class WrapsFloat implements Wrapper<Float> {
    @FallbackOnNull float first;
    @FallbackOnNull(fallbackFloat = 16.0f) float second;

    @Override public Float first() {
      return first;
    }

    @Override public Float second() {
      return second;
    }
  }

  @Test public void intFallbacks() throws Exception {
    assertForClass(WrapsInt.class, Integer.MIN_VALUE, -1, "{\"first\":-2147483648,\"second\":-1}");
  }

  @Test public void intFallbacksNoLocaleInfluence() throws Exception {
    Locale.setDefault(new Locale("tr", "TR"));
    assertForClass(WrapsInt.class, Integer.MIN_VALUE, -1, "{\"first\":-2147483648,\"second\":-1}");
  }

  private static class WrapsInt implements Wrapper<Integer> {
    @FallbackOnNull int first;
    @FallbackOnNull(fallbackInt = -1) int second;

    @Override public Integer first() {
      return first;
    }

    @Override public Integer second() {
      return second;
    }
  }

  @Test public void longFallbacks() throws Exception {
    assertForClass(WrapsLong.class, Long.MIN_VALUE, -113L,
        "{\"first\":-9223372036854775808,\"second\":-113}");
  }

  private static class WrapsLong implements Wrapper<Long> {
    @FallbackOnNull long first;
    @FallbackOnNull(fallbackLong = -113) long second;

    @Override public Long first() {
      return first;
    }

    @Override public Long second() {
      return second;
    }
  }

  @Test public void shortFallbacks() throws Exception {
    assertForClass(WrapsShort.class, Short.MIN_VALUE, (short) 121,
        "{\"first\":-32768,\"second\":121}");
  }

  private static class WrapsShort implements Wrapper<Short> {
    @FallbackOnNull short first;
    @FallbackOnNull(fallbackShort = 121) short second;

    @Override public Short first() {
      return first;
    }

    @Override public Short second() {
      return second;
    }
  }

  @Test public void factoryMaintainsOtherAnnotations() throws Exception {
    JsonAdapter<AnotherInt> adapter = moshi.adapter(AnotherInt.class);

    AnotherInt fromJson = adapter.fromJson("{\n"
        + "  \"willFallback\": null,\n"
        + "  \"willMultiply\": 3\n"
        + "}");
    assertThat(fromJson.willFallback).isEqualTo(2);
    assertThat(fromJson.willMultiply).isEqualTo(6);

    String toJson = adapter.toJson(fromJson);
    // Both values should be serialized by the Multiply json adapter.
    assertThat(toJson).isEqualTo("{\"willFallback\":1,\"willMultiply\":3}");
  }

  private static class AnotherInt {
    @FallbackOnNull(fallbackInt = 2) @Multiply int willFallback;
    @FallbackOnNull(fallbackInt = 2) @Multiply int willMultiply;
  }

  @Test public void factoryIgnoresNonPrimitiveTypes() throws Exception {
    List<Class<?>> classes = new ArrayList<Class<?>>() {
      {
        add(Boolean.class);
        add(Byte.class);
        add(Character.class);
        add(Double.class);
        add(Float.class);
        add(Integer.class);
        add(Long.class);
        add(Short.class);
        add(String.class);
        add(Object.class);
      }
    };

    for (Class<?> cls : classes) {
      assertThat(FallbackOnNull.ADAPTER_FACTORY.create(cls, ANNOTATIONS, moshi)).isNull();
    }
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<Integer> adapter = moshi.adapter(int.class, ANNOTATIONS);

    assertThat(adapter.toString())
        .isEqualTo("JsonAdapter(Integer).fallbackOnNull(fallbackInt=-1)");
  }

  private static final Set<? extends Annotation> ANNOTATIONS = Collections.singleton(
      new FallbackOnNull() {

        @Override public Class<? extends Annotation> annotationType() {
          return FallbackOnNull.class;
        }

        @Override public boolean fallbackBoolean() {
          return false;
        }

        @Override public byte fallbackByte() {
          return 0;
        }

        @Override public char fallbackChar() {
          return 0;
        }

        @Override public double fallbackDouble() {
          return 0;
        }

        @Override public float fallbackFloat() {
          return 0;
        }

        @Override public int fallbackInt() {
          return -1; // Only this method will be taken into account
        }

        @Override public long fallbackLong() {
          return 0;
        }

        @Override public short fallbackShort() {
          return 0;
        }
      });

  private <T extends Wrapper<P>, P> void assertForClass(Class<T> cls, P first, P second,
      String asJson) throws IOException {
    JsonAdapter<T> adapter = moshi.adapter(cls);

    T fromJson = adapter.fromJson("{\n"
        + "  \"first\": null,\n"
        + "  \"second\": null\n"
        + "}");
    assertThat(fromJson.first()).isEqualTo(first);
    assertThat(fromJson.second()).isEqualTo(second);

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo(asJson);
  }

  private interface Wrapper<P> {
    P first();

    P second();
  }

  @JsonQualifier
  @Retention(RetentionPolicy.RUNTIME) private @interface Multiply {
    final class MultiplyAdapter {
      @Multiply @FromJson int fromJson(int val) {
        return val * 2;
      }

      @ToJson int toJson(@Multiply int val) {
        return val / 2;
      }
    }
  }
}
