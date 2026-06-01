package com.tajim.urlshortener.api;

import okhttp3.OkHttpClient;

public class ApiClient {

    private static OkHttpClient client;

    public static OkHttpClient getClient() {

        if (client == null) {

            client = new OkHttpClient.Builder()
                    .build();
        }

        return client;
    }
}