package com.tajim.urlshortener.auth;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.tajim.urlshortener.MainActivity;
import com.tajim.urlshortener.R;
import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.api.AuthApi;
import com.tajim.urlshortener.databinding.ActivityLandingBinding;
import com.tajim.urlshortener.databinding.ActivityLoginBinding;
import com.tajim.urlshortener.ui.LandingActivity;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setupLayout();
        initVariables();
        initClickListeners();
    }
    private void enableEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initVariables(){
        sessionManager = new SessionManager(this);
        binding.loginProgressBar.setVisibility(GONE);
        AppUtils.clearErrorOnTextChange(binding.tieEmail, binding.tilEmail);
        AppUtils.clearErrorOnTextChange(binding.tiePassword, binding.tilPassword);
    }
    private void initClickListeners(){
        binding.btnLogin.setOnClickListener(v->{
            String email = binding.tieEmail.getText().toString().trim();
            String password = binding.tiePassword.getText().toString().trim();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.tilEmail.setError("Please enter a valid email address");
                return;
            }

            if (password.isEmpty() || password.length() < 8){
                binding.tilPassword.setError("Password must be at least 8 characters");
                return;
            }

            login(email,password);

        });

        binding.tvForgotPassword.setOnClickListener(v->{
            String url = ApiConfig.SERVER_BASE_URL+"/forgot-password";

            AppUtils.openLinksInCustomChromeTabOrBrowser(this, url);
        });

        binding.tvRegister.setOnClickListener(v->{
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void startLoading(){
        binding.btnLogin.setText("");
        binding.btnLogin.setEnabled(false);
        binding.loginProgressBar.setVisibility(VISIBLE);
    }
    private void endLoading(){
        runOnUiThread(()->{
            binding.btnLogin.setText("Log In");
            binding.btnLogin.setEnabled(true);
            binding.loginProgressBar.setVisibility(GONE);
        });
    }

    private void login(String email, String password){

        startLoading();
        AuthApi.login(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                endLoading();
                AppUtils.makeToast(LoginActivity.this, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                endLoading();
                String body = response.body().string();

                JSONObject jsonObject = AppUtils.getJsonObjFromString(body);

                if (jsonObject == null) {
                    AppUtils.makeToast(LoginActivity.this, "Invalid server response");
                    return;
                }

                if (!response.isSuccessful()){
                    String message = AppUtils.getStringFromJsonObject(jsonObject, "message", "Something went wrong");
                    AppUtils.makeToast(LoginActivity.this, message);
                    return;
                }

                String token = AppUtils.getStringFromJsonObject(jsonObject, "token", null);
                if (!jsonObject.has("token") || jsonObject.isNull("token")) {
                    AppUtils.makeToast(LoginActivity.this, "Token not found");
                    return;
                }

                sessionManager.saveToken(token);

                runOnUiThread(() -> {
                    AppUtils.makeToast(LoginActivity.this, "Logged in Successfully");

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                });

            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null) {
            view.clearFocus();
            AppUtils.turnOffKeyboard(view);

        }
        return super.dispatchTouchEvent(ev);
    }
}