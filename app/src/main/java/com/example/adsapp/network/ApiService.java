package com.example.adsapp.network;

import com.example.adsapp.models.ViewRequest;
import com.example.adsapp.models.Ad;
import com.example.adsapp.models.ClickRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("ads")
    Call<List<Ad>> getAds();
    Call<Void> reportView(@Body ViewRequest viewRequest);
    @POST("/ads/click")
    Call<Void> updateClickCount(@Body ClickRequest clickRequest);
    @POST("/ads/view")
    Call<Void> updateViewCount(@Body ViewRequest viewRequest);

}
