package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public final class RequiredJsonAdapter extends JsonAdapter<Object> {
  public static final JsonAdapter.Factory FACTORY = new JsonAdapter.Factory() {
    @Override public JsonAdapter<?> create(Type type, Set<? extends Annotation> annotations,
        com.squareup.moshi.Moshi moshi) {
      List<Field> requiredFields = null;
      Field[] fields = Types.getRawType(type).getDeclaredFields(); // Does not check superclasses.
      for (Field field : fields) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(Required.class)) {
          if (field.getType().isPrimitive()) {
            throw new IllegalStateException("Primitives may not be annotated with @Required.");
          }
          if (requiredFields == null) requiredFields = new ArrayList<>();
          field.setAccessible(true);
          requiredFields.add(field);
        }
      }
      if (requiredFields == null) {
        return null;
      }
      JsonAdapter<Object> delegate = moshi.nextAdapter(this, type, annotations);
      return new RequiredJsonAdapter(delegate, requiredFields);
    }
  };

  @Retention(RUNTIME) public @interface Required {
  }

  private final JsonAdapter<Object> delegate;
  private final List<Field> requiredFields;

  RequiredJsonAdapter(JsonAdapter<Object> delegate, List<Field> requiredFields) {
    this.delegate = delegate;
    this.requiredFields = requiredFields;
  }

  @Override public Object fromJson(JsonReader reader) throws IOException {
    Object value = delegate.fromJson(reader);
    checkRequiredFields(value);
    return value;
  }

  @Override public void toJson(JsonWriter writer, Object value) throws IOException {
    checkRequiredFields(value);
    delegate.toJson(value);
  }

  private void checkRequiredFields(Object value) {
    for (Field field : requiredFields) {
      try {
        if (field.get(value) == null) {
          throw new IllegalStateException(
              String.format(Locale.US, "Required field %1$s in %2$s was null.", field.getName(),
                  field.getDeclaringClass()));
        }
      } catch (IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
  }
}
