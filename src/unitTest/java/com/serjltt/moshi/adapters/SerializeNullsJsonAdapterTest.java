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

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import java.lang.annotation.Annotation;
import java.util.Collections;
import okio.Buffer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class SerializeNullsJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(SerializeNullsJsonAdapter.FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .build();

  @Test public void serializesNulls() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "  \"data\": null\n"
        + "}");
    assertThat(fromJson.data).isNull();

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"data\":null}");
  }

  @Test public void factoryMaintainsOtherAnnotations() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\n"
        + "  \"data\": \"val\"\n"
        + "}");
    assertThat(fromJson.data).isEqualTo("valCustom");

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"data\":\"val\"}");

    Data2 data = new Data2();
    data.data = null;
    toJson = adapter.toJson(data);
    assertThat(toJson).isEqualTo("{\"data\":null}");
  }

  @Test public void maintainsPreviousSerializationValue() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);
    Data1 data1 = new Data1();

    JsonWriter writer1 = JsonWriter.of(new Buffer());
    writer1.setSerializeNulls(true);
    adapter.toJson(writer1, data1);
    assertThat(writer1.getSerializeNulls()).isTrue();

    JsonWriter writer2 = JsonWriter.of(new Buffer());
    writer2.setSerializeNulls(false);
    adapter.toJson(writer2, data1);
    assertThat(writer2.getSerializeNulls()).isFalse();
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter = moshi.adapter(String.class, Collections.singleton(
        new SerializeNulls() {
          @Override public Class<? extends Annotation> annotationType() {
            return SerializeNulls.class;
          }
        }));

    assertThat(adapter.toString())
        .isEqualTo("JsonAdapter(String).nullSafe().serializeNulls()");
  }

  private static class Data1 {
    @SerializeNulls String data;
  }

  private static class Data2 {
    @Custom
    @SerializeNulls String data;
  }
}
