package com.tajim.urlshortener.utils;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tajim.urlshortener.R;

import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

    public static void clearErrorOnTextChange(TextInputEditText textInputEditText, TextInputLayout textInputLayout){
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                textInputLayout.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });
    }

    public static void clearAllActivitiesAndNavigate(Context context, Class<?> targetActivity) {
        Intent intent = new Intent(context, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private static AlertDialog loadingDialog;

    public static void startLoading(Context context) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_alert_for_loading, null);


        loadingDialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        loadingDialog.show();
    }

    public static void endLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public static JSONObject getJsonObjOrNullFromJsonObj(JSONObject jsonObject, String keyword) {
        if (jsonObject == null) {
            return null;
        }

        try {
            return jsonObject.getJSONObject(keyword);
        } catch (JSONException e) {
            return null;
        }
    }
}
