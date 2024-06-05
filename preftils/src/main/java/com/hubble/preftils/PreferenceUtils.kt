package com.hubble.preftils

import android.content.SharedPreferences

interface ICodable {
    fun encode(): String
    @Throws
    fun decode(string: String): ICodable
}

data class Preference<T>(val key: String, val default: T)

//TODO see about removing reified for Java interoperability
inline fun <reified T> SharedPreferences.get(preference: Preference<T>): T {
    return when (preference.default) {
        //Only Preferences type not supported is StringSet
        is String -> this.getString(preference.key, preference.default) as T
        is Int -> this.getInt(preference.key, preference.default) as T
        is Long -> this.getLong(preference.key, preference.default) as T
        is Boolean -> this.getBoolean(preference.key, preference.default) as T
        is Float -> this.getFloat(preference.key, preference.default) as T
        is ICodable -> {
            val str = this.getString(preference.key, null) ?: return preference.default
            preference.default.decode(str) as T
        }
        else -> throw IllegalArgumentException("Unsupported type")
    }
}

//fun <T : Any> SharedPreferences.Editor.put(preference: Preference<T>, value: T) {
inline fun <reified T> SharedPreferences.Editor.put(preference: Preference<T>, value: T) {
    when (value) {
        //Only Preferences type not supported is StringSet
        is String -> this.putString(preference.key, value)
        is Int -> this.putInt(preference.key, value)
        is Long -> this.putLong(preference.key, value)
        is Boolean -> this.putBoolean(preference.key, value)
        is Float -> this.putFloat(preference.key, value)
        is ICodable -> {
            this.putString(preference.key, value.encode())
        }
        else -> throw IllegalArgumentException("Unsupported type")
    }
}
