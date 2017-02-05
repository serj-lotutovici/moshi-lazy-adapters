/*
 * Copyright 2016 Serj Lotutovici
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.serjltt.moshi.adapters.Util.nextAnnotations;

/**
 * {@linkplain JsonAdapter} that fallbacks to a default value of a primitive field annotated with
 * {@linkplain FallbackOnNull}.
 */
public final class FallbackOnNullJsonAdapter<T> extends JsonAdapter<T> {
  /** Set of primitives classes that are supported by <strong>this</strong> adapter. */
  static final Set<Class<?>> PRIMITIVE_CLASSES = new LinkedHashSet<>();

  static {
    PRIMITIVE_CLASSES.add(boolean.class);
    PRIMITIVE_CLASSES.add(byte.class);
    PRIMITIVE_CLASSES.add(char.class);
    PRIMITIVE_CLASSES.add(double.class);
    PRIMITIVE_CLASSES.add(float.class);
    PRIMITIVE_CLASSES.add(int.class);
    PRIMITIVE_CLASSES.add(long.class);
    PRIMITIVE_CLASSES.add(short.class);
  }

  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Pair<FallbackOnNull, Set<Annotation>> nextAnnotations =
          nextAnnotations(annotations, FallbackOnNull.class);
      if (nextAnnotations == null) return null;

      Class<?> rawType = Types.getRawType(type);
      if (!PRIMITIVE_CLASSES.contains(rawType)) return null;

      String fallbackType = fallbackType(rawType);
      Object fallback = retrieveFallback(nextAnnotations.first, fallbackType);

      return new FallbackOnNullJsonAdapter<>(moshi.adapter(type, nextAnnotations.second),
          fallback, fallbackType);
    }

    /** Invokes the appropriate fallback method based on the {@code fallbackType}. */
    private Object retrieveFallback(FallbackOnNull annotation, String fallbackType) {
      try {
        Method fallbackMethod = FallbackOnNull.class.getMethod(fallbackType);
        return fallbackMethod.invoke(annotation);
      } catch (Exception e) {
        throw new AssertionError(e);
      }
    }

    /** Constructs the appropriate fallback method name based on the {@code rawType}. */
    private String fallbackType(Class<?> rawType) {
      String typeName = rawType.getSimpleName();
      String methodSuffix = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
      return "fallback" + methodSuffix;
    }
  };

  final JsonAdapter<T> adapter;
  final T fallback;
  final String fallbackType;

  FallbackOnNullJsonAdapter(JsonAdapter<T> adapter, T fallback, String fallbackType) {
    this.adapter = adapter;
    this.fallback = fallback;
    this.fallbackType = fallbackType;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    if (reader.peek() == JsonReader.Token.NULL) {
      reader.nextNull(); // We need to consume the value.
      return fallback;
    }
    return adapter.fromJson(reader);
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    adapter.toJson(writer, value);
  }

  @Override public String toString() {
    return adapter + ".fallbackOnNull(" + fallbackType + '=' + fallback + ')';
  }
}
