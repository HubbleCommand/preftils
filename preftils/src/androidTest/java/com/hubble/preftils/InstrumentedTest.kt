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
        lateinit var preferences: SharedPreferences

        @BeforeClass
        @JvmStatic
        internal fun initialize() {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            preferences = appContext.applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)

            with (preferences.edit()) {
                remove(PreferencesTest.NUMBER.key)
                remove(PreferencesSerializable.SERIALIZABLE.key)
                apply()
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
    fun testDefaults() {
        with (preferences.edit()) {
            remove(PreferencesTest.NUMBER.key)
            apply()
        }
        assertEquals(PreferencesTest.NUMBER.default, preferences.get(PreferencesTest.NUMBER))
        assertEquals(-3, preferences.get(PreferencesTest.NUMBER, -3))
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
}
