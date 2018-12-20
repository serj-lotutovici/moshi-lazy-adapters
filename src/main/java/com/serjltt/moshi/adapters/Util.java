/*
 * Copyright 2014 Square, Inc.
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
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

final class Util {
  /**
   * Checks if {@code annotations} contains {@code jsonQualifier}.
   * Returns a pair containing the subset of {@code annotations} without {@code jsonQualifier}
   * and the {@code jsonQualified} instance, or null if {@code annotations} does not contain
   * {@code jsonQualifier}.
   */
  public static <A extends Annotation> Pair<A, Set<Annotation>> nextAnnotations(
      Set<? extends Annotation> annotations, Class<A> jsonQualifier) {
    if (!jsonQualifier.isAnnotationPresent(JsonQualifier.class)) {
      throw new IllegalArgumentException(jsonQualifier + " is not a JsonQualifier.");
    }
    if (annotations.isEmpty()) {
      return null;
    }
    for (Annotation annotation : annotations) {
      if (jsonQualifier.equals(annotation.annotationType())) {
        Set<? extends Annotation> delegateAnnotations = new LinkedHashSet<>(annotations);
        delegateAnnotations.remove(annotation);
        //noinspection unchecked Protected by the if statment.
        return new Pair<>((A) annotation, Collections.unmodifiableSet(delegateAnnotations));
      }
      A delegate = findDelegatedAnnotation(annotation, jsonQualifier);
      if (delegate != null) {
        Set<? extends Annotation> delegateAnnotations = new LinkedHashSet<>(annotations);
        delegateAnnotations.remove(annotation);
        return new Pair<>(delegate, Collections.unmodifiableSet(delegateAnnotations));
      }
    }
    return null;
  }

  private static <A extends Annotation> A findDelegatedAnnotation(Annotation annotation, Class<A> jsonQualifier) {
    for (Annotation delegatedAnnotation : annotation.annotationType().getAnnotations()) {
      if (jsonQualifier.equals(delegatedAnnotation.annotationType())) {
        //noinspection unchecked
        return (A) delegatedAnnotation;
      }
    }
    return null;
  }

  private Util() {
    throw new AssertionError("No instances.");
  }
}
