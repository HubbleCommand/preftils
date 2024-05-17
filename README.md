# Preftils - Kotlin Android Shared Preference Utils

This is a very simple library that reduces some verbosity when handling [SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences) in Android, as well as providing type safety.

While the newer [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) exists, it seems to have the same pitfall of preference keys not being safely typed to their underlying data type.

Due to the implementation, `Set<String>` is not supported due to type erasure of the `String` type of the `Collection`.

## Usage
Check the Instrumented test for sample code.

Ideally, you can use this library with a Kotlin object like so:
```
object Preferences {
    val DELAY = Preference("delay", 1000L)
}
```

Then access the preference with:
```
var delay: Long = SharedPreferences.get(Preferences.DELAY)
```
This access is type-safe, and trying

And update the preference with:
```
with (PreferenceManager.getDefaultSharedPreferences(this).edit()) {
    put(Preferences.DELAY, 100L)
    apply()
}
```

## Reduced verbosity
This is a minor reduction, but this saves the headache of having to get/put with the specific typed function, i.e. getString / putFloat. Read below for details.

## Type Safety for Android Preferences
An issue I have seen appear a few times in my own projects, and also professionally, is managing Shared Preferences.
Problems can come up when prototyping and sometimes changing the type of the preference, which can cause crashes that are not findable during compile-time, only during runtime.
Even in small code bases, it can be troublesome to manage preference types, and while it is very easy to fix, it's still wasted time of finding what the source of the crash was.
This can be a pain when either changing how some preferences are stored, or when working within a large app that has hundreds of preferences.
It can become quite troublesome to manage them, especially tracking which types you should be requesting or saving.

This library avoids this 

The one limitation is that it can still crash if you change the type of the preference without clearing it first.

Look into DataStore


