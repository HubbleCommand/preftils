package com.hubble.preftils;

import static junit.framework.TestCase.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

public class InstrumentedTests {
    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;

    @BeforeClass
    public static void setup(){
        preferences = InstrumentationRegistry.getInstrumentation().getTargetContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = preferences.edit();

        editor.remove(BasicPreferences.NUMBER.getKey());
        editor.apply();
        editor.commit();
    }

    static class BasicPreferences {
        static PreferenceUtils.Preference<Integer> NUMBER = new PreferenceUtils.Preference<>("int", 10);
    }

    @Test
    public void testJavaBasic() throws IOException, ClassNotFoundException {
        //as intended, intellisense will show error & fails to compile
        //PreferenceUtils.set(editor, preference, "10");

        //Test read
        Integer prefVal = PreferenceUtils.get(preferences, BasicPreferences.NUMBER);
        assertEquals(BasicPreferences.NUMBER.getDefault(), prefVal);

        //Test write
        PreferenceUtils.put(editor, BasicPreferences.NUMBER, 10);
    }

    public static class SerializableType implements Serializable {
        SerializableType(int integer, String string) {
            this.integer = integer;
            this.string = string;
        }

        public int integer;
        public String string;

        @Override
        public boolean equals(@Nullable Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof SerializableType)) {
                return false;
            }

            SerializableType t = (SerializableType) o;
            return this.integer == t.integer && this.string.equals(t.string);
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("{integer: %d, string: %s}", this.integer, this.string);
        }
    }

    static class SerializablePreferences {
        static PreferenceUtils.Preference<SerializableType> SERIALIZABLE = new PreferenceUtils.Preference<>("int", new SerializableType(10, "string"));
    }

    @Test
    public void testJavaSerialization() throws IOException, ClassNotFoundException {
        //Test read
        SerializableType prefVal = SerializablePreferences.SERIALIZABLE.getValue(preferences);
        assertEquals(SerializablePreferences.SERIALIZABLE.getDefault(), prefVal);

        //Test write
        int newInt = 23;
        prefVal.integer = newInt;
        String newString = "another string";
        prefVal.string = newString;
        SerializablePreferences.SERIALIZABLE.putValue(editor, prefVal);

        //Verify write
        prefVal = PreferenceUtils.get(preferences, SerializablePreferences.SERIALIZABLE);
        System.out.println(prefVal.toString());
        assertEquals(new SerializableType(newInt, newString), prefVal);
    }

    class NonSerializableType { }

    @Test
    public void testJavaIncompatibleType() throws IOException, ClassNotFoundException {
        //This will not compile, as NonSerializableType does not implement Serializable
        //PreferenceUtils.Preference<NonSerializableType> INCOMPATIBLE = new PreferenceUtils.Preference<>("int", new NonSerializableType());

        //Primitives supported by SharedPreferences will work as expected
        PreferenceUtils.Preference<String> str = new PreferenceUtils.Preference<>("str", "string");
        PreferenceUtils.Preference<Integer> nt = new PreferenceUtils.Preference<>("int", 10);
        PreferenceUtils.Preference<Long> lng = new PreferenceUtils.Preference<>("lng", 10L);
        PreferenceUtils.Preference<Boolean> bln = new PreferenceUtils.Preference<>("bln", true);
        PreferenceUtils.Preference<Float> flt = new PreferenceUtils.Preference<>("flt", 10f);

        assertEquals(PreferenceUtils.get(preferences, str), "string");
        assertEquals(PreferenceUtils.get(preferences, nt), (Integer) 10);
        assertEquals(PreferenceUtils.get(preferences, lng), (Long) 10L);
        assertEquals(PreferenceUtils.get(preferences, bln), (Boolean) true);
        assertEquals(PreferenceUtils.get(preferences, flt), 10f);
    }
}
