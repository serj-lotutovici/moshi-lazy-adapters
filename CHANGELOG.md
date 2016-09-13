Change Log
===

1.0 *(14-09-2016)*
---
* Initial release.
* Introduce few handy adapters:
 * **SerializeNullsJsonAdapter** - Instructs moshi to serialize a value even if it's `null`;
 * **FirstElementJsonAdapter** - Instructs moshi to retrieve only the first element of a list;
 * **UnwrapJsonAdapter** - Unwraps a json object under the specified path;
 * **FallbackOnNullJsonAdapter** - Instructs moshi to fallback to a default value in case the json field is `null`.
