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

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class FirstElementJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(FirstElement.ADAPTER_FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .build();

  @Test public void first() throws Exception {
    JsonAdapter<Data> adapter = moshi.adapter(Data.class);

    Data fromJson = adapter.fromJson("{\n"
        + "  \"obj\": [\n"
        + "    \"one\",\n"
        + "    \"two\"\n"
        + "  ]\n"
        + "}");
    assertThat(fromJson.str).isEqualTo("one");

    String toJson = adapter.toJson(fromJson);
    // The excluded data is lost during parsing
    // Adapter under test assumes that the consumer doesn't need that data
    assertThat(toJson).isEqualTo("{\"obj\":[\"one\"]}");
  }

  @Test public void fromJsonOnEmptyArrayReturnsNull() throws Exception {
    assertNullReturn("{\n"
        + "  \"obj\": []\n"
        + "}");
  }

  @Test public void fromJsonOnNullArrayReturnsNull() throws Exception {
    assertNullReturn("{\n"
        + "  \"obj\": null\n"
        + "}");
  }

  @Test public void fromJsonExpectsAnArray() throws Exception {
    JsonAdapter<Data> adapter = moshi.adapter(Data.class);

    try {
      adapter.fromJson("{\n"
          + "  \"obj\": \"this_will_throw\"\n"
          + "}");
      fail();
    } catch (JsonDataException e) {
      // Moshi's Collection adapter will throw
      assertThat(e).hasMessage("Expected BEGIN_ARRAY but was STRING at path $.obj");
    }
  }

  // Currently there is no way to create an adapter
  @Test @Ignore public void factoryMaintainsOtherAnnotations() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\n"
        + "  \"str\": [\n"
        + "    \"test\"\n"
        + "  ]\n"
        + "}");
    assertThat(fromJson.str).isEqualTo("testCustom");

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"str\":[\"test\"]}");
  }

  // This one is redundant, but keeps JaCoCo quiet
  @Test public void factoryExpectsOnlyOneAnnotation() throws Exception {
    // A list of fake annotations.
    Set<Annotation> annotations = new LinkedHashSet<Annotation>() {
      {
        add(new Annotation() {
          @Override public Class<? extends Annotation> annotationType() {
            return Test.class;
          }
        });
        add(new Annotation() {
          @Override public Class<? extends Annotation> annotationType() {
            return Custom.class;
          }
        });
      }
    };
    assertThat(FirstElement.ADAPTER_FACTORY.create(String.class, annotations, moshi)).isNull();

    // Emulate existing annotation (should also return null).
    annotations.add(new Annotation() {
      @Override public Class<? extends Annotation> annotationType() {
        return FirstElement.class;
      }
    });
    assertThat(FirstElement.ADAPTER_FACTORY.create(String.class, annotations, moshi)).isNull();
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter = moshi.adapter(String.class,
        Collections.singleton(new FirstElement() {
          @Override public Class<? extends Annotation> annotationType() {
            return FirstElement.class;
          }
        }));

    assertThat(adapter.toString())
        .isEqualTo("JsonAdapter(String).nullSafe().collection().nullSafe().elementAt(0)");
  }

  private void assertNullReturn(String string) throws IOException {
    JsonAdapter<Data> adapter = moshi.adapter(Data.class);

    Data fromJson = adapter.fromJson(string);
    assertThat(fromJson.str).isNull();

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"obj\":[null]}");
  }

  private static class Data {
    @FirstElement
    @Json(name = "obj") String str;
  }

  private static class Data2 {
    @FirstElement
    @Custom String str;
  }
}
