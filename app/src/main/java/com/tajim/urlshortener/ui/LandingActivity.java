package com.tajim.urlshortener.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.R;
import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.auth.LoginActivity;
import com.tajim.urlshortener.auth.RegisterActivity;
import com.tajim.urlshortener.databinding.ActivityLandingBinding;
import com.tajim.urlshortener.utils.AppUtils;

public class LandingActivity extends AppCompatActivity {
    ActivityLandingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setupLayout();
        initClickListeners();


    }
    private void enableEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initClickListeners(){
        binding.btnLogin.setOnClickListener(v->{
            startActivity(new Intent(LandingActivity.this, LoginActivity.class));
        });
        binding.btnRegister.setOnClickListener(v->{
            startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
        });

        binding.tvPrivacy.setOnClickListener(v->{
            String url = ApiConfig.SERVER_BASE_URL+"/privacy-policy";
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, url);
        });
        binding.tvTerms.setOnClickListener(v->{
            String url = ApiConfig.SERVER_BASE_URL+"/terms-conditions";
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, url);
        });


    }
}