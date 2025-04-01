package com.example.swiftmart.EmailService;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesWithExpiry {
    private static final String PREFS_NAME = "APP_PREFS";
    private static final String DATA_KEY = "DATA";
    private static final String EXPIRY_KEY = "EXPIRY";

    // Save data with an expiry time
    public static void saveData(Context context, String data, long expiryInMillis) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DATA_KEY, data);
        editor.putLong(EXPIRY_KEY, System.currentTimeMillis() + expiryInMillis); // Set expiry timestamp
        editor.apply();
    }

    // Retrieve data, checking for expiry
    public static String getData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long expiryTime = sharedPreferences.getLong(EXPIRY_KEY, 0);

        if (System.currentTimeMillis() > expiryTime) {
            // Data has expired; clear it
            clearData(context);
            return null;
        }

        // Return data if valid
        return sharedPreferences.getString(DATA_KEY, null);
    }

    // Clear data
    public static void clearData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
