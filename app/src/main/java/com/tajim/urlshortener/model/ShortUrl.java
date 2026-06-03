package com.tajim.urlshortener.model;

public class ShortUrl {
    public long id;
    public String longUrl;
    public String shortCode;
    public long clicks;
    public boolean hasPassword;
    public ShortUrl(long id, String longUrl, String shortCode, long clicks, boolean hasPassword) {
        this.id = id;
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.clicks = clicks;
        this.hasPassword = hasPassword;
    }
}
