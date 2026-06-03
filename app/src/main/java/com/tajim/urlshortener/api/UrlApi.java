package com.tajim.urlshortener.api;

import android.content.Context;

import com.tajim.urlshortener.utils.SessionManager;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UrlApi {
    private final Context context;
    private final SessionManager sessionManager;
    String url;

    public UrlApi(Context context) {
        this.context = context.getApplicationContext();
        sessionManager = new SessionManager(context);
        url = ApiConfig.API_BASE_URL+"/urls";
    }
    public void getAllUrls(Callback callback){

        Request request = ApiRequest
                .authorized(sessionManager.getToken())
                .url(url)
                .get()
                .build();

        ApiClient
                .getClient(context)
                .newCall(request)
                .enqueue(callback);

    }

    public void createUrl(String longUrl, String shortCode, String password, Callback callback){

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("long_url", longUrl);
        if (shortCode != null) bodyBuilder.add("short_code", shortCode);
        if (password != null) bodyBuilder.add("password", password);

        RequestBody body = bodyBuilder.build();

        Request request = ApiRequest
                .authorized(sessionManager.getToken())
                .url(url)
                .post(body)
                .build();

        ApiClient
                .getClient(context)
                .newCall(request)
                .enqueue(callback);
    }
}
