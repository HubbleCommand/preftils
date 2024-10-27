
1. [Introduction](#preftils)
2. [Development](#development)
3. [Adding Dependency](#adding-dependency)
4. [Basic Usage](#basic-usage)
5. [Advanced usage with java.io `Serializable` interface](#advanced-usage-with-javaio-serializable)
6. [Advanced usage with kotlinx `@Serializable` annotation](#advanced-usage-with-kotlinx-serializable)

> The flutter version of this can be found [here](https://github.com/HubbleCommand/preftils_fl)

# Preftils - Android Shared Preference Utils

** WARNING **
You may see Gradle warnings about sync taking a long time or timing out. This is because if no one has requested the version of the package for an extended period, the build will be deleted, and JitPack will have to rebuild. You can see JitPack build status [here](https://jitpack.io/#hubblecommand/preftils), and retry once it is finished.

[![Beta Badge](https://kotl.in/badges/beta.svg)](https://kotlinlang.org/docs/components-stability.html#stability-of-subcomponents)

[pkg-url]: https://jitpack.io/#hubblecommand/preftils
[![Version Badge](https://jitpack.io/v/HubbleCommand/preftils.svg)][pkg-url]
[![Downloads Badge](https://jitpack.io/v/HubbleCommand/preftils/month.svg)][pkg-url]
[![License Badge](https://img.shields.io/github/license/HubbleCommand/preftils.svg?color=blue)](https://github.com/hubblecommand/preftils/blob/master/LICENSE)

This is a very simple library that provides type safety when working with Android's [SharedPreferences](https://developer.android.com/training/data-storage/shared-preferences).

You can read more about the development of this package [on my blog page dedicated to it](https://hubblecommand.github.io/projects/preftils.html).

When managing large amounts of SharedPreferences, it can be time-consuming to track the correct types when accessing preference values among hundreds of preferences, without otherwise making utility functions for each preference. This is incredibly verbose, but avoids otherwise critical runtime crashes. This extremely light library avoids these issues by providing a type-safe way of interacting with SharedPreferences.

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

You can achieve similar results in Java. Make sure to use the appropriate Java classes.

## Advanced usage with java.io [`Serializable`](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html)
[`Serializable`](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html) is an interface that is part of the [`java.io`](https://docs.oracle.com/javase/8/docs/api/java/io/package-summary.html) package.

Serializability of a class is enabled by the class implementing the `java.io.Serializable` interface. So, as long as your classes implement the interface (with no required methods to implement), then you are good to go.

You can read more [here](https://www.oracle.com/technical-resources/articles/java/serializationapi.html), including how to write your own serializers.

## Advanced usage with kotlinx [`@Serializable`](https://kotlinlang.org/docs/serialization.html)
[`Serialization`](https://kotlinlang.org/docs/serialization.html) is a set of Kotlin libraries to simplify [serialization](https://en.wikipedia.org/wiki/Serialization).

Just make sure that your custom classes have the `@Serializable` annotation, and you'll be good. If you want more control, you can also write [custom serializers](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md#custom-serializers) for your classes. You can also serialize [all sorts of primitive types](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/builtin-classes.md#primitives).

Currently serializes into [JSON](https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/README.md#json) and is planned to make it more format-agnostic once other formats leave the [`experimental` status](https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/README.md).

Be warned of [this](https://github.com/Kotlin/kotlinx.serialization?tab=readme-ov-file#android) in your proguard rules.
