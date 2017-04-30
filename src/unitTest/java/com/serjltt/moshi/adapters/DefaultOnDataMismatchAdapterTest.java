package com.serjltt.moshi.adapters;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static com.serjltt.moshi.adapters.DefaultOnDataMismatchAdapterTest.Fruit.APPLE;
import static com.serjltt.moshi.adapters.DefaultOnDataMismatchAdapterTest.Fruit.BANANA;
import static org.assertj.core.api.Java6Assertions.assertThat;

public final class DefaultOnDataMismatchAdapterTest {
  @Test public void deserializeMismatch() throws IOException {
    Fruit fruit = buildMoshi(newFruitFactory()).adapter(Fruit.class).fromJson("\"mango\"");
    assertThat(fruit).isNull();
  }

  @Test public void deserializeMatch() throws IOException {
    Fruit fruit = buildMoshi(newFruitFactory()).adapter(Fruit.class).fromJson("\"banana\"");
    assertThat(fruit).isEqualTo(BANANA);
  }

  @Test public void serialize() {
    String fruit = buildMoshi(newFruitFactory()).adapter(Fruit.class).toJson(APPLE);
    assertThat(fruit).isEqualTo("\"apple\"");
  }

  @Test public void factorySupportsType() throws Exception {
    Type parameterized = Types.newParameterizedType(List.class, String.class);
    List<String> fallback = Collections.singletonList("test");

    // Build a moshi instance using the adapter under test and one that will throw on each read.
    Moshi moshi = buildMoshi(DefaultOnDataMismatchAdapter.newFactory(parameterized, fallback))
        .newBuilder()
        .add(new JsonAdapter.Factory() {
          @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
              Moshi moshi) {
            final JsonAdapter<Object> next = moshi.nextAdapter(this, type, annotations);
            return new JsonAdapter<Object>() {
              @Override public Object fromJson(JsonReader reader) throws IOException {
                throw new JsonDataException("Fail for all types");
              }

              @Override public void toJson(JsonWriter writer, Object value) throws IOException {
                next.toJson(writer, value);
              }
            };
          }
        })
        .build();
    JsonAdapter<List<String>> adapter = moshi.adapter(parameterized);

    List<String> fromJson = adapter.fromJson("[]");
    assertThat(fromJson)
        .isEqualTo(fallback)
        .containsExactly("test");

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("[\"test\"]");
  }

  @Test public void toStringReflectsInner() {
    assertThat(buildMoshi(newFruitFactory()).adapter(Fruit.class).toString())
        .isEqualTo("JsonAdapter(com.serjltt.moshi.adapters.DefaultOnDataMismatchAdapterTest$Fruit)"
            + ".nullSafe().defaultOnDatMisMatch(null)");
  }

  private JsonAdapter.Factory newFruitFactory() {
    return DefaultOnDataMismatchAdapter.newFactory(Fruit.class, null);
  }

  private Moshi buildMoshi(JsonAdapter.Factory factory) {
    return new Moshi.Builder()
        .add(factory)
        .build();
  }

  enum Fruit {
    @Json(name = "banana")BANANA,
    @Json(name = "apple")APPLE
  }
}
