package com.example.adslibrary.network;

import com.example.adslibrary.models.Ad;
import com.example.adslibrary.models.ClickRequest;
import com.example.adslibrary.models.ViewRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("ads")
    Call<List<Ad>> getAds();
    Call<Void> reportView(@Body ViewRequest viewRequest);
    @POST("ads/click")
    Call<Void> updateClickCount(@Body ClickRequest clickRequest);

    @POST("/ads/view")
    Call<Void> updateViewCount(@Body ViewRequest viewRequest);

}
