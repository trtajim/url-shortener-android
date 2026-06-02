package com.tajim.urlshortener.api;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.tajim.urlshortener.auth.LoginActivity;
import com.tajim.urlshortener.utils.AppUtils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public abstract class SafeCallback implements Callback {
    private final Activity activity;

    protected SafeCallback(Activity activity) {
        this.activity = activity;
    }

    public abstract void onSuccess(String bodyFromResponse);

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        activity.runOnUiThread(AppUtils::endLoading);
        AppUtils.makeToast(activity, e.getMessage());
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        activity.runOnUiThread(AppUtils::endLoading);
        String body = response.body().string();

        JSONObject jsonObject = AppUtils.getJsonObjFromString(body);

        if (jsonObject == null) {
            AppUtils.makeToast(activity, "Invalid server response");
            return;
        }

        if (!response.isSuccessful()){
            String message = AppUtils.getStringFromJsonObject(jsonObject, "message", "Something went wrong");
            AppUtils.makeToast(activity, message);
            return;
        }

        activity.runOnUiThread(() -> onSuccess(body));


    }
}
