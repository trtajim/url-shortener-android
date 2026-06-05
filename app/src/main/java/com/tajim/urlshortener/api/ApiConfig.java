package com.tajim.urlshortener.api;

import com.tajim.urlshortener.BuildConfig;

public class ApiConfig {
    public static final String SERVER_BASE_URL = BuildConfig.SERVER_BASE_URL;

    public static final String API_BASE_URL = SERVER_BASE_URL+"/api";
    public static final String GOOGLE_CLIENT_ID = BuildConfig.GOOGLE_CLIENT_ID;
}