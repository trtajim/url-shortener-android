package com.tajim.urlshortener.api;

import android.content.Context;

import com.tajim.urlshortener.utils.SessionManager;

import okhttp3.Callback;
import okhttp3.Request;

public class UrlApi {
    private final Context context;
    private final SessionManager sessionManager;

    public UrlApi(Context context) {
        this.context = context.getApplicationContext();
        sessionManager = new SessionManager(context);
    }
    public void getAllUrls(Callback callback){
        String url = ApiConfig.API_BASE_URL+"/urls";

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
}
