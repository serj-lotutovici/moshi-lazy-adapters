package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class DeserializeOnlyJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(DeserializeOnly.ADAPTER_FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .build();

  @Test public void deserializeOnly() throws Exception {
    JsonAdapter<Data1> adapter = moshi.adapter(Data1.class);

    Data1 fromJson = adapter.fromJson("{\"data\": \"test\"}");
    assertThat(fromJson.data).isEqualTo("test");

    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");
  }

  @Test public void factoryMaintainsOtherAnnotations() throws Exception {
    JsonAdapter<Data2> adapter = moshi.adapter(Data2.class);

    Data2 fromJson = adapter.fromJson("{\"data\": \"test\"}");
    assertThat(fromJson.data).isEqualTo("testCustom");

    assertThat(adapter.toJson(fromJson)).isEqualTo("{}");
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter = moshi.adapter(String.class, DeserializeOnly.class);

    assertThat(adapter.toString()).isEqualTo("JsonAdapter(String).nullSafe().deserializeOnly()");
  }

  private static class Data1 {
    @DeserializeOnly String data;
  }

  private static class Data2 {
    @DeserializeOnly @Custom String data;
  }
}
