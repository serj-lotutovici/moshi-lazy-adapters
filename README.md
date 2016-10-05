Moshi Lazy Adapters
===


[![Build Status][travis.svg]][travis]
[![codecov][codecov.svg]][codecov]
[![Latest Build][latestbuild.svg]][latestbuild]

A collection of simple JsonAdapters for [Moshi][moshi].
 
This library acts as an extension to Moshi by providing general purpose, yet useful JsonAdapters that 
are not present in the main library.

How To Use It
---

Moshi-Lazy-Adapters is **not** forcing any of it's own adapters by default. To leverage from any of
the provided annotations/adapters add their respective factory to your `Moshi.Builder`:

```java
Moshi moshi = new Moshi.Builder()
  .add(UnwrapJsonAdapter.FACTORY);
  .build();
```

The Lazy Adapters
---

###UnwrapJsonAdapter


Some apis enjoy wrapping the response object inside other json objects. This creates a lot of inconvenience
 when it comes to consuming the request. The following example contains a list of a users
 favorite pokemon which is wrapped behind two keys:
 
```json
{
  "favorite_pokemon": {
    "pokemons": [
      "Snorlax",
      "Pikachu",
      "Bulbasaur",
      "Charmander",
      "Squirtle"
    ]
  }
}
```

In the end the consumer is just interested in the names of the pokemon, but the json object forces to create
 a wrapping object, which will contain the list:
 
```java
class FavoritePokemonResponse {
  FavoritePokemon favorite_pokemon;
}

class FavoritePokemon {
  List<String> pokemons;
}
```

A custom adapter would be another option, and `UnwrapJsonAdapter` is just the one. By annotating
the response type with `@UnwrapJson` and providing the path to the desired list, the need for 
an additional object is dropped:

```java
// This assumes that Retrofit is used to obtain the response.
interface PokemonService {
  @GET("/pokemon/favorite")
  @UnwrapJson({"favorite_pokemon", "pokemons"}) Call<List<String>> getFavorite();
}
```

No need for a new class, which results in less code and less methods generated by the consumer code. 
You can also annotate any field in your response entity and the same rules will apply.


###FallbackOnNullJsonAdapter


Primitives are simple and safe. Primitives have also a smaller memory footprint. Some apis may return
`null` for values that are normally processed as primitives. A safe alternative would be to use
their boxed counterparts, but that would result in redundant boxing and unboxing. By annotating 
any primitive field with `@FallbackOnNull` the consumer can specify a default value for the field, in
case it's json representation is null.

```json
[
  {
    "name": "Pikachu",
    "number_of_wins": 1
  },
  {
    "name": "Magikarp",
    "number_of_wins": null
  } 
]
```

The json above contains a list of pokemon of a user with their names and the number of wins the 
respective pokemon has obtained during it's trainings of fights. Notice that the 'Magikarp' pokemon
has `null` wins. Normally the representing POJO would declare the filed as `Integer`, but #perfmatters.
With `@FallbackOnNull` the `Pokemon` object can be declared as:

```java
class Pokemon {
  String name;
  @FallbackOnNull int number_of_wins;
}
```

If the incoming value would be `null` the `number_of_wins` field would default to `Integer.MIN_VALUE`,
this can be altered by providing an alternative fallback:

```java
  @FallbackOnNull(fallbackInt = -1) int number_of_wins;
```

See [FallbackOnNull's documentation](../master/src/main/java/com/serjltt/moshi/adapters/FallbackOnNull.java) 
for a full reference.


Full List of Adapters
---

Moshi-Lazy-Adapters contains the following json adapters:
* **SerializeNullsJsonAdapter** - Instructs moshi to serialize a value even if it's `null`;
* **FirstElementJsonAdapter** - Instructs moshi to retrieve only the first element of a list;
* **UnwrapJsonAdapter** - Unwraps a json object under the specified path;
* **FallbackOnNullJsonAdapter** - Instructs moshi to fallback to a default value in case the json field is `null`.

Download
---

Download [the latest JAR][dl] or depend via Maven:
```xml
<dependency>
  <groupId>com.serjltt.moshi</groupId>
  <artifactId>moshi-lazy-adapters</artifactId>
  <version>x.y</version>
</dependency>
```
or Gradle:
```groovy
compile 'com.serjltt.moshi:moshi-lazy-adapters:x.y'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][sonatype].

License
===

    Copyright 2016 Serj Lotutovici

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


 [moshi]: https://github.com/square/moshi
 [travis]: https://travis-ci.org/serj-lotutovici/moshi-lazy-adapters
 [travis.svg]: https://travis-ci.org/serj-lotutovici/moshi-lazy-adapters.svg?branch=master
 [codecov]: https://codecov.io/gh/serj-lotutovici/moshi-lazy-adapters
 [codecov.svg]: https://codecov.io/gh/serj-lotutovici/moshi-lazy-adapters/branch/master/graph/badge.svg
 [latestbuild]: http://search.maven.org/#search%7Cga%7C1%7Ccom.serjltt.moshi
 [latestbuild.svg]: https://img.shields.io/maven-central/v/com.serjltt.moshi/moshi-lazy-adapters.svg
 [sonatype]: https://oss.sonatype.org/content/repositories/snapshots/com/serjltt/moshi/
 [dl]: https://search.maven.org/remote_content?g=com.serjltt.moshi&a=moshi-lazy-adapters&v=LATEST
