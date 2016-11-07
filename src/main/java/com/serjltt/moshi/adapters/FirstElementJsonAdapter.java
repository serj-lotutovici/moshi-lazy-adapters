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
import com.squareup.moshi.Moshi;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import static com.serjltt.moshi.adapters.Util.hasAnnotation;

/**
 * {@linkplain JsonAdapter} that extracts the first element of an array of (a field) type annotated
 * with {@linkplain FirstElement}.
 */
public final class FirstElementJsonAdapter {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      if (hasAnnotation(annotations, FirstElement.class) && annotations.size() == 1) {
        return new ElementAtJsonAdapter<>(type, moshi, 0);
      }
      return null;
    }
  };

  private FirstElementJsonAdapter() {
    throw new AssertionError("No instances.");
  }
}
