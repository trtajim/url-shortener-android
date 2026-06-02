package com.tajim.urlshortener.api;

import android.content.Context;

import okhttp3.OkHttpClient;

public class ApiClient {

    private static OkHttpClient client;

    public static OkHttpClient getClient(Context context) {

        if (client == null) {

            client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context))
                    .build();
        }

        return client;
    }
}