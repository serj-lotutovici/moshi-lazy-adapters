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

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/** Data class for testing auto-value-moshi integration. */
@AutoValue
abstract class Data {
  public static JsonAdapter<Data> jsonAdapter(Moshi moshi) {
    return new AutoValue_Data.MoshiJsonAdapter(moshi);
  }

  abstract String name();

  /**
   * The name of the method will be taken as the first key, then the path provided with the
   * annotation.
   */
  @Wrapped({"1"}) abstract Meta meta();

  static class Meta {
    String value1;
    int value2;
  }
}
