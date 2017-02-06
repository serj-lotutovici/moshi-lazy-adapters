package com.serjltt.moshi.adapters;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class TransientAutoValueTest {
  private final Moshi moshi = new Moshi.Builder()
      .add(Transient.ADAPTER_FACTORY)
      .add(DataFactories.create())
      .build();

  @Test public void serialize() {
    final String json = AutoValueClass.jsonAdapter(moshi)
        .toJson(new AutoValue_TransientAutoValueTest_AutoValueClass(1, 2));
    assertThat(json).isEqualTo("{\"foo\":1}");
  }

  @Test public void deserialize() throws IOException {
    final AutoValueClass autoValueClass =
        AutoValueClass.jsonAdapter(moshi).fromJson("{\"foo\": 1,\"bar\": 2}");

    assertThat(autoValueClass.foo()).isEqualTo(1);
    assertThat(autoValueClass.bar()).isNull();
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter =
        moshi.adapter(String.class, Collections.singleton(new SerializeOnly() {
          @Override public Class<? extends Annotation> annotationType() {
            return Transient.class;
          }
        }));

    assertThat(adapter.toString()).isEqualTo("JsonAdapter(String).nullSafe().transient()");
  }

  @AutoValue abstract static class AutoValueClass {
    public static JsonAdapter<AutoValueClass> jsonAdapter(Moshi moshi) {
      return new AutoValue_TransientAutoValueTest_AutoValueClass.MoshiJsonAdapter(moshi);
    }

    abstract Integer foo();

    @Transient @Nullable abstract Integer bar();
  }
}
