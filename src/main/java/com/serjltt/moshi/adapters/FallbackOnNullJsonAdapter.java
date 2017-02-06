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
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@linkplain JsonAdapter} that fallbacks to a default value of a primitive field annotated with
 * {@linkplain FallbackOnNull}.
 */
final class FallbackOnNullJsonAdapter<T> extends JsonAdapter<T> {
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

  final JsonAdapter<T> dalegate;
  final T fallback;
  final String fallbackType;

  FallbackOnNullJsonAdapter(JsonAdapter<T> dalegate, T fallback, String fallbackType) {
    this.dalegate = dalegate;
    this.fallback = fallback;
    this.fallbackType = fallbackType;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    if (reader.peek() == JsonReader.Token.NULL) {
      reader.nextNull(); // We need to consume the value.
      return fallback;
    }
    return dalegate.fromJson(reader);
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    dalegate.toJson(writer, value);
  }

  @Override public String toString() {
    return dalegate + ".fallbackOnNull(" + fallbackType + '=' + fallback + ')';
  }
}
