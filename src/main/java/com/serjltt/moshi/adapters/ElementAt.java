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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type/field is <strong>expected</strong> to be the element
 * at the given index of a json array and will be serialized/deserialized
 * by {@linkplain ElementAtJsonAdapter}.
 *
 * <p>For example if a json object is:
 * <pre>
 *   [
 *    {
 *      "some_field": "some_value",
 *      "other_field": "other_value"
 *    }
 *   ]
 * </pre>
 * And the consumer only cares about the second element, if using retrofit a service method would
 * look like:
 *
 * <pre><code>
 *   {@literal @}GET("path/")
 *   {@literal @}ElementAt(index = 1) Call&lt;DataObject&gt; getData();
 * </code></pre>
 *
 * The resulting response returned by {@code response.body()} will be an instance of {@code
 * DataObject} with the respective values set.
 *
 * <p>To leverage from {@linkplain ElementAt} the {@linkplain ElementAtJsonAdapter#FACTORY}
 * must be added to a {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(ElementAtJsonAdapter.FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ElementAt {
  int index();
}
