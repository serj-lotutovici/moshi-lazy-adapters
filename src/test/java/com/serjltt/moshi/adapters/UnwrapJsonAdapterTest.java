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
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.ToJson;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class UnwrapJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(UnwrapJsonAdapter.FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .add(new ThrowingAdapter()) // We need to check that exceptions are propagated correctly.
      .build();

  @Test public void oneObject() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\n"
        + "  \"data\": {\n"
        + "    \"1\": {\n"
        + "      \"2\": {\n"
        + "        \"str\": \"test\",\n"
        + "        \"val\": 42\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "}");
    assertThat(fromJson).isNotNull();
    assertThat(fromJson.data.str).isEqualTo("test");
    assertThat(fromJson.data.val).isEqualTo(42);

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"data\":{\"1\":{\"2\":{\"str\":\"test\",\"val\":42}}}}");
  }

  @Test public void arrayOfObjects() throws Exception {
    JsonAdapter<List<Data2>> adapter = moshi.adapter(
        Types.newParameterizedType(List.class, Data2.class));

    List<Data2> fromJson = adapter.fromJson("[\n"
        + "  {\n"
        + "    \"data\": {\n"
        + "      \"1\": {\n"
        + "        \"2\": {\n"
        + "          \"str\": \"funny\",\n"
        + "          \"val\": 42\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "  },\n"
        + "  {\n"
        + "    \"data\": {\n"
        + "      \"1\": {\n"
        + "        \"2\": {\n"
        + "          \"str\": \"prime\",\n"
        + "          \"val\": 43\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "]");
    assertThat(fromJson.get(0).data.str).isEqualTo("funny");
    assertThat(fromJson.get(0).data.val).isEqualTo(42);
    assertThat(fromJson.get(1).data.str).isEqualTo("prime");
    assertThat(fromJson.get(1).data.val).isEqualTo(43);

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("["
        + "{\"data\":{\"1\":{\"2\":{\"str\":\"funny\",\"val\":42}}}},"
        + "{\"data\":{\"1\":{\"2\":{\"str\":\"prime\",\"val\":43}}}}"
        + "]");
  }

  @Test public void nullSafe() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\n"
        + "  \"data\": {\n"
        + "    \"1\": {\n"
        + "      \"2\": null\n"
        + "    }\n"
        + "  }\n"
        + "}");
    assertThat(fromJson.data).isNull();

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"data\":{\"1\":{\"2\":null}}}");
  }

  @Test public void nullSafe2() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\n"
        + "  \"data\": null\n"
        + "}");
    assertThat(fromJson.data).isNull();

    // Here we can't expect {"data":null}, since we don't know on which end the value was null.
    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"data\":{\"1\":{\"2\":null}}}");
  }

  @Test public void fromJsonSkipsNonPathValues() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\n"
        + "  \"data\": {\n"
        + "    \"should_be_skipped\": null,\n"
        + "    \"1\": {\n"
        + "      \"2\": {\n"
        + "        \"str\": \"works\",\n"
        + "        \"val\": 11\n"
        + "      }\n"
        + "    }\n"
        + "\n"
        + "  }\n"
        + "}");

    assertThat(fromJson.data.str).isEqualTo("works");
    assertThat(fromJson.data.val).isEqualTo(11);
  }

  @Test public void fromJsonRemainingPathValues() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\n"
        + "  \"data\": {\n"
        + "    \"1\": {\n"
        + "      \"2\": {\n"
        + "        \"str\": \"works\",\n"
        + "        \"val\": 11\n"
        + "      }\n"
        + "    },\n"
        + "    \"should_be_skipped1\": null,\n"
        + "    \"should_be_skipped2\": null\n"
        + "  }\n"
        + "}");

    assertThat(fromJson.data.str).isEqualTo("works");
    assertThat(fromJson.data.val).isEqualTo(11);
  }

  @Test public void fromJsonOnIncorrectPath() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    try {
      adapter.fromJson("{\n"
          + "  \"data\": {\n"
          + "    \"2\": {\n"
          + "      \"1\": null\n"
          + "    }\n"
          + "  }\n"
          + "}");
      fail();
    } catch (IOException e) {
      assertThat(e).hasMessage("Json object could not be found at expected path [1, 2].");
    }
  }

  @Test public void fromJsonDoesNotSwallowIOExceptions() throws Exception {
    JsonAdapter<Data4> adapter = moshi.adapter(Data4.class);

    try {
      adapter.fromJson("{\n"
          + "  \"th\": {\n"
          + "    \"1\": \"this_will_throw\"\n"
          + "  }\n"
          + "}");
      fail();
    } catch (IOException e) {
      assertThat(e).hasMessage("ThrowingAdapter.fromJson");
    }
  }

  @Test public void fromJsonDoesNotSwallowJsonExceptions() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    try {
      adapter.fromJson("{\n"
          + "  \"data\": {\n"
          + "    \"1\": {\n"
          + "      \"2\": [\n"
          + "        \"this_will_throw\"\n"
          + "      ]\n"
          + "    }\n"
          + "  }\n"
          + "}");
      fail();
    } catch (JsonDataException e) {
      assertThat(e).hasMessage("Expected BEGIN_OBJECT but was BEGIN_ARRAY at path $.data.1.2");
    }
  }

  @Test public void toJsonDoesNotSwallowExceptions() throws Exception {
    JsonAdapter<Data4> adapter = moshi.adapter(Data4.class);

    Data4 data4 = new Data4();
    data4.th = new Throws();
    try {
      adapter.toJson(data4);
      fail();
    } catch (Throwable e) {
      // Moshi wraps write exceptions in an AssertionError
      assertThat(e.getCause()).hasMessage("ThrowingAdapter.toJson");
    }
  }

  @Test public void factoryMaintainsOtherAnnotations() throws Exception {
    JsonAdapter<Data3> adapter = moshi.adapter(Data3.class);

    Data3 fromJson = adapter.fromJson("{\n"
        + "  \"str\": {\n"
        + "    \"1\": \"test\"\n"
        + "  }\n"
        + "}");
    assertThat(fromJson.str).isEqualTo("testCustom");

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"str\":{\"1\":\"test\"}}");
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(String.class, Collections.singleton(
        new UnwrapJson() {
          @Override public String[] value() {
            return new String[] {"1", "2"};
          }

          @Override public Class<? extends Annotation> annotationType() {
            return UnwrapJson.class;
          }
        }));

    assertThat(adapter.toString())
        .isEqualTo("JsonAdapter(String).nullSafe().wrappedIn([1, 2])");
  }

  static class Data1 {
    String str;
    int val;
  }

  static class Data2 {
    @UnwrapJson({"1", "2"}) Data1 data;
  }

  static class Data3 {
    @Custom
    @UnwrapJson("1") String str;
  }

  static class Data4 {
    @UnwrapJson("1") Throws th;
  }

  static class Throws {
  }

  /** String adapter, that will throw on read and write. */
  static final class ThrowingAdapter {
    @FromJson Throws fromJson(String str) throws IOException {
      throw new IOException("ThrowingAdapter.fromJson");
    }

    @ToJson String toJson(Throws th) throws IOException {
      throw new IOException("ThrowingAdapter.toJson");
    }
  }
}
