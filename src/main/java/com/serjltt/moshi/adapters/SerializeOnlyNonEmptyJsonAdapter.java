package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.serjltt.moshi.adapters.Util.findAnnotation;

/**
 * {@linkplain JsonAdapter} that will not serialize {@code T} when the passed value is empty.
 */
public final class SerializeOnlyNonEmptyJsonAdapter<T> extends JsonAdapter<T> {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Annotation annotation = findAnnotation(annotations, SerializeOnlyNonEmpty.class);
      if (annotation == null) return null;

      Class<?> rawType = Types.getRawType(type);

      if (rawType.isArray() || Collection.class.isAssignableFrom(rawType)
          || Map.class.isAssignableFrom(rawType)) {
        // Clone the set and remove the annotation so that we can pass the remaining set to moshi.
        Set<? extends Annotation> reducedAnnotations = new LinkedHashSet<>(annotations);
        reducedAnnotations.remove(annotation);

        return new SerializeOnlyNonEmptyJsonAdapter<>(moshi.adapter(type, reducedAnnotations));
      }

      return null;
    }
  };

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
