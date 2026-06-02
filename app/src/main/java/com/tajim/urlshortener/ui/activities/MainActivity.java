package com.tajim.urlshortener.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.tajim.urlshortener.R;
import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.databinding.ActivityMainBinding;
import com.tajim.urlshortener.ui.fragments.DashboardFragment;
import com.tajim.urlshortener.ui.fragments.ProfileFragment;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    SessionManager sessionManager;
    ActionBarDrawerToggle toggle;
    private DashboardFragment dashboardFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setupLayout();
        initVariables();
        initNavigationDrawer();
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
        dashboardFragment = new DashboardFragment();
        profileFragment = new ProfileFragment();
        loadFragment(dashboardFragment);
        sessionManager = new SessionManager(this);
    }
    private void initNavigationDrawer(){
        setSupportActionBar(binding.toolbar);

        binding.navView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.open, R.string.close);

        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setCheckedItem(R.id.dashboard);


        binding.btnPrivacy.setOnClickListener(v->{
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, ApiConfig.SERVER_BASE_URL+"/privacy-policy");

        });

        binding.btnTerms.setOnClickListener(v->{
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, ApiConfig.SERVER_BASE_URL+"/terms-conditions");

        });



    }
    private void checkUserStatus(){
        if (!sessionManager.isLoggedIn()){
            startActivity(new Intent(MainActivity.this, LandingActivity.class));
            finish();
        }

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.fragmentContainer.getId(), fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        menuItem.setChecked(true);


        if (id == R.id.dashboard) {
            loadFragment(dashboardFragment);
        } else if (id == R.id.profile) {
            loadFragment(profileFragment);
        } else if (id == R.id.visit_web) {
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, ApiConfig.SERVER_BASE_URL);
        }else if (id == R.id.about_developer) {
            AppUtils.openLinksInCustomChromeTabOrBrowser(this, "https://tajimz.xyz");
        }else if (id == R.id.logout) {
            sessionManager.logout();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}