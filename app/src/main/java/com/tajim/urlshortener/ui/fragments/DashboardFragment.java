package com.tajim.urlshortener.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajim.urlshortener.R;
import com.tajim.urlshortener.adapter.UrlAdapter;
import com.tajim.urlshortener.api.SafeCallback;
import com.tajim.urlshortener.api.UrlApi;
import com.tajim.urlshortener.databinding.FragmentDashboardBinding;
import com.tajim.urlshortener.model.ShortUrl;
import com.tajim.urlshortener.ui.activities.CreateUrlActivity;
import com.tajim.urlshortener.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    UrlApi urlApi;
    List<ShortUrl> shortUrls;
    private UrlAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        initVariables();
        fetchUrls();
        initListeners();

        return binding.getRoot();

    }
    private void initVariables(){
        urlApi = new UrlApi(requireContext());
        shortUrls = new ArrayList<>();
        adapter = new UrlAdapter(shortUrls);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));



    }
    private void fetchUrls(){
        urlApi.getAllUrls(new SafeCallback(requireContext()) {
            @Override
            public void onSuccess(String bodyFromResponse) {
                binding.swipeRefreshLayout.setRefreshing(false);
                AppUtils.logD("DashboardFragment", bodyFromResponse);
                List<ShortUrl> tempList = new ArrayList<>();

                new Thread(()->{
                    JSONObject jsonObject = AppUtils.getJsonObjFromString(bodyFromResponse);
                    JSONArray jsonArray = AppUtils.getJsonArrayOrNullFromJsonObj(jsonObject, "data");
                    if (jsonArray == null) return;

                    for (int i = 0; i < jsonArray.length(); i ++){
                        JSONObject shortUrl = jsonArray.optJSONObject(i);
                        if (shortUrl == null) continue;

                        String longUrl = AppUtils.getStringFromJsonObject(shortUrl, "long_url", null);
                        String shortCode = AppUtils.getStringFromJsonObject(shortUrl, "short_code", null);
                        long clicks = Long.parseLong(AppUtils.getStringFromJsonObject(shortUrl, "clicks", "0"));
                        long id = Long.parseLong(AppUtils.getStringFromJsonObject(shortUrl, "id", "0"));
                        boolean hasPassword = shortUrl.optBoolean("has_password", false);

                        tempList.add(new ShortUrl(id, longUrl, shortCode, clicks, hasPassword));





                    }

                    AppUtils.postUI(()->{
                        shortUrls.clear();
                        shortUrls.addAll(tempList);
                        adapter.notifyDataSetChanged();
                        AppUtils.logD("DashboardFragment", shortUrls.toString());
                        binding.tvClicks.setText(AppUtils.getStringFromJsonObject(jsonObject,"total_clicks","0"));
                        binding.tvLinks.setText(AppUtils.getStringFromJsonObject(jsonObject,"total_urls","0"));
                    });

                }).start();


            }

            @Override
            protected void onFailureHandled(Call call, IOException e) {
                super.onFailureHandled(call, e);
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void initListeners(){
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUrls();
            }
        });

        binding.btnAdd.setOnClickListener(v->{
            startActivity(new Intent(requireContext(), CreateUrlActivity.class));
        });
    }
}