package com.hubble.preftils

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T: Any> Preference<T>.asPreferenceKey() : Preferences.Key<T> {
    @Suppress("UNCHECKED_CAST")
    return when (default) {
        //Only Preferences type not supported is StringSet
        is String -> stringPreferencesKey(key) as Preferences.Key<T>
        is Int -> intPreferencesKey(key) as Preferences.Key<T>
        is Long -> longPreferencesKey(key) as Preferences.Key<T>
        is Boolean -> booleanPreferencesKey(key) as Preferences.Key<T>
        is Float -> floatPreferencesKey(key) as Preferences.Key<T>
        else -> {
            if (default::class.java.isAnnotationPresent(Serializable::class.java)) {
                stringPreferencesKey(key) as Preferences.Key<T>
            }

            throw IllegalArgumentException("Unsupported type")
        }
    }
}

inline /*operator*/ fun <reified T: Any> Preferences.get(preference: Preference<T>) : T {
    if (preference.default is String || preference.default is Int || preference.default is Long || preference.default is Boolean || preference.default is Float) {
        println(this[preference.asPreferenceKey()])
        return this[preference.asPreferenceKey()] ?: preference.default
    }

    if (preference.default::class.java.isAnnotationPresent(Serializable::class.java)) {
        val str = (this[preference.asPreferenceKey()] as String) ?: return preference.default
        return Json.decodeFromString<T>(str)
    }

    throw IllegalArgumentException("Unsupported get type: ${preference.default.javaClass.name}")
}

inline fun <reified T: Any> MutablePreferences.put(preference: Preference<T>, value: T) {
    if (preference.default is String || preference.default is Int || preference.default is Long || preference.default is Boolean || preference.default is Float) {
        this[preference.asPreferenceKey()] = value; return
    }

    @Suppress("UNCHECKED_CAST")
    if (preference.default::class.java.isAnnotationPresent(Serializable::class.java)) {
        val key = preference.asPreferenceKey() as Preferences.Key<String>
        this[key] = Json.encodeToString(value)
        return
    }

    throw IllegalArgumentException("Unsupported put type: ${preference.default::class.simpleName}")
}
