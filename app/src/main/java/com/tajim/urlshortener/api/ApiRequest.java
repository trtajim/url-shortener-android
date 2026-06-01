package com.tajim.urlshortener.api;

import okhttp3.Request;

public final class ApiRequest {
    public static Request.Builder authorized(String token){
        return new Request.Builder()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer "+token);
    }

    public static Request.Builder guest() {
        return new Request.Builder()
                .addHeader("Accept", "application/json");
    }
}
