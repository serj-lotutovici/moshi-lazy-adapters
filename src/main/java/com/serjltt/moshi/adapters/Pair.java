package com.serjltt.moshi.adapters;

/** A simple pair data class. */
final class Pair<F, S> {
  final F first;
  final S second;

  Pair(F first, S second) {
    this.first = first;
    this.second = second;
  }
}
