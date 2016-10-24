/*
 * Copyright 2016 Serj Lotutovici
 * Copyright (C) 2014 Square, Inc.
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

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@linkplain JsonAdapter} that fallbacks to a default enum constant declared in the enum type
 * annotated with {@linkplain FallbackEnum}.
 */
public final class FallbackEnumJsonAdapter<T extends Enum<T>> extends JsonAdapter<T> {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      if (!annotations.isEmpty()) return null;

      Class<?> rawType = Types.getRawType(type);
      if (rawType.isEnum()) {
        FallbackEnum annotation = rawType.getAnnotation(FallbackEnum.class);
        if (annotation == null) return null;

        //noinspection unchecked
        return new FallbackEnumJsonAdapter<>((Class<? extends Enum>) rawType, annotation.name())
            .nullSafe();
      }

      return null;
    }
  };

  private final Class<T> enumType;
  private final T fallbackConstant;
  private final Map<String, T> nameConstantMap;
  private final String[] nameStrings;

  FallbackEnumJsonAdapter(Class<T> enumType, String fallback) {
    this.enumType = enumType;

    try {
      int fallbackConstantIndex = -1;
      T[] constants = enumType.getEnumConstants();
      nameConstantMap = new LinkedHashMap<>();
      nameStrings = new String[constants.length];

      for (int i = 0; i < constants.length; i++) {
        T constant = constants[i];
        Json annotation = enumType.getField(constant.name()).getAnnotation(Json.class);
        String name = annotation != null ? annotation.name() : constant.name();
        nameConstantMap.put(name, constant);
        nameStrings[i] = name;

        if (fallback.equals(constant.name())) {
          fallbackConstantIndex = i;
        }
      }

      if (fallbackConstantIndex != -1) {
        fallbackConstant = constants[fallbackConstantIndex];
      } else {
        throw new NoSuchFieldException("Filed \"" + fallback + "\" is not declared.");
      }
    } catch (NoSuchFieldException e) {
      throw new AssertionError("Missing field in " + enumType.getName(), e);
    }
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    String name = reader.nextString();
    T constant = nameConstantMap.get(name);
    if (constant != null) return constant;
    return fallbackConstant;
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    writer.value(nameStrings[value.ordinal()]);
  }

  @Override public String toString() {
    return "JsonAdapter(" + enumType.getName() + ").fallbackEnum(" + fallbackConstant + ")";
  }
}
