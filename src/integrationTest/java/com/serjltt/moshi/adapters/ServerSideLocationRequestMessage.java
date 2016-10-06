package com.serjltt.moshi.adapters;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue public abstract class ServerSideLocationRequestMessage {
  abstract int bar();
  abstract String circle();
  @Json(name = "circle_id") abstract String circleId();

  public static JsonAdapter<ServerSideLocationRequestMessage> jsonAdapter(final Moshi moshi) {
    return new AutoValue_ServerSideLocationRequestMessage.MoshiJsonAdapter(moshi);
  }
}
