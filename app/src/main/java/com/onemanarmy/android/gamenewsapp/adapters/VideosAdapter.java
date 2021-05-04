package com.onemanarmy.android.gamenewsapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.onemanarmy.android.gamenewsapp.R;
import com.onemanarmy.android.gamenewsapp.VideosDetailActivity;
import com.onemanarmy.android.gamenewsapp.models.Videos;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    Context context;
    List<Videos> videos;

    public VideosAdapter(Context context, List<Videos> videos) {
        this.context = context;
        this.videos = videos;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("VideosAdapter", "onCreateViewHolder");
        View videosView = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(videosView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("VideosAdapter", "onBindViewHolder " + position);
        // Get the game at the passed in position
        Videos videos = this.videos.get(position);
        // Bind the game data into the VH
        holder.bind(videos);
    }

    // Return the total count of items in the list
    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        TextView tvTitle, tvDeck, tvPublishDate;
        ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDeck = itemView.findViewById(R.id.tvDeck);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvPublishDate = itemView.findViewById(R.id.tvPublishDate);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(Videos videos) {
            tvTitle.setText(videos.getTitle());
            tvDeck.setText(videos.getDeck());
            tvPublishDate.setText(videos.getPublishDateTimeFromNow());

            String imageUrl;
            // If phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = videos.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = videos.getPosterPath();
            }

            int radius = 30; // corner radius, higher value = more rounded
            int margin = 10; // crop margin, set to 0 for corners with no crop

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.imagenotfound)
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(ivPoster);

            // 1. Register click listener on the whole row
            container.setOnClickListener(v -> {
                // 2. Navigate to a new activity on tap
                Intent i  = new Intent(context, VideosDetailActivity.class);
                i.putExtra("video", Parcels.wrap(videos));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, itemView, "video");
                context.startActivity(i, options.toBundle());
            });
        }
    }
}
