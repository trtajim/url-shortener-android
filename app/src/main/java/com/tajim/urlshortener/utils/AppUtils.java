package com.tajim.urlshortener.utils;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;

import com.tajim.urlshortener.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AppUtils {
    public static String getDeviceName(){
        return Build.MANUFACTURER + " " + Build.MODEL;
    }
    public static JSONObject getJsonObjFromString(String string){
        try {
            return new JSONObject(string);
        } catch (JSONException e) {
            return null;
        }

    }

    public static String getStringFromJsonObject(JSONObject jsonObject, String keyword, String fallback) {
        if (jsonObject == null) {
            return fallback;
        }

        try {
            return jsonObject.getString(keyword);
        } catch (JSONException e) {
            return fallback;
        }
    }

    public static void makeToast(Activity activity, String message){
        activity.runOnUiThread(()->{
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        });
    }

    public static void turnOffKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)
                view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void openLinksInCustomChromeTabOrBrowser(Context context, String url) {

        int color = context.getResources().getColor(R.color.crimson_red);

        CustomTabsIntent customTabsIntent =
                new CustomTabsIntent.Builder()
                        .setToolbarColor(color)
                        .setShowTitle(true)
                        .build();

        try {
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "You have no browser installed", Toast.LENGTH_SHORT).show();
        }

    }
}
