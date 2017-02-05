package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

public final class DeserializeOnlyJsonAdapter<T> extends JsonAdapter<T> {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Set<? extends Annotation> nextAnnotations =
          Types.nextAnnotations(annotations, DeserializeOnly.class);
      if (nextAnnotations == null) return null;

      return new DeserializeOnlyJsonAdapter<>(moshi.adapter(type, nextAnnotations));
    }
  };

  private final JsonAdapter<T> delegate;

  DeserializeOnlyJsonAdapter(JsonAdapter<T> delegate) {
    this.delegate = delegate;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    return delegate.fromJson(reader);
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    // We'll need to consume this property otherwise we'll get an IllegalArgumentException.
    delegate.toJson(writer, null);
  }

  @Override public String toString() {
    return delegate + ".deserializeOnly()";
  }
}
