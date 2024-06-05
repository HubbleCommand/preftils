package com.hubble.preftils

import android.content.Context
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

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
                remove(PreferencesCodableTest.CODABLE.key)
                apply()
            }
        }
    }

    @Test
    fun test() {
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

    class CodableTest(val a: Int, val b: Boolean): ICodable {
        override fun encode(): String {
            return "$a,$b"
        }

        override fun decode(string: String): CodableTest {
            val split = string.split(",")
            return CodableTest(split[0].toInt(), split[1].toBooleanStrict())
        }
    }

    object PreferencesCodableTest {
        val CODABLE = Preference("codable", CodableTest(17, false))
    }

    @Test
    fun testCodable() {
        val initial = preferences.get(PreferencesCodableTest.CODABLE)
        assertEquals(PreferencesCodableTest.CODABLE.default.a, initial.a)
        assertEquals(PreferencesCodableTest.CODABLE.default.b, initial.b)

        val target = CodableTest(20, true)
        with (preferences.edit()) {
            put(PreferencesCodableTest.CODABLE, target)
            apply()
        }

        val set = preferences.get(PreferencesCodableTest.CODABLE)
        assertEquals(target.a, set.a)
        assertEquals(target.b, set.b)
    }
}
