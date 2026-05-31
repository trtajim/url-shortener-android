package com.tajim.urlshortener;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.databinding.ActivityMainBinding;
import com.tajim.urlshortener.ui.LandingActivity;
import com.tajim.urlshortener.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setupLayout();
        initVariables();
        checkUserStatus();


    }
    private void enableEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initVariables(){
        sessionManager = new SessionManager(this);
    }
    private void checkUserStatus(){
        if (!sessionManager.isLoggedIn()){
            startActivity(new Intent(MainActivity.this, LandingActivity.class));
            finish();
        }

    }

}