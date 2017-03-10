package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Adapter that fallbacks to a default value in case there's a mismatch.
 */
public final class DefaultOnDataMismatchAdapter<T> extends JsonAdapter<T> {
  private final JsonAdapter<T> delegate;
  private final T defaultValue;

  DefaultOnDataMismatchAdapter(JsonAdapter<T> delegate, T defaultValue) {
    this.delegate = delegate;
    this.defaultValue = defaultValue;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    Object jsonValue = reader.readJsonValue();

    try {
      return delegate.fromJsonValue(jsonValue);
    } catch (JsonDataException ignore) {
      return defaultValue;
    }
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    delegate.toJson(writer, value);
  }

  /** Builds an adapter that fallbacks to a default value in case there's a mismatch. */
  public static <T> JsonAdapter.Factory newFactory(final Class<T> type, final T defaultValue) {
    return new Factory() {
      @Override public JsonAdapter<?> create(Type requestedType,
          Set<? extends Annotation> annotations, Moshi moshi) {
        if (type == requestedType) {
          JsonAdapter<T> delegate = moshi.nextAdapter(this, type, annotations);
          return new DefaultOnDataMismatchAdapter<>(delegate, defaultValue);
        }

        return null;
      }
    };
  }
}
