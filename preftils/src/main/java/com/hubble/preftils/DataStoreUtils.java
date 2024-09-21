package com.hubble.preftils;


import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DataStoreUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> Preferences.Key<T> preferenceToKey(PreferenceUtils.Preference<T> preference) {
        if (preference.getDefault() instanceof Integer) {
            return (Preferences.Key<T>) PreferencesKeys.intKey(preference.getKey());
        } else if (preference.getDefault() instanceof Long) {
            return (Preferences.Key<T>) PreferencesKeys.longKey(preference.getKey());
        } else if (preference.getDefault() instanceof Float) {
            return (Preferences.Key<T>) PreferencesKeys.floatKey(preference.getKey());
        } else if (preference.getDefault() instanceof Boolean) {
            return (Preferences.Key<T>) PreferencesKeys.booleanKey(preference.getKey());
        } else if (preference.getDefault() instanceof String) {
            return (Preferences.Key<T>) PreferencesKeys.stringKey(preference.getKey());
        } else {
            return (Preferences.Key<T>) PreferencesKeys.stringKey(preference.getKey());
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Serializable> T get(Preferences preferences, PreferenceUtils.Preference<T> preference) throws IOException, ClassNotFoundException {
        if (preference.getDefault() instanceof Integer || preference.getDefault() instanceof Long ||  preference.getDefault() instanceof Float || preference.getDefault() instanceof Boolean || preference.getDefault() instanceof String) {
            return preferences.get(preferenceToKey(preference));
        } else {
            String string = (String) preferences.get(preferenceToKey(preference));

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

    @SuppressWarnings("unchecked")
    static <T extends Serializable> void put(MutablePreferences mutablePreferences, PreferenceUtils.Preference<T> preference, @NotNull T value) throws IOException {
        if (value instanceof Integer || value instanceof Long ||  value instanceof Float || value instanceof Boolean || value instanceof String) {
            mutablePreferences.set(preferenceToKey(preference), value);
        } else {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(outStream);

            out.writeObject(value);
            out.flush();
            out.close();

            mutablePreferences.set(preferenceToKey(preference), (T) outStream.toString());
            outStream.flush();
            outStream.close();
        }
    }
}
