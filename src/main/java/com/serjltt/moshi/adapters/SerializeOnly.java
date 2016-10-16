package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonQualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializeOnly {
}
