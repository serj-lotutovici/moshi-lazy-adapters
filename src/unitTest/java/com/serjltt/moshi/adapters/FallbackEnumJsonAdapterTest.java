/*
 * Copyright 2016 Serj Lotutovici
 * Copyright (C) 2014 Square, Inc.
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

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import java.lang.annotation.Annotation;
import java.util.Collections;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class FallbackEnumJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(FallbackEnum.ADAPTER_FACTORY)
      .build();

  @Test public void asRegularEnumAdapter() throws Exception {
    JsonAdapter<Roshambo> adapter = moshi.adapter(Roshambo.class).lenient();
    assertThat(adapter.fromJson("\"ROCK\"")).isEqualTo(Roshambo.ROCK);
    assertThat(adapter.toJson(Roshambo.PAPER)).isEqualTo("\"PAPER\"");
    // Check annotated value
    assertThat(adapter.fromJson("\"scr\"")).isEqualTo(Roshambo.SCISSORS);
    assertThat(adapter.toJson(Roshambo.SCISSORS)).isEqualTo("\"scr\"");
  }

  @Test public void fallbackEnum() throws Exception {
    JsonAdapter<Roshambo> adapter = moshi.adapter(Roshambo.class).lenient();
    assertThat(adapter.fromJson("\"SPOCK\"")).isEqualTo(Roshambo.UNKNOWN);
  }

  @Test public void nullEnum() throws Exception {
    JsonAdapter<Roshambo> adapter = moshi.adapter(Roshambo.class).lenient();
    assertThat(adapter.fromJson("null")).isNull();
    assertThat(adapter.toJson(null)).isEqualTo("null");
  }

  @Test public void throwsOnInvalidFallback() throws Exception {
    try {
      moshi.adapter(Value.class);
      fail();
    } catch (Error ex) {
      assertThat(ex).hasMessage("Missing field in "
          + "com.serjltt.moshi.adapters.FallbackEnumJsonAdapterTest$Value");
      assertThat(ex.getCause()).hasMessage("Filed \"UNK\" is not declared.");
    }
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<Roshambo> adapter = moshi.adapter(Roshambo.class);

    assertThat(adapter.toString()).isEqualTo(
        "JsonAdapter(com.serjltt.moshi.adapters.FallbackEnumJsonAdapterTest$Roshambo)"
            + ".fallbackEnum(UNKNOWN).nullSafe()");
  }

  @Test public void ignoresUnannotatedEnums() throws Exception {
    JsonAdapter<Regular> adapter = moshi.adapter(Regular.class).lenient();
    assertThat(adapter.fromJson("\"ONE\"")).isEqualTo(Regular.ONE);

    try {
      adapter.fromJson("\"TWO\"");
      fail();
    } catch (JsonDataException expected) {
      assertThat(expected).hasMessage(
          "Expected one of [ONE] but was TWO at path $");
    }
  }

  @Test public void factoryIgnoresUnsupportedTypes() throws Exception {
    JsonAdapter<?> adapter1 = FallbackEnum.ADAPTER_FACTORY
        .create(String.class, Collections.<Annotation>emptySet(), moshi);
    assertThat(adapter1).isNull();

    JsonAdapter<?> adapter2 = FallbackEnum.ADAPTER_FACTORY
        .create(Roshambo.class, Collections.singleton(Wrapped.Factory.create("")), moshi);
    assertThat(adapter2).isNull();
  }

  @FallbackEnum(name = "UNKNOWN") enum Roshambo {
    ROCK,
    PAPER,
    @Json(name = "scr")SCISSORS,
    UNKNOWN
  }

  @FallbackEnum(name = "UNK") enum Value {
    @SuppressWarnings("unused")UNKNOWN
  }

  enum Regular {
    ONE
  }
}
