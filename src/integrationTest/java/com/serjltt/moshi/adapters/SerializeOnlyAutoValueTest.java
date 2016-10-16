package com.serjltt.moshi.adapters;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class SerializeOnlyAutoValueTest {
  private final Moshi moshi = new Moshi.Builder()
      .add(SerializeOnlyJsonAdapter.FACTORY)
      .add(DataFactories.create())
      .build();

  @Test public void serialize() {
    final String json = AutoValueClass.jsonAdapter(moshi)
        .toJson(new AutoValue_SerializeOnlyAutoValueTest_AutoValueClass(1, 2));
    assertThat(json).isEqualTo("{\"foo\":1,\"bar\":2}");
  }

  @Test public void deserialize() throws IOException {
    final AutoValueClass autoValueClass =
        AutoValueClass.jsonAdapter(moshi).fromJson("{\"foo\": 1,\"bar\": 2}");

    assertThat(autoValueClass.foo()).isEqualTo(1);
    assertThat(autoValueClass.bar()).isNull();
  }

  @AutoValue abstract static class AutoValueClass {
    public static JsonAdapter<AutoValueClass> jsonAdapter(Moshi moshi) {
      return new AutoValue_SerializeOnlyAutoValueTest_AutoValueClass.MoshiJsonAdapter(moshi);
    }

    abstract Integer foo();

    @SerializeOnly @Nullable abstract Integer bar();
  }
}
