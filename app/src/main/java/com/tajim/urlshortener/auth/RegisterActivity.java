package com.tajim.urlshortener.auth;

import static android.view.View.GONE;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.api.AuthApi;
import com.tajim.urlshortener.api.SafeCallback;
import com.tajim.urlshortener.databinding.ActivityRegisterBinding;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    SessionManager sessionManager;
    AuthApi authApi;
    CredentialManager credentialManager;
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
        credentialManager = CredentialManager.create(this);
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

        binding.btnGoogleRegister.setOnClickListener(v->{
            requestGoogleLogin();
        });
    }
    private void register(String name, String email, String password){
        AppUtils.startLoading(this, "Creating Account...");

        authApi.register(name, email, password, new SafeCallback(this) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                handleSuccessRegister(bodyFromResponse);
            }
        });



    }
    private void handleSuccessRegister(String bodyFromResponse){

        JSONObject jsonObject = AppUtils.getJsonObjFromString(bodyFromResponse);

        String token = AppUtils.getStringFromJsonObject(jsonObject, "token", null);

        if (!jsonObject.has("token") || jsonObject.isNull("token")) {
            Toast.makeText(RegisterActivity.this, "Token not found", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject user = AppUtils.getJsonObjOrNullFromJsonObj(jsonObject, "user");
        String name = user.optString("name");
        String email = user.optString("email");

        sessionManager.saveUser(name,email,token);

        Toast.makeText(RegisterActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
        sessionManager.routeUser(user);
    }

    private void requestGoogleLogin(){
        GetGoogleIdOption getGoogleIdOption  = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(ApiConfig.GOOGLE_CLIENT_ID)
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(getGoogleIdOption)
                .build();

        credentialManager.getCredentialAsync(this,
                request,
                new android.os.CancellationSignal(),
                getMainExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        GoogleIdTokenCredential credential = GoogleIdTokenCredential.createFrom(getCredentialResponse.getCredential().getData());

                        String idToken = credential.getIdToken();
                        callSocialLogin("google",idToken);

                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Toast.makeText(RegisterActivity.this, "Sign-Up failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();                    }
                });

    }
    private void callSocialLogin(String provider, String id_token){
        authApi.socialLogin(provider, id_token, new SafeCallback(this) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                handleSuccessRegister(bodyFromResponse);
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