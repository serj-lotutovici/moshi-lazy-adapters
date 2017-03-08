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
import java.util.Set;

/**
 * Indicates that the annotated field may not contain any null values.
 * This annotation is applicable to all Collections.
 *
 * <p>To leverage from {@link FilterNulls} {@link FilterNulls#ADAPTER_FACTORY} must be
 * added to your {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(FilterNulls.ADAPTER_FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface FilterNulls {
  /** Builds an adapter that can process types annotated with {@link FilterNulls}. */
  JsonAdapter.Factory ADAPTER_FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        Moshi moshi) {
      Set<? extends Annotation> nextAnnotations =
          Types.nextAnnotations(annotations, FilterNulls.class);
      if (nextAnnotations == null || !nextAnnotations.isEmpty()) return null;

      Class<?> rawType = Types.getRawType(type);

      if (Collection.class.isAssignableFrom(rawType)) {
        return new FilterNullsJsonAdapter<>(moshi.adapter(type, nextAnnotations));
      }

      return null;
    }
  };
}
