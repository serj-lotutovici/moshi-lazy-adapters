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

import com.squareup.moshi.Moshi;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated enum has a fallback value. The fallback must be set via
 * {@link #name()}. If no enum constant with the provided name is declared in the annotated
 * enum type an {@linkplain AssertionError assertion error} will be thrown.
 *
 * <p>To leverage from {@linkplain FallbackEnum} the {@linkplain FallbackEnumJsonAdapter#FACTORY}
 * must be added to a {@linkplain Moshi moshi instance}:
 *
 * <pre><code>
 *   Moshi moshi = new Moshi.Builder()
 *      .add(FallbackEnumJsonAdapter.FACTORY)
 *      .build();
 * </code></pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FallbackEnum {
  String name();
}
