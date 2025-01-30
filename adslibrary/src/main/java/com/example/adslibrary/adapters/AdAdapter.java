package com.example.adslibrary.adapters;

import com.example.adslibrary.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.adslibrary.models.Ad;
import com.example.adslibrary.models.ClickRequest;
import com.example.adslibrary.network.RetrofitClient;
import com.example.adslibrary.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
    private Context context;
    private List<Ad> adList;

    public AdAdapter(Context context, List<Ad> adList) {
        this.context = context;
        this.adList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ad_item, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        Ad ad = adList.get(position);
        holder.title.setText(ad.getTitle());
        holder.description.setText(ad.getDescription());

        Glide.with(context).load(ad.getImageUrl()).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            // דיווח לשרת על קליק
            ApiService apiService = RetrofitClient.getClient();
            apiService.updateClickCount(new ClickRequest(ad.getTitle())).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    // אין צורך בתגובה, רק דיווח
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            // פתיחת הפרסומת בדפדפן
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ad.getClickUrl()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView image;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.adTitle);
            description = itemView.findViewById(R.id.adDescription);
            image = itemView.findViewById(R.id.adImage);
        }
    }
}
