package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.serjltt.moshi.adapters.Util.findAnnotation;

public final class DeserializeOnlyJsonAdapter<T> extends JsonAdapter<T> {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Annotation annotation = findAnnotation(annotations, DeserializeOnly.class);
      if (annotation == null) return null;

      // Clone the set and remove the annotation so that we can pass the remaining set to moshi.
      Set<? extends Annotation> reducedAnnotations = new LinkedHashSet<>(annotations);
      reducedAnnotations.remove(annotation);

      return new DeserializeOnlyJsonAdapter<>(moshi.adapter(type, reducedAnnotations));
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
