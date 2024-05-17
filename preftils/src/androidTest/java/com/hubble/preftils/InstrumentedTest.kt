package com.hubble.preftils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    object PreferencesTest {
        val NUMBER = Preference("number", 1)
    }

    @Test
    fun test() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = appContext.applicationContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        val initial = prefs.get(PreferencesTest.NUMBER)
        val target = 2
        assertEquals(PreferencesTest.NUMBER.default, initial)

        with (prefs.edit()) {
            put(PreferencesTest.NUMBER, target)
            apply()
        }

        val set = prefs.get(PreferencesTest.NUMBER)
        assertEquals(target, set)
    }
}