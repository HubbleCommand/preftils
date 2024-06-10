
1. [Introduction](#preftils)
2. [Development](#development)
3. [Adding Dependency](#adding-dependency)
4. [Basic Usage](#basic-usage)
5. [Advanced Usage with ICodable](#advanced-usage-with-icodable)

# Preftils - Kotlin Android Shared Preference Utils

[pkg-url]: https://jitpack.io/#hubblecommand/preftils
[![Version Badge](https://jitpack.io/v/HubbleCommand/preftils.svg)][pkg-url]
[![Downloads Badge](https://jitpack.io/v/HubbleCommand/preftils/month.svg)][pkg-url]
[![License Badge](https://img.shields.io/github/license/HubbleCommand/preftils.svg?color=blue)](https://github.com/hubblecommand/preftils/blob/master/LICENSE)

This is a very simple library that provides type safety when working with Android's [SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences).

When managing large amounts of SharedPreferences, it can be time-consuming to track the correct types when accessing preference values among hundreds of preferences, without otherwise making utility functions for each preference. This is incredibly verbose, but avoids otherwise critical runtime crashes. This extremely light library avoids these issues by providing a type-safe way of interacting with SharedPreferences.

While the newer [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) exists, it seems to have the same pitfall of preference values not being safely typed to their underlying data type. Additionally, SharedPreferences remains a very popular and approachable way of managing long-duration state in Android.

Due to the implementation, `Set<String>` is not supported due to type erasure of the `String` type of the `Collection`. All other SharedPreference types are supported.

## Development
When doing local development, make sure to set the version to `local` with `implementation("com.github.HubbleCommand:preftils:local")` as it is set in the module-level `build.gradle.kts`.

## Adding dependency
Add jitpack to your repositories in your root build.gradle:
```
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

Then add the dependency to your module / app build.gradle:
```
dependencies {
    implementation 'com.github.HubbleCommand:preftils:version'
}
```

## Basic Usage
Check the Instrumented tests or the sample app under `app` for sample code. Ideally, you can use this library with a Kotlin object like so:
```
object Preferences {
    val PREF = Preference("pref", 1000L)
}
```

Then access the preference with:
```
var pref: Long = SharedPreferences.get(Preferences.PREF)
```
This access is type-safe, and trying to cast, say, the Long PREF to a String will fail at compile-time.

The preference can be updated with the following, which will also fail at compile-time if the types do not match:
```
with (PreferenceManager.getDefaultSharedPreferences(this).edit()) {
    put(Preferences.PREF, 100L)
    apply()
}
```

## Advanced usage with `ICodable`
Custom types are also supported, as long as they implement the `ICodable` interface. Once you have implemented a way to encode & decode your custom class, you can easily use it with SharedPreferences like with the other primitive types. An example is provided in the Instrumented tests as before.
