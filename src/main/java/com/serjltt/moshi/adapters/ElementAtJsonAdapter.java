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
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.serjltt.moshi.adapters.Util.findAnnotation;

/**
 * {@linkplain JsonAdapter} that extracts the element at the given index
 * of an array of (a field) type annotated with {@linkplain ElementAt}.
 */
public final class ElementAtJsonAdapter<T> extends JsonAdapter<T> {
  public static final Factory FACTORY = new Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Annotation annotation = findAnnotation(annotations, ElementAt.class);
      if (annotation == null) return null;

      // Clone the set and remove the annotation so that we can pass the remaining set to moshi.
      Set<? extends Annotation> reducedAnnotations = new LinkedHashSet<>(annotations);
      reducedAnnotations.remove(annotation);

      ElementAt wrapped = (ElementAt) annotation;
      return new ElementAtJsonAdapter<>(type, moshi, reducedAnnotations, wrapped.index());
    }
  };

  private final JsonAdapter<List<T>> adapter;
  private final int index;

  ElementAtJsonAdapter(Type type, Moshi moshi,
    Set<? extends Annotation> reducedAnnotations, int index) {
    Type listType = Types.newParameterizedType(List.class, type);
    adapter = moshi.adapter(listType, reducedAnnotations);
    this.index = index;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    List<T> fromJson = adapter.fromJson(reader);
    if (fromJson != null && index < fromJson.size()) return fromJson.get(index);
    return null;
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    adapter.toJson(writer, Collections.singletonList(value));
  }

  @Override public String toString() {
    return adapter + ".elementAt(" + index + ")";
  }
}
