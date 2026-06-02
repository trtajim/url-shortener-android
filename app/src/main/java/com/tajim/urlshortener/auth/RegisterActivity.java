package com.tajim.urlshortener.auth;

import static android.view.View.GONE;
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
import com.tajim.urlshortener.databinding.ActivityRegisterBinding;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
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
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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
        binding.registerProgressBar.setVisibility(GONE);

        AppUtils.clearErrorOnTextChange(binding.tieEmail, binding.tilEmail);
        AppUtils.clearErrorOnTextChange(binding.tiePassword, binding.tilPassword);
        AppUtils.clearErrorOnTextChange(binding.tieConfirmPassword, binding.tilConfirmPassword);
        AppUtils.clearErrorOnTextChange(binding.tieName, binding.tilName);


    }


    private void initClickListeners(){
        binding.imgBack.setOnClickListener(v->{onBackPressed();});

        binding.tvPrivacy.setOnClickListener(v->{
            String url = ApiConfig.SERVER_BASE_URL+"/privacy-policy";
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, url);
        });

        binding.tvTerms.setOnClickListener(v->{
            String url = ApiConfig.SERVER_BASE_URL+"/terms-conditions";
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, url);
        });

        binding.btnRegister.setOnClickListener(v->{
            String name = binding.tieName.getText().toString().trim();
            String email = binding.tieEmail.getText().toString().trim();
            String password = binding.tiePassword.getText().toString().trim();
            String confirmPassword = binding.tieConfirmPassword.getText().toString().trim();

            if(name.isEmpty() || name.length() < 3 || name.length() > 255){
                binding.tilName.setError("Please Enter a valid name");
                return;
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.tilEmail.setError("Please enter a valid email address");
                return;
            }

            if (password.isEmpty() || password.length() < 8){
                binding.tilPassword.setError("Password must be at least 8 characters");
                return;
            }

            if (!confirmPassword.equals(password)){
                binding.tilConfirmPassword.setError("Passwords do not match");
                return;
            }

            register(name,email,password);



        });
    }
    private void register(String name, String email, String password){
        AppUtils.startLoading(this, "Creating Account...");
        authApi.register(name, email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(AppUtils::endLoading);
                AppUtils.makeToast(RegisterActivity.this, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(AppUtils::endLoading);
                String body = response.body().string();

                JSONObject jsonObject = AppUtils.getJsonObjFromString(body);

                if (jsonObject == null) {
                    AppUtils.makeToast(RegisterActivity.this, "Invalid server response");
                    return;
                }

                if (!response.isSuccessful()){
                    String message = AppUtils.getStringFromJsonObject(jsonObject, "message", "Something went wrong");
                    AppUtils.makeToast(RegisterActivity.this, message);
                    return;
                }

                String token = AppUtils.getStringFromJsonObject(jsonObject, "token", null);

                if (!jsonObject.has("token") || jsonObject.isNull("token")) {
                    AppUtils.makeToast(RegisterActivity.this, "Token not found");
                    return;
                }

                sessionManager.saveToken(token);


                AppUtils.makeToast(RegisterActivity.this, "Account created Successfully");
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