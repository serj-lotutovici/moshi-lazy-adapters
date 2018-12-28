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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.Locale;

import static com.serjltt.moshi.adapters.Util.nextAnnotations;

/**
 * Indicates that the annotated field may be {@code null} in the json source and thus requires a
 * fallback value.
 *
 * <p>To leverage from {@linkplain FallbackOnNull} {@linkplain FallbackOnNull#ADAPTER_FACTORY}
 * must be added to your {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(FallbackOnNull.ADAPTER_FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface FallbackOnNull {
  /** Fallback value for {@code boolean} primitives. Default: {@code false}. */
  boolean fallbackBoolean() default false;

  /** Fallback value for {@code byte} primitives. Default: {@code Byte.MIN_VALUE}. */
  byte fallbackByte() default Byte.MIN_VALUE;

  /** Fallback value for {@code char} primitives. Default: {@code Character.MIN_VALUE}. */
  char fallbackChar() default Character.MIN_VALUE;

  /** Fallback value for {@code double} primitives. Default: {@code Double.MIN_VALUE}. */
  double fallbackDouble() default Double.MIN_VALUE;

  /** Fallback value for {@code float} primitives. Default: {@code Float.MIN_VALUE}. */
  float fallbackFloat() default Float.MIN_VALUE;

  /** Fallback value for {@code int} primitives. Default: {@code Integer.MIN_VALUE}. */
  int fallbackInt() default Integer.MIN_VALUE;

  /** Fallback value for {@code long} primitives. Default: {@code Long.MIN_VALUE}. */
  long fallbackLong() default Long.MIN_VALUE;

  /** Fallback value for {@code short} primitives. Default: {@code Short.MIN_VALUE}. */
  short fallbackShort() default Short.MIN_VALUE;

  /** Builds an adapter that can process a types annotated with {@link FallbackOnNull}. */
  JsonAdapter.Factory ADAPTER_FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Pair<FallbackOnNull, Set<Annotation>> nextAnnotations =
          nextAnnotations(annotations, FallbackOnNull.class);
      if (nextAnnotations == null) return null;

      Class<?> rawType = Types.getRawType(type);
      if (!FallbackOnNullJsonAdapter.PRIMITIVE_CLASSES.contains(rawType)) return null;

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
      String methodSuffix = typeName.substring(0, 1).toUpperCase(Locale.US) + typeName.substring(1);
      return "fallback" + methodSuffix;
    }
  };
}
