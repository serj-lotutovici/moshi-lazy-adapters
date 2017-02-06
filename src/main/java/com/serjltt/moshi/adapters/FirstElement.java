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
import java.util.Set;

/**
 * Indicates that the annotated type/field is <strong>expected</strong> to be the first element of a
 * json array.
 *
 * <p>For example if a json object is returned as:
 * <pre>
 *   [
 *    {
 *      "some_field": "some_value",
 *      "other_field": "other_value"
 *    }
 *   ]
 * </pre>
 * And the consumer only cares about the actual element, in the case of using a retrofit service
 * method the code would look like:
 *
 * <pre><code>
 *   {@literal @}GET("path/")
 *   {@literal @}FirstElement Call&lt;DataObject&gt; getData();
 * </code></pre>
 *
 * The resulting response returned by {@code response.body()} will be an instance of {@code
 * DataObject} with the respective values set.
 *
 * <p>To leverage from {@link FirstElement} {@linkplain FirstElement#ADAPTER_FACTORY}
 * must be added to a {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(FirstElement.ADAPTER_FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface FirstElement {
  /** Builds an adapter that can process a types annotated with {@link FirstElement}. */
  JsonAdapter.Factory ADAPTER_FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Set<? extends Annotation> nextAnnotations =
          Types.nextAnnotations(annotations, FirstElement.class);
      if (nextAnnotations == null || !nextAnnotations.isEmpty()) return null;

      return new ElementAtJsonAdapter<>(type, moshi, 0);
    }
  };
}
