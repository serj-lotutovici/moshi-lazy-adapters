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

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.ToJson;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Helper annotation (to test if JsonQualifier annotations are propagated further down the stream).
 */
@JsonQualifier
@Retention(RetentionPolicy.RUNTIME) @interface Custom {
  /** String adapter, that will append "Custom" on read, and exclude it on write. */
  final class CustomAdapter {
    @Custom @FromJson String fromJson(String str) {
      return str + "Custom";
    }

    @ToJson String toJson(@Custom String str) {
      return str.substring(0, str.length() - "Custom".length());
    }
  }
}
