package com.example.periodic_broadcast_recevier.prefernce;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_LAST_INTERACTION_TIME = "last_interaction_time";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveLastInteractionTime(Context context, long lastInteractionTime) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putLong(KEY_LAST_INTERACTION_TIME, lastInteractionTime);
        editor.apply();
    }

    public static long getLastInteractionTime(Context context) {
        return getSharedPreferences(context).getLong(KEY_LAST_INTERACTION_TIME, 0);
    }
}

