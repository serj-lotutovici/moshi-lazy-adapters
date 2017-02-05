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

public final class SerializeOnlyJsonAdapter<T> extends JsonAdapter<T> {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Set<? extends Annotation> nextAnnotations =
          Types.nextAnnotations(annotations, SerializeOnly.class);
      if (nextAnnotations == null) return null;

      return new SerializeOnlyJsonAdapter<>(moshi.adapter(type, nextAnnotations));
    }
  };

  private final JsonAdapter<T> delegate;

  SerializeOnlyJsonAdapter(JsonAdapter<T> delegate) {
    this.delegate = delegate;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    reader.skipValue();
    return null;
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    delegate.toJson(writer, value);
  }

  @Override public String toString() {
    return delegate + ".serializeOnly()";
  }
}
