package com.example.adsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.adsapp.models.Ad;
import com.example.adsapp.models.ClickRequest;
import com.example.adsapp.models.ViewRequest;
import com.example.adsapp.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView adImage;
    private TextView adTitle, adDescription, adTimer;
    private List<Ad> adList;
    private Handler handler = new Handler();
    private int currentIndex = 0; // Keeps track of the current ad index
    private String currentClickUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adImage = findViewById(R.id.adImage);
        adTitle = findViewById(R.id.adTitle);
        adDescription = findViewById(R.id.adDescription);
        adTimer = findViewById(R.id.adTimer);

        // Open ad URL on image click
        adImage.setOnClickListener(v -> {
            if (!currentClickUrl.isEmpty()) {
                registerClick(adTitle.getText().toString());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentClickUrl));
                startActivity(browserIntent);
            }
        });


        fetchAds();
    }

    private void fetchAds() {
        RetrofitClient.getClient().getAds().enqueue(new Callback<List<Ad>>() {
            @Override
            public void onResponse(Call<List<Ad>> call, Response<List<Ad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adList = response.body();
                    startAdRotation();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load ads", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ad>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching ads", t);
                Toast.makeText(MainActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAdRotation() {
        if (adList == null || adList.isEmpty()) {
            return; // Avoid running if there are no ads
        }

        // Display the first ad immediately instead of waiting 10 seconds
        updateAd();

        handler.postDelayed(new Runnable() {
            int secondsRemaining = 10;

            @Override
            public void run() {
                if (secondsRemaining > 0) {
                    adTimer.setText(secondsRemaining + "s");
                    secondsRemaining--;
                    handler.postDelayed(this, 1000);
                } else {
                    updateAd(); // Show the next ad
                    secondsRemaining = 10; // Reset timer
                    handler.postDelayed(this, 1000);
                }
            }
        }, 0);
    }

    private void updateAd() {
        if (adList != null && !adList.isEmpty()) {
            // Get the next ad in sequence
            Ad ad = adList.get(currentIndex);

            // Register view count
            registerView(ad.getTitle());

            // Fade out animation before changing ad
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            fadeOut.setFillAfter(true);
            adImage.startAnimation(fadeOut);
            adTitle.startAnimation(fadeOut);
            adDescription.startAnimation(fadeOut);

            handler.postDelayed(() -> {
                // Update UI with new ad content
                adTitle.setText(ad.getTitle());
                adDescription.setText(ad.getDescription());
                Glide.with(MainActivity.this).load(ad.getImageUrl()).into(adImage);
                currentClickUrl = ad.getClickUrl();

                // Fade in animation for the new ad
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
                fadeIn.setDuration(500);
                fadeIn.setFillAfter(true);
                adImage.startAnimation(fadeIn);
                adTitle.startAnimation(fadeIn);
                adDescription.startAnimation(fadeIn);

            }, 500); // Delay before content update

            // Move to the next ad (loop back if at the end)
            currentIndex = (currentIndex + 1) % adList.size();
        }
    }


    private void registerView(String adTitle) {
        RetrofitClient.getClient().updateViewCount(new ViewRequest(adTitle))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("VIEWS", "View count updated for: " + adTitle);
                        } else {
                            Log.e("VIEWS", "Failed to update view count");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("VIEWS", "Error updating view count", t);
                    }
                });
    }

    private void registerClick(String adTitle) {
        RetrofitClient.getClient().updateClickCount(new ClickRequest(adTitle)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CLICK", "Click registered for: " + adTitle);
                } else {
                    Log.e("CLICK", "Failed to register click");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CLICK", "Error: " + t.getMessage());
            }
        });
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
