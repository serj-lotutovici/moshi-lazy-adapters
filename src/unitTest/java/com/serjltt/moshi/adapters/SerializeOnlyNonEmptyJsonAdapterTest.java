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
import com.squareup.moshi.Moshi;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class SerializeOnlyNonEmptyJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(SerializeOnlyNonEmpty.ADAPTER_FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .build();

  @Test public void serializesOnlyNonEmptyCustomArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"customArray\": [{"
        + "\"data\":\"blub\""
        + "}]\n"
        + "}");
    assertThat(fromJson.customArray).isNotNull().hasSize(1);
    assertThat(fromJson.customArray[0].data).isEqualTo("blub");

    fromJson.customArray = new CustomType[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.customArray = new CustomType[] { new CustomType("blub") };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"customArray\":[{\"data\":\"blub\"}]}");
  }

  @Test public void serializesOnlyNonEmptyByteArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"byteArray\": [1]\n"
        + "}");
    assertThat(fromJson.byteArray).containsExactly((byte) 1);

    fromJson.byteArray = new byte[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.byteArray = new byte[] { 5 };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"byteArray\":[5]}");
  }

  @Test public void serializesOnlyNonEmptyCharArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"charArray\": [\"A\"]\n"
        + "}");
    assertThat(fromJson.charArray).containsExactly((char) 65);

    fromJson.charArray = new char[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.charArray = new char[] { 65 };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"charArray\":[\"A\"]}");
  }

  @Test public void serializesOnlyNonEmptyShortArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"shortArray\": [1]\n"
        + "}");
    assertThat(fromJson.shortArray).containsExactly((short) 1);

    fromJson.shortArray = new short[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.shortArray = new short[] { 5 };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"shortArray\":[5]}");
  }

  @Test public void serializesOnlyNonEmptyIntArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"intArray\": [1]\n"
        + "}");
    assertThat(fromJson.intArray).containsExactly(1);

    fromJson.intArray = new int[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.intArray = new int[] { 5 };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"intArray\":[5]}");
  }

  @Test public void serializesOnlyNonEmptyLongArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"longArray\": [1]\n"
        + "}");
    assertThat(fromJson.longArray).containsExactly(1L);

    fromJson.longArray = new long[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.longArray = new long[] { 5L };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"longArray\":[5]}");
  }

  @Test public void serializesOnlyNonEmptyFloatArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"floatArray\": [1.0]\n"
        + "}");
    assertThat(fromJson.floatArray).containsExactly(1.f);

    fromJson.floatArray = new float[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.floatArray = new float[] { 5f };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"floatArray\":[5.0]}");
  }

  @Test public void serializesOnlyNonEmptyDoubleArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"doubleArray\": [1.0]\n"
        + "}");
    assertThat(fromJson.doubleArray).containsExactly(1.f);

    fromJson.doubleArray = new double[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.doubleArray = new double[] { 5f };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"doubleArray\":[5.0]}");
  }

  @Test public void serializesOnlyNonEmptyBooleanArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"booleanArray\": [false]\n"
        + "}");
    assertThat(fromJson.booleanArray).containsExactly(false);

    fromJson.booleanArray = new boolean[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.booleanArray = new boolean[] { false };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"booleanArray\":[false]}");
  }

  @Test public void serializesOnlyNonEmptyStringArray() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"stringArray\": [\"blub\"]\n"
        + "}");
    assertThat(fromJson.stringArray).containsExactly("blub");

    fromJson.stringArray = new String[0];
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.stringArray = new String[] { "blub" };
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"stringArray\":[\"blub\"]}");
  }

  @Test public void serializesOnlyNonEmptyCollection() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"collection\": [\"blub\"]\n"
        + "}");
    assertThat(fromJson.collection).containsExactly("blub");

    fromJson.collection = new ArrayList<>(0);
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.collection.add("blub");
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"collection\":[\"blub\"]}");
  }

  @Test public void serializesOnlyNonEmptyMap() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\n"
        + "\"map\": {"
        + "\"email\":\"blub\"\n"
        + "}\n"
        + "}");
    assertThat(fromJson.map).containsEntry("email", "blub");

    fromJson.map = new HashMap<>();
    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");

    fromJson.map.put("email", "blub");
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"map\":{\"email\":\"blub\"}}");
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter = moshi.adapter(String[].class, Collections.singleton(
        new SerializeOnlyNonEmpty() {
          @Override public Class<? extends Annotation> annotationType() {
            return SerializeOnlyNonEmpty.class;
          }
        }));

    assertThat(adapter.toString())
        .isEqualTo("JsonAdapter(String).nullSafe().array().nullSafe().serializeOnlyNonEmpty()");
  }

  static class Data1 {
    @SerializeOnlyNonEmpty CustomType[] customArray;
    @SerializeOnlyNonEmpty byte[] byteArray;
    @SerializeOnlyNonEmpty char[] charArray;
    @SerializeOnlyNonEmpty short[] shortArray;
    @SerializeOnlyNonEmpty int[] intArray;
    @SerializeOnlyNonEmpty long[] longArray;
    @SerializeOnlyNonEmpty float[] floatArray;
    @SerializeOnlyNonEmpty double[] doubleArray;
    @SerializeOnlyNonEmpty boolean[] booleanArray;
    @SerializeOnlyNonEmpty String[] stringArray;
    @SerializeOnlyNonEmpty Collection<String> collection;
    @SerializeOnlyNonEmpty Map<String, String> map;
  }

  static class CustomType {
    final String data;

    CustomType(final String data) {
      this.data = data;
    }
  }
}
