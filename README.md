# Preftils - Kotlin Android Shared Preference Utils

[![](https://jitpack.io/v/HubbleCommand/preftils.svg)](https://jitpack.io/#HubbleCommand/preftils)

This is a very simple library that reduces some verbosity and provides type safety when working with Android's [SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences).

While the newer [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) exists, it seems to have the same pitfall of preference values not being safely typed to their underlying data type. Additionally, SharedPreferences remains a very popular way of managing long-duration state in Android.

Due to the implementation, `Set<String>` is not supported due to type erasure of the `String` type of the `Collection`. All other SharedPreference types are supported.

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

## Usage
Check the Instrumented test for sample code. Ideally, you can use this library with a Kotlin object like so:
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

## Reduced verbosity
This is a minor reduction, but this saves the headache of having to get/put with the specific typed function, i.e. getString / putFloat. Read below for details.

## Type Safety for Android Preferences
An issue I have seen a few times is managing SharedPreferences.

Even in small code bases, it can be troublesome to manage preference types, and while the runtime crashes are very easy to fix, it's still wasted debug time. This issue is compounded when working with large apps that have hundreds of preferences. It can become quite troublesome to manage them, especially tracking which types you should be requesting or saving, having to navigate your code to make sure you have requested the right type.

This extremely light library avoids these issues by providing a type-safe way to request and store these preferences.
