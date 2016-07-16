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
import java.util.List;
import java.util.Set;

import static com.serjltt.moshi.adapters.Util.hasAnnotation;

/**
 * {@linkplain JsonAdapter} that extracts the first element of an array of (a field) type annotated
 * with {@linkplain FirstElement}.
 */
public final class FirstElementJsonAdapter<T> extends JsonAdapter<T> {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      if (hasAnnotation(annotations, FirstElement.class) && annotations.size() == 1) {
        return new FirstElementJsonAdapter<>(type, moshi);
      }
      return null;
    }
  };

  private final JsonAdapter<List<T>> adapter;

  FirstElementJsonAdapter(Type type, Moshi moshi) {
    Type listType = Types.newParameterizedType(List.class, type);
    adapter = moshi.adapter(listType);
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    List<T> fromJson = adapter.fromJson(reader);
    if (fromJson != null && !fromJson.isEmpty()) return fromJson.get(0);
    return null;
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    adapter.toJson(writer, Collections.singletonList(value));
  }

  @Override public String toString() {
    return adapter + ".first()";
  }
}
