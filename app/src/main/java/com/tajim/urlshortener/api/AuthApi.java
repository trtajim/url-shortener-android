package com.tajim.urlshortener.api;

import com.tajim.urlshortener.utils.AppUtils;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthApi {
    public static void login (String email, String password, Callback callback){

        String url = ApiConfig.API_BASE_URL+"/login";
        String deviceName = AppUtils.getDeviceName();

        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("device_name", deviceName)
                .build();

        Request request = ApiRequest.guest()
                .url(url)
                .post(body)
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);


    }

    public static void register(String name, String email,String password, Callback callback){

        String url = ApiConfig.API_BASE_URL+"/register";

        String deviceName = AppUtils.getDeviceName();

        RequestBody body = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .add("device_name", deviceName)
                .build();

        Request request = ApiRequest.guest()
                .url(url)
                .post(body)
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);


    }

    public static void logout(String token, Callback callback){
        String url = ApiConfig.API_BASE_URL+"/logout";
        RequestBody body = new FormBody.Builder().build();

        Request request = ApiRequest.authorized(token)
                .url(url)
                .post(body)
                .build();
        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);

    }

    public static void sendVerificationMail(String token, Callback callback){
        String url = ApiConfig.API_BASE_URL+"/verification/send";
        RequestBody body = new FormBody.Builder().build();

        Request request = ApiRequest.authorized(token)
                .url(url)
                .post(body)
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);

    }

    public static void getUser(String token, Callback callback){
        String url = ApiConfig.API_BASE_URL+"/me";

        Request request = ApiRequest.authorized(token)
                .url(url)
                .get()
                .build();

        ApiClient.getClient()
                .newCall(request)
                .enqueue(callback);
    }
}
