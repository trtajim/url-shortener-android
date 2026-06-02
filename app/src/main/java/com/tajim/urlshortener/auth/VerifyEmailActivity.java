package com.tajim.urlshortener.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.api.AuthApi;
import com.tajim.urlshortener.api.SafeCallback;
import com.tajim.urlshortener.databinding.ActivityVerifyEmailBinding;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    ActivityVerifyEmailBinding binding;
    SessionManager sessionManager;
    AuthApi authApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setupLayout();
        initVariables();
        setupClickListeners();


    }
    private void enableEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityVerifyEmailBinding.inflate(getLayoutInflater());
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initVariables(){
        authApi = new AuthApi(this);
        sessionManager = new SessionManager(this);
    }
    private void setupClickListeners(){

        binding.btnLogout.setOnClickListener(v->{
           logout();
        });
        binding.tvResendMail.setOnClickListener(v->{
            sendVerificationMail();
        });

        binding.btnVerified.setOnClickListener(v->{
            checkIfVerified();
        });
    }
    private void logout(){
        AppUtils.startLoading(this,"Signing out...");

        authApi.logout(new SafeCallback(this) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                sessionManager.logout();
            }
        });

    }

    private void sendVerificationMail(){
        AppUtils.startLoading(this,"Sending verification email...");

        authApi.sendVerificationMail(new SafeCallback(this) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                JSONObject jsonObject = AppUtils.getJsonObjFromString(bodyFromResponse);

                AppUtils.makeToast(VerifyEmailActivity.this, AppUtils.getStringFromJsonObject(jsonObject, "message", "Verification Link was successfully sent"));
            }
        });

    }

    private void checkIfVerified(){
        AppUtils.startLoading(this,"Checking verification status...");
        authApi.getUser(new SafeCallback(this) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                JSONObject user = AppUtils.getJsonObjFromString(bodyFromResponse);
                String emailVerifiedStatus = AppUtils.getStringFromJsonObject(user, "email_verified_at", null);
                if (emailVerifiedStatus == null || emailVerifiedStatus.isEmpty() || emailVerifiedStatus.equals("null")) {
                    AppUtils.makeToast(VerifyEmailActivity.this, "Email not verified. Please check your inbox.");
                    return;
                }

                sessionManager.routeUser(user);


            }
        });

    }
}