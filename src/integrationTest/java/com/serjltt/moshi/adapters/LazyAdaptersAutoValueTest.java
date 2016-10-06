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
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.Collections;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Test all lazy adapters to work in integration with AutoValue extensions. */
public class LazyAdaptersAutoValueTest {
  private final Moshi moshi = new Moshi.Builder().add(UnwrapJsonAdapter.FACTORY).add(DataFactories.create()).build();

  @Test public void unwrap() throws Exception {
    JsonAdapter<Data> adapter = moshi.adapter(Data.class);

    Data fromJson = adapter.fromJson("{\n"
        + "  \"name\": \"data_name\",\n"
        + "  \"meta\": {\n"
        + "    \"1\": {\n"
        + "      \"value1\": \"value1\",\n"
        + "      \"value2\": 2\n"
        + "    }\n"
        + "  }\n"
        + "}");

    assertThat(fromJson.name()).isEqualTo("data_name");
    assertThat(fromJson.meta().value1).isEqualTo("value1");
    assertThat(fromJson.meta().value2).isEqualTo(2);
  }

  @Test public void unwrapSecond() throws IOException {
    final ServerSideLocationRequestMessage response = (ServerSideLocationRequestMessage) moshi.adapter(ServerSideLocationRequestMessage.class,
        Collections.singleton(UnwrapJsonFactory.create("foo")))
        .fromJson("{\"foo\":{\"bar\":2,\"circle_id\":\"564542654\",\"circle\":\"5530fa4915c1cf21e3043009\"}}");
    assertThat(response.bar()).isEqualTo(2);
    assertThat(response.circle()).isEqualTo("5530fa4915c1cf21e3043009");
    assertThat(response.circleId()).isEqualTo("564542654");
  }
}
