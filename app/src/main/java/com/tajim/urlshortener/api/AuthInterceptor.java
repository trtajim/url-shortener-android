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
        boolean isHandled = false;
        Response response = chain.proceed(chain.request());
        String body = response.peekBody(1024 * 8).string();

        if (response.code() == 401) {

            if (!body.contains("Unauthenticated.")) {
                return response;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(context, "Session expired", Toast.LENGTH_SHORT).show();
            });
            sessionManager.clearTokenFromDevice();
            AppUtils.logD("AuthInterceptor", "intercept: 401");
            isHandled = true;


        }else if (response.code() == 403){

            if (!body.contains("email address is not verified.")){
                return response;
            }
            AppUtils.clearAllActivitiesAndNavigate(context, VerifyEmailActivity.class);
            AppUtils.logD("AuthInterceptor", "intercept: 403");
            isHandled = true;
        }
        return response.newBuilder()
                .header("X-Auth-Handled", ""+isHandled)
                .build();

    }

}
