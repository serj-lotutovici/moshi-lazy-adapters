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
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Indicates that the annotated type/field should not be serialized in case of an
 * empty value.
 *
 * <p>To leverage from {@link SerializeOnlyNonEmpty} {@link SerializeOnlyNonEmpty#ADAPTER_FACTORY}
 * must be added to your {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(SerializeOnlyNonEmpty.ADAPTER_FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SerializeOnlyNonEmpty {
  /** Builds an adapter that can process a types annotated with {@link SerializeOnlyNonEmpty}. */
  JsonAdapter.Factory ADAPTER_FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Set<? extends Annotation> nextAnnotations =
          Types.nextAnnotations(annotations, SerializeOnlyNonEmpty.class);
      if (nextAnnotations == null) return null;

      Class<?> rawType = Types.getRawType(type);

      if (rawType.isArray() || Collection.class.isAssignableFrom(rawType)
          || Map.class.isAssignableFrom(rawType)) {
        return new SerializeOnlyNonEmptyJsonAdapter<>(moshi.adapter(type, nextAnnotations));
      }

      return null;
    }
  };
}
