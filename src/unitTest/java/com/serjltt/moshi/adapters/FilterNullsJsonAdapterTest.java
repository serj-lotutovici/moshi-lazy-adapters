package com.serjltt.moshi.adapters;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;

public final class FilterNullsJsonAdapterTest {
  // Lazy adapters work only within the context of moshi.
  private final Moshi moshi = new Moshi.Builder()
      .add(FilterNulls.ADAPTER_FACTORY)
      .add(new Custom.CustomAdapter()) // We need to check that other annotations are not lost.
      .build();

  @Test public void noNullValues() throws Exception {
    JsonAdapter<List<String>> adapter = moshi.adapter(Types.newParameterizedType(List.class,
        String.class), FilterNulls.class);

    List<String> fromJson = adapter.fromJson("[\"apple\",\"banana\"]");
    assertThat(fromJson).containsExactly("apple", "banana");

    String toJson = adapter.toJson(fromJson);
    assertThat(toJson).isEqualTo("[\"apple\",\"banana\"]");
  }

  @Test public void nullValues() throws Exception {
    JsonAdapter<List<String>> adapter = moshi.adapter(Types.newParameterizedType(List.class,
        String.class), FilterNulls.class);

    List<String> fromJson = adapter.fromJson("[\"apple\",\"banana\",null]");
    assertThat(fromJson).containsExactly("apple", "banana");

    String toJson = adapter.toJson(new ArrayList<>(asList("apple", "banana", null)));
    assertThat(toJson).isEqualTo("[\"apple\",\"banana\"]");
  }

  @Test public void toStringReflectsInnerAdapter() throws Exception {
    JsonAdapter<String> adapter = moshi.adapter(Types.newParameterizedType(Collection.class,
        String.class), FilterNulls.class);

    assertThat(adapter.toString())
        .isEqualTo("JsonAdapter(String).nullSafe().collection().nullSafe().filterNulls()");
  }
}
