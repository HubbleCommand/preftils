package com.hubble.preftils

import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Wrapper class for handling SharedPreferences. Make sure your custom Kotlin classes have the @Serializable annotation, otherwise you will get runtime errors:
 *
 *     kotlinx.serialization.SerializationException: Serializer for class 'X' is not found.
 *     Please ensure that class is marked as '@Serializable' and that the serialization compiler plugin is applied.
 *
 * @property[key] the SharedPreference Key
 * @property[default] the default value of the preference, also provides concrete type-checkable type
 */
@Serializable
data class Preference<T: Any>(val key: String, val default: T)

/**
 * Retrieves the value of a preference in a type-safe way
 *
 * Propagates errors thrown by Json.decodeFromString() if decoding is impossible
 *
 * @param preference the Preference to retrieve
 *
 * @throws SerializationException in case of any decoding-specific error
 * @throws IllegalArgumentException if the decoded input is not a valid instance of [T]
 */
inline fun <reified T: Any> SharedPreferences.get(preference: Preference<T>, default: T = preference.default): T {
    return when (preference.default) {
        //Only Preferences type not supported is StringSet
        is String -> this.getString(preference.key, default as String) as T
        is Int -> this.getInt(preference.key, default as Int) as T
        is Long -> this.getLong(preference.key, default as Long) as T
        is Boolean -> this.getBoolean(preference.key, default as Boolean) as T
        is Float -> this.getFloat(preference.key, default as Float) as T
        else -> {
            if (preference.default::class.java.isAnnotationPresent(Serializable::class.java)) {
                val str = this.getString(preference.key, null) ?: return default
                return Json.decodeFromString<T>(str)
            }

            throw IllegalArgumentException("Unsupported type: ${preference.default::class.qualifiedName}")
        }
    }
}

/**
 * Sets the value of a preference in a type-safe way
 *
 * Propagates errors thrown by Json.encodeToString() if encoding of [value] is impossible
 *
 * @param[preference] The preference to save
 * @param[value] The value to set the preference to
 *
 * @throws SerializationException in case of any encoding-specific error
 * @throws IllegalArgumentException if the encoded input is not a valid instance of [T]
 */
inline fun <reified T: Any> SharedPreferences.Editor.put(preference: Preference<T>, value: T) {
    when (value) {
        //Only Preferences type not supported is StringSet
        is String -> this.putString(preference.key, value)
        is Int -> this.putInt(preference.key, value)
        is Long -> this.putLong(preference.key, value)
        is Boolean -> this.putBoolean(preference.key, value)
        is Float -> this.putFloat(preference.key, value)
        else -> {
            if (preference.default::class.java.isAnnotationPresent(Serializable::class.java)) {
                this.putString(preference.key, Json.encodeToString(value))
                return
            }
            throw IllegalArgumentException("Unsupported type: ${preference.default::class.qualifiedName}")
        }
    }
}
