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
 * Indicates that the annotated type/field is <strong>expected</strong> to be the last element of a
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
 * And the consumer only cares about the last element, in the case of using a retrofit service
 * method the code would look like:
 *
 * <pre><code>
 *   {@literal @}GET("path/")
 *   {@literal @}LastElement Call&lt;DataObject&gt; getData();
 * </code></pre>
 *
 * The resulting response returned by {@code response.body()} will be an instance of {@code
 * DataObject} with the respective values set.
 *
 * <p>To leverage from {@link LastElement} {@linkplain LastElement#ADAPTER_FACTORY}
 * must be added to a {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(LastElement.ADAPTER_FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface LastElement {
  /** Builds an adapter that can process a types annotated with {@link LastElement}. */
  JsonAdapter.Factory ADAPTER_FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Set<? extends Annotation> nextAnnotations =
          Types.nextAnnotations(annotations, LastElement.class);
      if (nextAnnotations == null || !nextAnnotations.isEmpty()) return null;

      return new LastElementJsonAdapter<>(type, moshi);
    }
  };
}
