package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.Moshi;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field may only be deserialized.
 *
 * <p>To leverage from {@linkplain DeserializeOnly} the
 * {@linkplain DeserializeOnlyJsonAdapter#FACTORY} must be added to a
 * {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(DeserializeOnlyJsonAdapter.FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface DeserializeOnly {
}
