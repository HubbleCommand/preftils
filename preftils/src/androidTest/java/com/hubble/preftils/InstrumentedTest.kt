package com.hubble.preftils

import android.content.Context
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.Serializable

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.BeforeClass

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 *
 * Test results are in build/reports/androidTests...
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    object PreferencesTest {
        val NUMBER = Preference("number", 1)
    }

    companion object {
        lateinit var context: Context
        lateinit var preferences: SharedPreferences

        private const val PREFERENCES_NAME = "my_preferences"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

        @BeforeClass
        @JvmStatic
        internal fun initialize() {
            context = InstrumentationRegistry.getInstrumentation().targetContext
            preferences = context.applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)

            with (preferences.edit()) {
                remove(PreferencesTest.NUMBER.key)
                remove(PreferencesSerializable.SERIALIZABLE.key)
                apply()
            }

            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences.remove(PreferencesTest.NUMBER.asPreferenceKey())
                }
            }
        }
    }

    @Test
    fun testBasic() {
        val initial = preferences.get(PreferencesTest.NUMBER)
        assertEquals(PreferencesTest.NUMBER.default, initial)

        val target = 2
        with (preferences.edit()) {
            put(PreferencesTest.NUMBER, target)
            apply()
        }

        val set = preferences.get(PreferencesTest.NUMBER)
        assertEquals(target, set)
    }

    @Test
    fun testIncompatibleType() {
        val invalidPreferenceType = Preference("invalid", 20u)
        try {
            preferences.get(invalidPreferenceType)
            fail("Should have thrown")
        } catch (_: IllegalArgumentException) {

        }
    }

    @Serializable
    class SerializableType(val a: Int, val b: String)

    object PreferencesSerializable {
        val SERIALIZABLE = Preference("serializable", SerializableType(19, "a"))
    }

    @Test
    fun testSerializablePreference() {
        //Test read
        val pref : SerializableType = preferences.get(PreferencesSerializable.SERIALIZABLE)
        assert(pref.a == PreferencesSerializable.SERIALIZABLE.default.a)
        assert(pref.b == PreferencesSerializable.SERIALIZABLE.default.b)

        //Test write
        val newInt = 32
        val newString = "another string"
        with (preferences.edit()) {
            put(PreferencesSerializable.SERIALIZABLE, SerializableType(newInt, newString))
            apply()
        }

        //Verify write
        val prefNew : SerializableType = preferences.get(PreferencesSerializable.SERIALIZABLE)
        assert(prefNew.a == newInt)
        assert(prefNew.b == newString)
    }

    @Test
    fun testDataStore() = runTest {
        assertEquals(PreferencesTest.NUMBER.default, context.dataStore.data.first().get(PreferencesTest.NUMBER))
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences.put(PreferencesTest.NUMBER, 3)
        }
        assertEquals(3, context.dataStore.data.first().get(PreferencesTest.NUMBER))
    }
}
