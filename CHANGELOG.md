Change Log
===

1.2 *(08-11-2016)*
---
* New: `@FallbackEnum` annotation and it's respective adapter.
* New: `@ElementAt` (similar to `@FirstElement` but more powerful) and it's respective adapter.
* Fix: `@SerializeNulls` adapter now maintains previous writer setting.

1.1 *(17-10-2016)*
---
*  New: Rename `@UnwrapJson` to `@Wrapped` annotation and it's respective adapter to `WrappedJsonAdapter`.
*  New: Allow manual creation of `@Wrapped` annotation via `Wrapped.Factory`.
*  New: `@SerializeOnly` & `@DeserializeOnly` annotations and their respective adapters.
*  New: Upgrade to Moshi 1.3.0.
   
   ```
   compile 'com.squareup.moshi:moshi:1.3.0'
   ```
*  Fix: Restrict `@Target` for each declared annotation.

1.0 *(14-09-2016)*
---
*  Initial release.
*  Introduce few handy adapters:
  * **SerializeNullsJsonAdapter** - Instructs moshi to serialize a value even if it's `null`;
  * **FirstElementJsonAdapter** - Instructs moshi to retrieve only the first element of a list;
  * **UnwrapJsonAdapter** - Unwraps a json object under the specified path;
  * **FallbackOnNullJsonAdapter** - Instructs moshi to fallback to a default value in case the json field is `null`.
