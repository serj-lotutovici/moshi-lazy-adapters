package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * {@link JsonAdapter} that filters null values out.
 */
final class FilterNullsJsonAdapter<T> extends JsonAdapter<T> {
  private final JsonAdapter<T> delegate;

  FilterNullsJsonAdapter(JsonAdapter<T> delegate) {
    this.delegate = delegate;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    return removeNulls(delegate.fromJson(reader));
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    delegate.toJson(writer, removeNulls(value));
  }

  @Override public String toString() {
    return delegate + ".filterNulls()";
  }

  private T removeNulls(final T value) {
    final Iterator<?> it = ((Collection<?>) value).iterator();

    while (it.hasNext()) {
      if (it.next() == null) {
        it.remove();
      }
    }

    return value;
  }
}
