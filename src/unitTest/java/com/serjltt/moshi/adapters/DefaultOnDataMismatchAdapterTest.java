package com.serjltt.moshi.adapters;

import com.squareup.moshi.Json;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import org.junit.Test;

import static com.serjltt.moshi.adapters.DefaultOnDataMismatchAdapterTest.Fruit.APPLE;
import static com.serjltt.moshi.adapters.DefaultOnDataMismatchAdapterTest.Fruit.BANANA;
import static org.assertj.core.api.Java6Assertions.assertThat;

public final class DefaultOnDataMismatchAdapterTest {
  private Moshi moshi = new Moshi.Builder()
      .add(DefaultOnDataMismatchAdapter.newFactory(Fruit.class, null))
      .build();

  @Test public void deserializeMismatch() throws IOException {
    Fruit fruit = moshi.adapter(Fruit.class).fromJson("\"mango\"");
    assertThat(fruit).isNull();
  }

  @Test public void deserializeMatch() throws IOException {
    Fruit fruit = moshi.adapter(Fruit.class).fromJson("\"banana\"");
    assertThat(fruit).isEqualTo(BANANA);
  }

  @Test public void serialize() {
    String fruit = moshi.adapter(Fruit.class).toJson(APPLE);
    assertThat(fruit).isEqualTo("\"apple\"");
  }

  @Test public void toStringReflectsInner() {
    assertThat(moshi.adapter(Fruit.class).toString())
        .isEqualTo("JsonAdapter(com.serjltt.moshi.adapters.DefaultOnDataMismatchAdapterTest$Fruit)"
            + ".nullSafe().defaultOnDatMisMatch(null)");
  }

  enum Fruit {
    @Json(name = "banana") BANANA,
    @Json(name = "apple") APPLE
  }
}
