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

public final class LastElementJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(LastElement.ADAPTER_FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .build();

  @Test public void last() throws Exception {
    JsonAdapter<LastElementJsonAdapterTest.Data>
        adapter = moshi.adapter(LastElementJsonAdapterTest.Data.class);

    LastElementJsonAdapterTest.Data fromJson = adapter.fromJson("{\n"
        + "  \"obj\": [\n"
        + "    \"one\",\n"
        + "    \"two\"\n"
        + "  ]\n"
        + "}");
    assertThat(fromJson.str).isEqualTo("two");

    String toJson = adapter.toJson(fromJson);
    // The excluded data is lost during parsing
    // Adapter under test assumes that the consumer doesn't need that data
    assertThat(toJson).isEqualTo("{\"obj\":[\"two\"]}");
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
    JsonAdapter<LastElementJsonAdapterTest.Data> adapter = moshi.adapter(Data.class);

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
    JsonAdapter<LastElementJsonAdapterTest.Data2> adapter = moshi.adapter(Data2.class);

    LastElementJsonAdapterTest.Data2 fromJson = adapter.fromJson("{\n"
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
    assertThat(LastElement.ADAPTER_FACTORY.create(String.class, annotations, moshi)).isNull();

    // Emulate existing annotation (should also return null).
    annotations.add(new Annotation() {
      @Override public Class<? extends Annotation> annotationType() {
        return LastElement.class;
      }
    });
    assertThat(LastElement.ADAPTER_FACTORY.create(String.class, annotations, moshi)).isNull();
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter = moshi.adapter(String.class,
        Collections.singleton(new LastElement() {
          @Override public Class<? extends Annotation> annotationType() {
            return LastElement.class;
          }
        }));

    assertThat(adapter.toString())
        .isEqualTo("JsonAdapter(String).nullSafe().collection().nullSafe().lastElement");
  }

  private void assertNullReturn(String string) throws IOException {
    JsonAdapter<LastElementJsonAdapterTest.Data> adapter = moshi.adapter(Data.class);

    LastElementJsonAdapterTest.Data fromJson = adapter.fromJson(string);
    assertThat(fromJson.str).isNull();

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"obj\":[null]}");
  }

  private static class Data {
    @LastElement
    @Json(name = "obj") String str;
  }

  private static class Data2 {
    @LastElement
    @Custom String str;
  }
}
