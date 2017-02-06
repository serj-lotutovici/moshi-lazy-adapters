package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;

/**
 * {@link JsonAdapter} with transient functionality. The consumer can decide to ether serialize or
 * deserialize, or make the adapter completely transient.
 */
final class TransientJsonAdapter<T> extends JsonAdapter<T> {
  private final JsonAdapter<T> delegate;
  private final boolean serialize;
  private final boolean deserialize;

  TransientJsonAdapter(JsonAdapter<T> delegate, boolean serialize, boolean deserialize) {
    this.delegate = delegate;
    this.serialize = serialize;
    this.deserialize = deserialize;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    if (deserialize) {
      return delegate.fromJson(reader);
    } else {
      reader.skipValue();
      return null;
    }
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    if (serialize) {
      delegate.toJson(writer, value);
    } else {
      // We'll need to consume this property otherwise we'll get an IllegalArgumentException.
      delegate.toJson(writer, null);
    }
  }

  @Override public String toString() {
    return delegate + ((serialize && deserialize)
        ? "" : serialize
        ? ".serializeOnly()" : deserialize
        ? ".deserializeOnly()" : ".transient()");
  }
}
