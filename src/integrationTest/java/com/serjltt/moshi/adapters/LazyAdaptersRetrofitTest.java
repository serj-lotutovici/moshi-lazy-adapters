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
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

import static org.assertj.core.api.Assertions.assertThat;

/** Test all lazy adapters to work in integration with retrofit. */
public final class LazyAdaptersRetrofitTest {
  @Rule public final MockWebServer server = new MockWebServer();

  private final Moshi moshi = new Moshi.Builder()
      .add(WrappedJsonAdapter.FACTORY)
      .add(FirstElementJsonAdapter.FACTORY)
      .build();

  private final Retrofit retrofit = new Retrofit.Builder()
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .baseUrl(server.url("/"))
      .build();

  private final Service service = retrofit.create(Service.class);

  @Test public void unwrapJsonAdapter() throws Exception {
    assertResponse(service.unwrap(), "{\n"
        + "  \"one\": {\n"
        + "    \"two\": \"works!\"\n"
        + "  }\n"
        + "}", "works!");
  }

  @Test public void firstElementJsonAdapter() throws Exception {
    assertResponse(service.firstElement(), "[\n"
        + "  \"expected\",\n"
        + "  \"ignored\"\n"
        + "]", "expected");
  }

  @Test public void unwrapFirstElement() throws Exception {
    assertResponse(service.unwrapFirstElement(), "{\n"
        + "  \"one\": {\n"
        + "    \"two\": [\n"
        + "      \"first\"\n"
        + "    ]\n"
        + "  }\n"
        + "}", "first");
  }

  private <T> void assertResponse(Call<T> call, String input, T expected) throws IOException {
    server.enqueue(new MockResponse().setBody(input));

    Response<T> response = call.execute();
    assertThat(response.body()).isEqualTo(expected);
  }

  /** Test service for all lazy adapters. */
  private interface Service {
    /** Helps to test the unwrap adapter. */
    @GET("/")
    @Wrapped({"one", "two"}) Call<String> unwrap();

    /** Helps to test the first element json adapter. */
    @GET("/")
    @FirstElement Call<String> firstElement();

    /** Helps to test the first element json adapter. */
    @GET("/")
    @Wrapped({"one", "two"})
    @FirstElement Call<String> unwrapFirstElement();
  }
}
