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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Set;

import static com.serjltt.moshi.adapters.Util.nextAnnotations;

/**
 * Indicates that the annotated type/field is <strong>expected</strong> to be the element
 * at the given index of a json array.
 *
 * <p>For example if a json object is:
 * <pre>
 *   [
 *    {
 *      "some_field": "some_value",
 *      "other_field": "other_value"
 *    },
 *    {
 *      "some_field": "some_value_2",
 *      "other_field": "other_value_2"
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
 * <p>To leverage from {@link ElementAt} {@link ElementAt#ADAPTER_FACTORY}
 * must be added to your {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(ElementAt.ADAPTER_FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface ElementAt {
  /**
   * Represents the index location at which the element will be expected to be.
   * If the size of the array will be less then the provided index,
   * the companion adapter will return {@code null}.
   */
  int index();

  /** Builds an adapter that can process a types annotated with {@link ElementAt}. */
  JsonAdapter.Factory ADAPTER_FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Pair<ElementAt, Set<Annotation>> nextAnnotations =
          nextAnnotations(annotations, ElementAt.class);
      if (nextAnnotations == null || !nextAnnotations.second.isEmpty()) return null;

      return new ElementAtJsonAdapter<>(type, moshi, nextAnnotations.first.index());
    }
  };
}
