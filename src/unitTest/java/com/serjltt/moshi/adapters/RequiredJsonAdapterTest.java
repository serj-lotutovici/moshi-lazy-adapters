package com.serjltt.moshi.adapters;

import com.serjltt.moshi.adapters.RequiredJsonAdapter.Required;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public final class RequiredJsonAdapterTest {
  private final Moshi moshi = new Moshi.Builder().add(RequiredJsonAdapter.FACTORY).build();

  @Test public void requiredFieldMissingFails() throws Exception {
    String json = "{}";
    JsonAdapter<StringData> adapter = moshi.adapter(StringData.class);
    try {
      adapter.fromJson(json);
      fail("Deserialization should fail with missing @Required value.");
    } catch (IllegalStateException expected) {
      assertThat(expected).hasMessage("Required field data in class "
          + "com.serjltt.moshi.adapters.RequiredJsonAdapterTest$StringData was null.");
    }
  }

  @Test public void requiredFieldNullFails() throws Exception {
    String json = "{\"data\": null}";
    JsonAdapter<StringData> adapter = moshi.adapter(StringData.class);
    try {
      adapter.fromJson(json);
      fail("Deserialization should fail with null @Required value.");
    } catch (IllegalStateException expected) {
      assertThat(expected).hasMessage("Required field data in class "
          + "com.serjltt.moshi.adapters.RequiredJsonAdapterTest$StringData was null.");
    }
  }

  @Test public void requiredPrimitiveFieldDisallowed() throws Exception {
    try {
      JsonAdapter<IntData> adapter = moshi.adapter(IntData.class);
      fail("Adapter should disallow primitives annotated with @Required.");
    } catch (IllegalStateException expected) {
      assertThat(expected).hasMessage("Primitives may not be annotated with @Required.");
    }
  }

  private static class StringData {
    @Required String data;
  }

  private static class IntData {
    @Required int data;
  }
}
