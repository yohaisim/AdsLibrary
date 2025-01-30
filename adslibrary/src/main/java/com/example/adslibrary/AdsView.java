package com.example.adslibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adslibrary.models.Ad;
import com.example.adslibrary.network.ApiService;
import com.example.adslibrary.network.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsView extends FrameLayout {
    private ImageView adImage;
    private TextView adTitle, adDescription;

    public AdsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ads_view, this, true);

        adImage = findViewById(R.id.ad_image);
        adTitle = findViewById(R.id.ad_title);
        adDescription = findViewById(R.id.ad_description);

        fetchAds();
    }

    private void fetchAds() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Ad>> call = apiService.getAds();

        call.enqueue(new Callback<List<Ad>>() {
            @Override
            public void onResponse(Call<List<Ad>> call, Response<List<Ad>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Ad ad = response.body().get(0);
                    adTitle.setText(ad.getTitle());
                    adDescription.setText(ad.getDescription());
                    Picasso.get().load(ad.getImageUrl()).into(adImage);
                }
            }

            @Override
            public void onFailure(Call<List<Ad>> call, Throwable t) {
                adTitle.setText("Failed to load ad.");
            }
        });
    }
}
