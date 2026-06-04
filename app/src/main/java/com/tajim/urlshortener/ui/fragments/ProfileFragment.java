package com.tajim.urlshortener.ui.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.api.AuthApi;
import com.tajim.urlshortener.api.SafeCallback;
import com.tajim.urlshortener.auth.VerifyEmailActivity;
import com.tajim.urlshortener.databinding.FragmentProfileBinding;
import com.tajim.urlshortener.utils.AppUtils;
import com.tajim.urlshortener.utils.SessionManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    SessionManager sessionManager;
    AuthApi authApi;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        initVariables();
        updateViews();
        updateProfile();

        return binding.getRoot();
    }

    private void initVariables(){
        sessionManager = new SessionManager(requireContext().getApplicationContext());
        authApi = new AuthApi(requireContext());

        binding.tilEmail.setOnClickListener(v->{
            Toast.makeText(requireContext(), "Email address can't be changed.", Toast.LENGTH_SHORT).show();
        });
        binding.tvForgotPassword.setOnClickListener(v->{
            AppUtils.openLinksInCustomChromeTabOrBrowser(requireActivity(), ApiConfig.SERVER_BASE_URL+"/forgot-password");
        });
        binding.btnDelete.setOnClickListener(v->{
            AppUtils.showMaterialDialog(requireContext(), "Delete Your Account","To ensure your data is handled securely, account deletion must be completed through your profile settings on our web app.", "Go to Web App", (dialog, which) -> {
                AppUtils.openLinksInCustomChromeTabOrBrowser(requireActivity(), ApiConfig.SERVER_BASE_URL+"/settings/profile");
            }, "Cancel", (dialog, which) -> {});
        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateProfile();
            }
        });

        binding.btnUpdate.setOnClickListener(v -> {

            String newNme = binding.tieName.getText().toString().trim();
            String oldPassword = binding.tiePassword.getText().toString().trim();
            String newPassword = binding.tieNewPassword.getText().toString().trim();
            String confirmNewPassword = binding.tieConfirmPassword.getText().toString().trim();

            if (newNme.isEmpty() || newNme.length() < 3 || newNme.length() > 255) {
                binding.tilName.setError("Please enter a valid name");
                return;
            }

            boolean isChangingPassword = !newPassword.isEmpty();

            if (isChangingPassword) {

                if (oldPassword.isEmpty()) {
                    binding.tilPassword.setError("Please enter your current password");
                    return;
                }

                if (newPassword.length() < 8) {
                    binding.tilNewPassword.setError("Password must be at least 8 characters");
                    return;
                }

                if (!newPassword.equals(confirmNewPassword)) {
                    binding.tilConfirmPassword.setError("Passwords do not match");
                    return;
                }
            }

            AppUtils.startLoading(requireContext(), "Updating profile...");
            authApi.updateUser(newNme, oldPassword, newPassword, new SafeCallback(requireContext()) {
                @Override
                public void onSuccess(String bodyFromResponse) {
                    JSONObject jsonObject = AppUtils.getJsonObjFromString(bodyFromResponse);

                    JSONObject user = AppUtils.getJsonObjOrNullFromJsonObj(jsonObject, "data");
                    String name = user.optString("name");

                    sessionManager.updateName(name);
                    Toast.makeText(requireContext(), "Profile Updated ! ", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateViews(){
        String name = sessionManager.getName();
        String email = sessionManager.getEmail();

        binding.tieName.setText(name);
        binding.tieEmail.setText(email);
    }

    private void updateProfile(){

        binding.swipeRefreshLayout.setRefreshing(true);
        authApi.getUser(new SafeCallback(requireContext()) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                binding.swipeRefreshLayout.setRefreshing(false);
                JSONObject user = AppUtils.getJsonObjFromString(bodyFromResponse);
                String name = AppUtils.getStringFromJsonObject(user, "name", null);
                sessionManager.updateName(name);
                updateViews();


            }

            @Override
            protected void onFailureHandled(Call call, IOException e) {
                super.onFailureHandled(call, e);
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

    }



}

