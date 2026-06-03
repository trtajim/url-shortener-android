package com.tajim.urlshortener.auth;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
        binding.tv2.setText("We’ve sent a verification email to\n"+sessionManager.getEmail()+". Please check your inbox, including the spam folder, to continue.");
    }
    private void setupClickListeners(){

        binding.btnLogout.setOnClickListener(v -> logout());
        binding.tvResendMail.setOnClickListener(v -> sendVerificationMail());
        binding.btnVerified.setOnClickListener(v -> checkIfVerified());
    }
    private void logout(){

        AppUtils.showMaterialDialog(
                this,
                "Sign Out",
                "Are you sure you want to sign out? You will need to sign in again to access your account.",
                "Log Out",
                (dialog, which) -> {
                    AppUtils.startLoading(this,"Signing out...");
                    authApi.logout(new SafeCallback(this) {
                        @Override
                        public void onSuccess(String bodyFromResponse) {

                            sessionManager.clearTokenFromDevice();
                        }
                    });
                },
                "Stay Logged In",
                (dialog, which) -> dialog.dismiss()
        );


    }

    private void sendVerificationMail(){
        AppUtils.startLoading(this,"Sending verification email...");

        authApi.sendVerificationMail(new SafeCallback(this) {
            @Override
            public void onSuccess(String bodyFromResponse) {

                JSONObject jsonObject = AppUtils.getJsonObjFromString(bodyFromResponse);

                Toast.makeText(VerifyEmailActivity.this,AppUtils.getStringFromJsonObject(jsonObject, "message", "Verification Link was successfully sent") , Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(VerifyEmailActivity.this, " Email not verified. Please check your inbox.", Toast.LENGTH_SHORT).show();
                    return;
                }

                sessionManager.routeUser(user);


            }
        });

    }
}