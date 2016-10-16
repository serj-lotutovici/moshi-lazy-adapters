package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.lang.annotation.Annotation;
import java.util.Collections;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class SerializeOnlyJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(SerializeOnlyJsonAdapter.FACTORY)
      .add(SerializeNullsJsonAdapter.FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .build();

  @Test public void serializeOnly() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\"data\": \"test\"}");
    assertThat(fromJson.data).isNull();

    fromJson.data = "1234";
    assertThat(adapter.toJson(fromJson)).isEqualTo("{\"data\":\"1234\"}");
  }

  @Test public void factoryMaintainsOtherAnnotations() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\"data\": \"test\"}");
    assertThat(fromJson.data).isNull();

    fromJson.data = "1234Custom";
    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("{\"data\":\"1234\"}");

    Data2 data = new Data2();
    data.data = null;
    toJson = adapter.toJson(data);
    assertThat(toJson).isEqualTo("{\"data\":null}");
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter =
        moshi.adapter(String.class, Collections.singleton(new SerializeOnly() {
          @Override public Class<? extends Annotation> annotationType() {
            return SerializeOnly.class;
          }
        }));

    assertThat(adapter.toString()).isEqualTo("JsonAdapter(String).nullSafe().serializeOnly()");
  }

  private static class Data1 {
    @SerializeOnly String data;
  }

  private static class Data2 {
    @Custom @SerializeOnly @SerializeNulls String data;
  }
}
