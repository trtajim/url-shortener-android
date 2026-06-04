package com.tajim.urlshortener.api;

import android.content.Context;

import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthApi {
    Context context;
    SessionManager sessionManager;
    public AuthApi(Context context){
        this.context = context.getApplicationContext();
        sessionManager = new SessionManager(context);
    }
    public void login (String email, String password, Callback callback){

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

        ApiClient.getClient(context)
                .newCall(request)
                .enqueue(callback);


    }

    public void register(String name, String email,String password, Callback callback){

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

        ApiClient.getClient(context)
                .newCall(request)
                .enqueue(callback);


    }

    public void logout(Callback callback){

        String url = ApiConfig.API_BASE_URL+"/logout";
        RequestBody body = new FormBody.Builder().build();

        Request request = ApiRequest.authorized(sessionManager.getToken())
                .url(url)
                .post(body)
                .build();
                ApiClient.getClient(context)
                .newCall(request)
                .enqueue(callback);

    }

    public void sendVerificationMail(Callback callback){
        String url = ApiConfig.API_BASE_URL+"/verification/send";
        RequestBody body = new FormBody.Builder().build();

        Request request = ApiRequest.authorized(sessionManager.getToken())
                .url(url)
                .post(body)
                .build();

        ApiClient.getClient(context)
                .newCall(request)
                .enqueue(callback);

    }

    public void getUser(Callback callback){
        String url = ApiConfig.API_BASE_URL+"/me";

        Request request = ApiRequest.authorized(sessionManager.getToken())
                .url(url)
                .get()
                .build();

        ApiClient.getClient(context)
                .newCall(request)
                .enqueue(callback);
    }

    public void updateUser(String name, String oldPassword, String newPassword, Callback callback) {

        String url = ApiConfig.API_BASE_URL + "/me";

        FormBody.Builder bodyBuilder = new FormBody.Builder()
                .add("name", name);

        if (newPassword != null && !newPassword.isEmpty()) {
            bodyBuilder.add("old_password", oldPassword);
            bodyBuilder.add("new_password", newPassword);
        }

        RequestBody body = bodyBuilder.build();

        Request request = ApiRequest.authorized(sessionManager.getToken())
                .url(url)
                .patch(body)
                .build();

        ApiClient.getClient(context)
                .newCall(request)
                .enqueue(callback);
    }
}
