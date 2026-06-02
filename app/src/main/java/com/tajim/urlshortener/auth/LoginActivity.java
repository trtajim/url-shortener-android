package com.tajim.urlshortener.auth;

import static android.view.View.GONE;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.api.AuthApi;
import com.tajim.urlshortener.api.SafeCallback;
import com.tajim.urlshortener.databinding.ActivityLoginBinding;
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
    AuthApi authApi;
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
        authApi = new AuthApi(this);
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



    private void login(String email, String password){

        AppUtils.startLoading(this, "Logging in...");

        authApi.login(email, password, new SafeCallback(LoginActivity.this) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                JSONObject jsonObject = AppUtils.getJsonObjFromString(bodyFromResponse);

                String token = AppUtils.getStringFromJsonObject(jsonObject, "token", null);
                if (!jsonObject.has("token") || jsonObject.isNull("token")) {
                    AppUtils.makeToast(LoginActivity.this, "Token not found");
                    return;
                }

                sessionManager.saveToken(token);

                AppUtils.makeToast(LoginActivity.this, "Logged in Successfully");
                sessionManager.routeUser(AppUtils.getJsonObjOrNullFromJsonObj(jsonObject, "user"));

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