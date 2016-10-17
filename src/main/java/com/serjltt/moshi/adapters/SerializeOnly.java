package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.Moshi;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the annotated field may only be serialized.
 *
 * <p>To leverage from {@linkplain SerializeOnly} the
 * {@linkplain SerializeOnlyJsonAdapter#FACTORY} must be added to a
 * {@linkplain Moshi Moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(SerializeOnlyJsonAdapter.FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeOnly {
}
