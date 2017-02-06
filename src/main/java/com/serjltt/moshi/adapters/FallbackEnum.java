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
import com.squareup.moshi.Types;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Indicates that the annotated enum has a fallback value. The fallback must be set via
 * {@link #name()}. If no enum constant with the provided name is declared in the annotated
 * enum type an {@linkplain AssertionError assertion error} will be thrown.
 *
 * <p>To leverage from {@link FallbackEnum} {@link FallbackEnum#ADAPTER_FACTORY} must be added to
 * your {@linkplain Moshi moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(FallbackEnum.ADAPTER_FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FallbackEnum {
  String name();

  /** Builds an adapter that can process enums annotated with {@link FallbackEnum}. */
  JsonAdapter.Factory ADAPTER_FACTORY = new JsonAdapter.Factory() {
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
}
