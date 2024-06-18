package com.hubble.preftils;

import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PreferenceUtils {
    static class Preference<T extends Serializable> {
        private final String key;
        private final T defaultValue;

        /**
         * Wrapper class for handling SharedPreferences
         *
         * @param key the key for the SharedPreference
         * @param defaultValue the default value for the SharedPreference
         */
        Preference(String key, @NotNull T defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public T getDefault() {
            return defaultValue;
        }

        /**
         * Instance helper that wraps PreferenceUtils.get()
         * @see PreferenceUtils#get(SharedPreferences, Preference) 
         */
        public T getValue(SharedPreferences preferences) throws IOException, ClassNotFoundException {
            return get(preferences, this);
        }

        /**
         * Instance helper that wraps PreferenceUtils.put()
         * @see PreferenceUtils#put(SharedPreferences.Editor, Preference, Serializable) 
         */
        public void putValue(SharedPreferences.Editor editor, @NotNull T value) throws IOException {
            put(editor, this, value);
        }
    }

    /**
     * Retrieves the value of a preference in a type-safe way
     * <p>
     * Propagates errors thrown by ObjectInputStream.readObject() if decoding is impossible
     *
     * @param preferences instance of SharedPreferences to read from
     * @param preference the preference to read
     * @return the value retrieved in SharedPreferences, or the default value of the [preference] parameter
     *
     * @throws  ClassNotFoundException Class of a serialized object cannot be
     *          found.
     * @throws  IOException Any of the usual Input/Output related exceptions.
     *
     * @see     ObjectInputStream#readObject()
     */
    @SuppressWarnings("unchecked")
    static <T extends Serializable> T get(SharedPreferences preferences, Preference<T> preference) throws IOException, ClassNotFoundException {
        if (preference.getDefault() instanceof Integer) {
            return (T) (Integer) preferences.getInt(preference.getKey(), (Integer) preference.getDefault());
        } else if (preference.getDefault() instanceof Long) {
            return (T) (Long) preferences.getLong(preference.getKey(), (Long) preference.getDefault());
        } else if (preference.getDefault() instanceof Float) {
            return (T) (Float) preferences.getFloat(preference.getKey(), (Float) preference.getDefault());
        } else if (preference.getDefault() instanceof Boolean) {
            return (T) (Boolean) preferences.getBoolean(preference.getKey(), (Boolean) preference.getDefault());
        } else if (preference.getDefault() instanceof String) {
            return (T) preferences.getString(preference.getKey(), (String) preference.getDefault());
        } else {
            String string = preferences.getString(preference.getKey(), null);

            if (string == null) {
                return preference.getDefault();
            }

            ByteArrayInputStream inStream = new ByteArrayInputStream(string.getBytes());
            ObjectInputStream in = new ObjectInputStream(inStream);

            T object = (T) in.readObject();

            inStream.close();
            in.close();

            return object;
        }
    }

    /**
     * Sets the value of a preference in a type-safe way
     * <p>
     * Propagates errors thrown by ObjectOutputStream.writeObject() if encoding is impossible
     *
     * @param editor Instance of SharedPreferences.Editor to write to
     * @param preference the Preference to set
     * @param value the value to set the Preference to
     *
     * @throws  IOException Any exception thrown by the underlying
     *          OutputStream.
     *
     * @see     ObjectOutputStream#writeObject(Object) ()
     */
    static <T extends Serializable> void put(SharedPreferences.Editor editor, Preference<T> preference, @NotNull T value) throws IOException {
        if (value instanceof Integer) {
            editor.putInt(preference.getKey(), (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(preference.getKey(), (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(preference.getKey(), (Float) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(preference.getKey(), (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(preference.getKey(), (String) value);
        } else {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outStream);

            out.writeObject(value);
            out.flush();
            out.close();

            editor.putString(preference.getKey(), outStream.toString());
            outStream.flush();
            outStream.close();
        }
    }
}
