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

  public final Moshi moshi = new Moshi.Builder()
      .add(UnwrapJsonAdapter.FACTORY)
      .build();

  public final Retrofit retrofit = new Retrofit.Builder()
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .baseUrl(server.url("/"))
      .build();

  @Test public void unwrapJsonAdapter() throws Exception {
    server.enqueue(new MockResponse().setBody("{\n"
        + "  \"one\": {\n"
        + "    \"two\": \"works!\"\n"
        + "  }\n"
        + "}"));

    Service service = retrofit.create(Service.class);
    Response<String> response = service.unwrap().execute();

    assertThat(response.body()).isEqualTo("works!");
  }

  /** Test service for all lazy adapters. */
  interface Service {
    /** Helps to test the unwrap adapter. */
    @GET("/")
    @UnwrapJson({"one", "two"}) Call<String> unwrap();
  }
}
