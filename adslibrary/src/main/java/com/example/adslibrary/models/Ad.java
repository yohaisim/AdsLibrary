package com.example.adslibrary.models;

import com.google.gson.annotations.SerializedName;

public class Ad {
    private String title;
    private String description;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("click_url")
    private String clickUrl;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getClickUrl() {
        return clickUrl;
    }
}
