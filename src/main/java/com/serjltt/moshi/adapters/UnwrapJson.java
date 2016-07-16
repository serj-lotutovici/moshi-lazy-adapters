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
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated type/field should be unwrapped by the {@linkplain UnwrapJsonAdapter}
 * and can be found in the provided {@code path}.
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
 *   {@literal @}UnwrapJson({"response", "status"}) Call&lt;String&gt; getStatus();
 * </code></pre>
 *
 * The resulting response returned by {@code response.body()} will be a {@code String} with the
 * value {@code "OK"}.
 *
 * <p>To leverage from {@linkplain UnwrapJson} the {@linkplain UnwrapJsonAdapter#FACTORY} must be
 * added  to a {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(UnwrapJsonAdapter.FACTORY)
 *      .build();
 * </code></pre>
 *
 * <b>DISCLAIMER: </b> The order of {@linkplain JsonAdapter added json adapters} maters, to ensure
 * {@linkplain UnwrapJsonAdapter UnwrapJsonAdapter's} correct behaviour it must be the
 * <strong>first</strong> custom adapter added to the {@linkplain Moshi.Builder}.
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface UnwrapJson {
  /** The path to the wrapped json value. */
  String[] value();
}
