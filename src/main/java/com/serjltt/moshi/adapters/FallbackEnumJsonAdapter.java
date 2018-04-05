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
import java.io.IOException;

/**
 * {@linkplain JsonAdapter} that fallbacks to a default enum constant declared in the enum type
 * annotated with {@linkplain FallbackEnum}.
 */
final class FallbackEnumJsonAdapter<T extends Enum<T>> extends JsonAdapter<T> {
  private final Class<T> enumType;
  private final String[] nameStrings;
  private final T[] constants;
  private final JsonReader.Options options;
  private final T fallbackConstant;

  FallbackEnumJsonAdapter(Class<T> enumType, String fallback) {
    fallbackConstant = Enum.valueOf(enumType, fallback);
    this.enumType = enumType;
    try {
      constants = enumType.getEnumConstants();
      nameStrings = new String[constants.length];
      for (int i = 0; i < constants.length; i++) {
        T constant = constants[i];
        Json annotation = enumType.getField(constant.name()).getAnnotation(Json.class);
        String name = annotation != null ? annotation.name() : constant.name();
        nameStrings[i] = name;
      }
      options = JsonReader.Options.of(nameStrings);
    } catch (NoSuchFieldException e) {
      throw new AssertionError(e);
    }
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    int index = reader.selectString(options);
    if (index != -1) return constants[index];
    reader.nextString();
    return fallbackConstant;
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    writer.value(nameStrings[value.ordinal()]);
  }

  @Override public String toString() {
    return "JsonAdapter(" + enumType.getName() + ").fallbackEnum(" + fallbackConstant + ")";
  }
}
