Change Log
===

2.1 *(11-10-2017)*
---
* Fix: Forces WrappedJsonAdapter to re-throw all exceptions. (#60)

2.0 *(06-06-2017)*
---
* New: Move all adapter factories to their respective annotations. (#47)
* New: Added `DefaultOnDataMismatchAdapter`. (#54)
* New: Added `@FilterNulls` annotation and its respective adapter. (#52)
* New: Added `@LastElement` annotation plus adapter (#50)
* New: Added `@Transient` annotation and it's respective adapter. (#49)
* New: Makes `JsonAdapter` for `@Wrapped` more strict. (#45)

* Enhancement: Rely on moshi's resialize nulls functionality. (#46)
* Enhancement: Use moshi's Types.nextAnnotations() where possible. (#44)

* New: Upgrade to Moshi 1.5.0.

   ```
   compile 'com.squareup.moshi:moshi:1.5.0'
   ```

1.3 *(04-01-2017)*
---
* New: Added `SerializeOnlyNonEmpty` for all collections and arrays.
* New: Allow `WrappedJsonAdapter` to fail on un-found value.
* New: Upgrade to Moshi 1.3.0.

   ```
   compile 'com.squareup.moshi:moshi:1.3.0'
   ```

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
