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
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated type/field should be unwrapped by the {@linkplain
 * WrappedJsonAdapter} and can be found in the provided {@code path}.
 *
 * <p>For example if a json object is:
 * <pre>
 *   {
 *     "response": {
 *       "status": "OK"
 *     }
 *   }
 * </pre>
 * And the consumer only cares about the value of {@code status}, if using retrofit a service method
 * would look like:
 *
 * <pre><code>
 *   {@literal @}GET("path/")
 *   {@literal @}Wrapped({"response", "status"}) Call&lt;String&gt; getStatus();
 * </code></pre>
 *
 * The resulting response returned by {@code response.body()} will be a {@code String} with the
 * value {@code "OK"}.
 *
 * <p>To leverage from {@linkplain Wrapped} the {@linkplain WrappedJsonAdapter#FACTORY} must be
 * added  to a {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(WrappedJsonAdapter.FACTORY)
 *      .build();
 * </code></pre>
 *
 * <b>DISCLAIMER: </b> The order of {@linkplain JsonAdapter added json adapters} maters, to ensure
 * {@linkplain WrappedJsonAdapter WrappedJsonAdapter's} correct behaviour it must be the
 * <strong>first</strong> custom adapter added to the {@linkplain Moshi.Builder}.
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Wrapped {
  /** The path to the wrapped json value. */
  String[] value();

  /** Allows to easily create a new instance of {@link Wrapped} annotation. */
  final class Factory {
    /** Will create a new instance of {@link Wrapped} with the specified JSON path. */
    public static Wrapped create(final String... path) {
      return new Wrapped() {
        @Override public String[] value() {
          return path;
        }

        @Override public Class<? extends Annotation> annotationType() {
          return Wrapped.class;
        }
      };
    }

    private Factory() {
      throw new AssertionError("No instances.");
    }
  }
}
