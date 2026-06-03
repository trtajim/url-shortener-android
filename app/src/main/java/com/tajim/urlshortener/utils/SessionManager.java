package com.tajim.urlshortener.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.tajim.urlshortener.ui.activities.MainActivity;
import com.tajim.urlshortener.auth.VerifyEmailActivity;
import com.tajim.urlshortener.ui.activities.LandingActivity;

import org.json.JSONObject;

public class SessionManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences sharedPreferences;
    private final Context context;

    public SessionManager(Context context) {

        sharedPreferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
        this.context = context;
    }

    public void saveUser(String name, String email, String token) {

        sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public boolean isLoggedIn() {

        return sharedPreferences.getString(KEY_TOKEN, null) != null;
    }
    public void clearTokenFromDevice() {
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(context, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    public String getToken() {

        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    public String getName() {

        return sharedPreferences.getString(KEY_NAME, null);
    }
    public String getEmail() {

        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public void routeUser(JSONObject user){

        if (user == null){
            AppUtils.clearAllActivitiesAndNavigate(context, LandingActivity.class);
            return;
        }

        String emailVerifiedStatus = AppUtils.getStringFromJsonObject(user, "email_verified_at", null);

        if (emailVerifiedStatus == null || emailVerifiedStatus.isEmpty() || emailVerifiedStatus.equals("null")) {
            AppUtils.clearAllActivitiesAndNavigate(context, VerifyEmailActivity.class);
            return;
        }

        AppUtils.clearAllActivitiesAndNavigate(context, MainActivity.class);

    }



}