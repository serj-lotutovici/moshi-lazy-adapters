package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * {@linkplain JsonAdapter} that will not serialize {@code T} when the passed value is empty.
 */
final class SerializeOnlyNonEmptyJsonAdapter<T> extends JsonAdapter<T> {
  private final JsonAdapter<T> delegate;

  SerializeOnlyNonEmptyJsonAdapter(JsonAdapter<T> delegate) {
    this.delegate = delegate;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    return delegate.fromJson(reader);
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    if (isNotEmpty(value)) {
      delegate.toJson(writer, value);
    } else {
      // We'll need to consume this property otherwise we'll get an IllegalArgumentException.
      delegate.toJson(writer, null);
    }
  }

  private boolean isNotEmpty(final T value) {
    if (value instanceof Collection) {
      Collection collection = (Collection) value;
      return collection.size() > 0;
    } else if (value instanceof Map) {
      Map map = (Map) value;
      return map.size() > 0;
    } else if (value != null) {
      return Array.getLength(value) > 0;
    }

    return false;
  }

  @Override public String toString() {
    return delegate + ".serializeOnlyNonEmpty()";
  }
}
