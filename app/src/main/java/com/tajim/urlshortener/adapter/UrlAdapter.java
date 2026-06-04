package com.tajim.urlshortener.adapter;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tajim.urlshortener.api.ApiConfig;
import com.tajim.urlshortener.databinding.ItemUrlBinding;
import com.tajim.urlshortener.model.ShortUrl;
import com.tajim.urlshortener.ui.activities.ViewUrlActivity;
import com.tajim.urlshortener.utils.AppUtils;

import java.util.List;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.ViewHolder> {
    List<ShortUrl> shortUrls;
    public UrlAdapter(List<ShortUrl> shortUrls){
        this.shortUrls = shortUrls;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUrlBinding binding = ItemUrlBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShortUrl shortUrl =shortUrls.get(position);
        String shortLink = ApiConfig.SERVER_BASE_URL+"/"+shortUrl.shortCode;
        holder.binding.tvShortUrl.setText(shortLink);
        holder.binding.tvLongUrl.setText(shortUrl.longUrl);
        holder.binding.tvView.setText(""+shortUrl.clicks);
        if(shortUrl.hasPassword) {
            AppUtils.logD("UrlAdapter", "onBindViewHolder: "+shortUrl.hasPassword);
            holder.binding.tvLocked.setVisibility(VISIBLE);
        }
        else holder.binding.tvLocked.setVisibility(GONE);

        holder.binding.btnCopy.setOnClickListener(v->{
            AppUtils.copyToClipBoard(holder.binding.getRoot().getContext(), shortLink);
        });


        holder.binding.getRoot().setOnClickListener(v->{
            AppUtils.copyToClipBoard(holder.binding.getRoot().getContext(), shortLink);

//            holder.binding.getRoot().getContext().startActivity(new Intent(holder.binding.getRoot().getContext(), ViewUrlActivity.class));
        });


    }

    @Override
    public int getItemCount() {
        return shortUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemUrlBinding binding;
        public ViewHolder(ItemUrlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
