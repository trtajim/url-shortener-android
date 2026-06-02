package com.tajim.urlshortener.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.MainActivity;
import com.tajim.urlshortener.R;
import com.tajim.urlshortener.api.AuthApi;
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
        AppUtils.startLoading(this);
        authApi.logout(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(AppUtils::endLoading);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(AppUtils::endLoading);

                String body = response.body().string();

                JSONObject jsonObject = AppUtils.getJsonObjFromString(body);

                if (jsonObject == null) {
                    AppUtils.makeToast(VerifyEmailActivity.this, "Invalid server response");
                    return;
                }

                if (!response.isSuccessful()){
                    String message = AppUtils.getStringFromJsonObject(jsonObject, "message", "Something went wrong");
                    AppUtils.makeToast(VerifyEmailActivity.this, message);
                    return;
                }

                sessionManager.logout();


            }
        });
    }

    private void sendVerificationMail(){
        AppUtils.startLoading(this);
        authApi.sendVerificationMail( new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(AppUtils::endLoading);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(AppUtils::endLoading);

                String body = response.body().string();


                JSONObject jsonObject = AppUtils.getJsonObjFromString(body);

                if (jsonObject == null) {
                    AppUtils.makeToast(VerifyEmailActivity.this, "Invalid server response");
                    return;
                }

                if (!response.isSuccessful()){
                    String message = AppUtils.getStringFromJsonObject(jsonObject, "message", "Something went wrong");
                    AppUtils.makeToast(VerifyEmailActivity.this, message);
                    return;
                }
                
                AppUtils.makeToast(VerifyEmailActivity.this, AppUtils.getStringFromJsonObject(jsonObject, "message", "Verification Link was successfully sent"));


            }
        });
    }

    private void checkIfVerified(){
        AppUtils.startLoading(this);
        authApi.getUser(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(AppUtils::endLoading);

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                runOnUiThread(AppUtils::endLoading);

                String body = response.body().string();


                JSONObject user = AppUtils.getJsonObjFromString(body);

                if (user == null) {
                    AppUtils.makeToast(VerifyEmailActivity.this, "Invalid server response");
                    return;
                }

                if (!response.isSuccessful()){
                    String message = AppUtils.getStringFromJsonObject(user, "message", "Something went wrong");
                    AppUtils.makeToast(VerifyEmailActivity.this, message);
                    return;
                }

                sessionManager.routeUser(user);



            }
        });
    }
}