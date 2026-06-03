package com.tajim.urlshortener.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tajim.urlshortener.api.SafeCallback;
import com.tajim.urlshortener.api.UrlApi;
import com.tajim.urlshortener.databinding.ActivityCreateUrlBinding;

public class CreateUrlActivity extends AppCompatActivity {

    ActivityCreateUrlBinding binding;
    UrlApi urlApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setupLayout();
        initVariables();
        initListeners();

    }
    private void enableEdgeToEdge(){
        EdgeToEdge.enable(this);
    }
    private void setupLayout(){
        binding = ActivityCreateUrlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initVariables(){
        urlApi = new UrlApi(this);
    }
    private void initListeners(){
        binding.btnCreate.setOnClickListener(v->{
            String longUrl = binding.tieLongUrl.getText().toString().trim();
            String shortCode = binding.tieShortCode.getText().toString().trim();
            String password = binding.tiePassword.getText().toString().trim();

            if (longUrl.isEmpty()){
                Toast.makeText(this, "Please enter a long URL", Toast.LENGTH_SHORT).show();
                return;
            }

            urlApi.createUrl(longUrl, shortCode, password, new SafeCallback(this) {
                @Override
                public void onSuccess(String bodyFromResponse) {
                    Toast.makeText(CreateUrlActivity.this, "Short URL created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
}