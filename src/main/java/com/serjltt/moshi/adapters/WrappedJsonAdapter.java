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
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import java.io.IOException;
import java.util.Arrays;

/** {@linkplain JsonAdapter} that unwraps the type/field annotated with {@linkplain Wrapped}. */
final class WrappedJsonAdapter<T> extends JsonAdapter<T> {
  private final JsonAdapter<T> delegate;
  private final String[] path;
  private final boolean failOnNotFound;

  WrappedJsonAdapter(JsonAdapter<T> delegate, String[] path, boolean failOnNotFound) {
    this.delegate = delegate;
    this.path = path;
    this.failOnNotFound = failOnNotFound;
  }

  @Override public T fromJson(JsonReader reader) throws IOException {
    return fromJson(delegate, reader, path, 0, failOnNotFound);
  }

  @Override public void toJson(JsonWriter writer, T value) throws IOException {
    toJson(delegate, writer, value, path, 0);
  }

  @Override public String toString() {
    return delegate + String.format(".wrapped(%s)", Arrays.asList(path))
        + (failOnNotFound ? ".failOnNotFound()" : "");
  }

  /**
   * Recursively goes through the json and finds the given root. Returns the object wrapped by the
   * provided {@code path}.
   */
  private static <T> T fromJson(JsonAdapter<T> adapter, JsonReader reader, String[] path,
      int index, boolean failOnNotFound) throws IOException {
    if (index == path.length) {
      //noinspection unchecked This puts full responsibility on the caller.
      return adapter.fromJson(reader);
    } else {
      reader.beginObject();
      Exception caughtException = null;
      try {
        String root = path[index];
        while (reader.hasNext()) {
          if (reader.nextName().equals(root)) {
            if (reader.peek() == JsonReader.Token.NULL) {
              // Consumer expects a value, not a null.
              if (failOnNotFound) {
                throw new JsonDataException(String.format(
                    "Wrapped Json expected at path: %s. Found null at %s",
                    Arrays.asList(path), reader.getPath()
                ));
              }

              return reader.nextNull();
            }
            return fromJson(adapter, reader, path, ++index, failOnNotFound);
          } else {
            reader.skipValue();
          }
        }
      } catch (Exception e) {
        caughtException = e;
      } finally {
        // If the try block throw an exception, rethrow it up the stack.
        if (caughtException instanceof IOException) {
          //noinspection ThrowFromFinallyBlock
          throw (IOException) caughtException;
        } else if (caughtException instanceof JsonDataException) {
          //noinspection ThrowFromFinallyBlock
          throw (JsonDataException) caughtException;
        } else if (caughtException != null) {
          //noinspection ThrowFromFinallyBlock
          throw new AssertionError(caughtException);
        }
        // If the json has an additional key, that was not red, we ignore it.
        while (reader.hasNext()) {
          reader.skipValue();
        }
        // End object, so that other adapters (if any) can proceed.
        reader.endObject();
      }
      throw new JsonDataException(String.format(
          "Wrapped Json expected at path: %s. Actual: %s",
          Arrays.asList(path), reader.getPath()));
    }
  }

  /**
   * Recursively writes the respective roots forming a json object that resembles the {@code path}
   * wrapping the type of the {@code adapter}.
   */
  private static <T> void toJson(JsonAdapter<T> adapter, JsonWriter writer, T value,
      String[] path, int index) throws IOException {
    if (value != null || writer.getSerializeNulls()) {
      if (index == path.length) {
        adapter.toJson(writer, value);
      } else {
        writer.beginObject();
        writer.name(path[index]);
        toJson(adapter, writer, value, path, ++index);
        writer.endObject();
      }
    } else {
      // If we don't propagate the null value the writer will throw.
      writer.nullValue();
    }
  }
}
