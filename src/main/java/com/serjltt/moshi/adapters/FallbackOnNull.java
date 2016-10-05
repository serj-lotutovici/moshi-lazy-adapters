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

import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.Moshi;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated field may be {@code null} in the json source and thus requires a
 * fallback value.
 *
 * <p>To leverage from {@linkplain FallbackOnNull} the {@linkplain FallbackOnNullJsonAdapter#FACTORY
 * } must be added to a {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(FallbackOnNullJsonAdapter.FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
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
}
