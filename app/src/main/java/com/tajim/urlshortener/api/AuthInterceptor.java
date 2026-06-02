package com.tajim.urlshortener.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.tajim.urlshortener.auth.VerifyEmailActivity;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;
    SessionManager sessionManager;

    public AuthInterceptor(Context context) {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.code() == 401) {

            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, "Session expired", Toast.LENGTH_SHORT).show();
            });
            sessionManager.logout();


        }else if (response.code() == 403){
            AppUtils.clearAllActivitiesAndNavigate(context, VerifyEmailActivity.class);
        }
        return response;

    }

}
