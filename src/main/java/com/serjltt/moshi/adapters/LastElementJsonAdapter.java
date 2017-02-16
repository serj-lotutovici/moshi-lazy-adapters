package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * {@linkplain JsonAdapter} that extracts the last element
 * of an array of (a field) type or a method annotated with {@linkplain LastElement}.
 */
final class LastElementJsonAdapter<T> extends JsonAdapter<T> {
  private final JsonAdapter<List<T>> delegate;

  LastElementJsonAdapter(Type type, Moshi moshi) {
    Type listType = Types.newParameterizedType(List.class, type);
    delegate = moshi.adapter(listType);
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    List<T> fromJson = delegate.fromJson(reader);
    if (fromJson != null && !fromJson.isEmpty()) return fromJson.get(fromJson.size() - 1);
    return null;
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    delegate.toJson(writer, Collections.singletonList(value));
  }

  @Override public String toString() {
    return delegate + ".lastElement()";
  }
}
