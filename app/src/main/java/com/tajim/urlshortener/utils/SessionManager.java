package com.tajim.urlshortener.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "token";

    private final SharedPreferences sharedPreferences;

    public SessionManager(Context context) {

        sharedPreferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    // Save token
    public void saveToken(String token) {

        sharedPreferences.edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    // Check login status
    public boolean isLoggedIn() {

        return sharedPreferences.getString(KEY_TOKEN, null) != null;
    }

    // Logout user
    public void logout() {

        sharedPreferences.edit().clear().apply();
    }

    // Get token (useful for API calls)
    public String getToken() {

        return sharedPreferences.getString(KEY_TOKEN, null);
    }
}