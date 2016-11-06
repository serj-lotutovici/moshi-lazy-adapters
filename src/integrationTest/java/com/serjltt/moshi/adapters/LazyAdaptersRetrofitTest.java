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

import com.squareup.moshi.Json;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

  @Test public void unwrapNestedJsonAdapter() throws Exception {
    server.enqueue(new MockResponse().setBody("{\n"
        + " \"one\": {\n"
        + "  \"two\": {\n"
        + "   \"item\": {\n"
        + "    \"foo\": \"this\"\n"
        + "   },\n"
        + "   \"item2\": {\n"
        + "    \"bar\": 1234\n"
        + "   },\n"
        + "   \"foobar\": 567\n"
        + "  }\n"
        + " }\n"
        + "}"));

    Response<Nested> response = service.unwrapNested().execute();

    assertThat(response.body().foobar).isEqualTo(567);
    assertThat(response.body().foo).isEqualTo("this");
    assertThat(response.body().bar).isEqualTo(1234);
  }

  @Test public void wrapPostBody() throws Exception {
    server.enqueue(new MockResponse());

    Call<ResponseBody> call = service.wrappedPost("one");
    call.execute();

    RecordedRequest recorded = server.takeRequest();
    assertThat(recorded.getBody()
        .readUtf8())
        .isEqualTo("{\"1\":{\"2\":\"one\"}}");
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

    @GET("/")
    @Wrapped({"one", "two"}) Call<Nested> unwrapNested();

    @POST("/") Call<ResponseBody> wrappedPost(@Body @Wrapped({"1", "2"}) String value);

    /** Helps to test the first element json adapter. */
    @GET("/")
    @FirstElement Call<String> firstElement();

    /** Helps to test the first element json adapter. */
    @GET("/")
    @Wrapped({"one", "two"})
    @FirstElement Call<String> unwrapFirstElement();
  }

  static class Nested {
    @Wrapped("foo") @Json(name = "item") String foo;
    @Wrapped("bar") @Json(name = "item2") int bar;
    int foobar;
  }
}
