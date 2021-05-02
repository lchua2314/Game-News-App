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
import com.onemanarmy.android.gamenewsapp.ArticleDetailActivity;
import com.onemanarmy.android.gamenewsapp.LatestReviewsDetailActivity;
import com.onemanarmy.android.gamenewsapp.R;
import com.onemanarmy.android.gamenewsapp.models.LatestReviews;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class LatestReviewsAdapter extends RecyclerView.Adapter<LatestReviewsAdapter.ViewHolder> {

    Context context;
    List<LatestReviews> latestReviews;

    public LatestReviewsAdapter(Context context, List<LatestReviews> latestReviews) {
        this.context = context;
        this.latestReviews = latestReviews;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("LatestReviewsAdapter", "onCreateViewHolder");
        View latestReviewsView = LayoutInflater.from(context).inflate(R.layout.item_latest_review, parent, false);
        return new ViewHolder(latestReviewsView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("LatestReviewsAdapter", "onBindViewHolder " + position);
        // Get the game at the passed in position
        LatestReviews latestReviews = this.latestReviews.get(position);
        // Bind the game data into the VH
        holder.bind(latestReviews);
    }

    // Return the total count of items in the list
    @Override
    public int getItemCount() {
        return latestReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        TextView tvTitle, tvDeck, tvAuthors, tvUpdateDate, tvScore;
        ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDeck = itemView.findViewById(R.id.tvDeck);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvAuthors = itemView.findViewById(R.id.tvAuthors);
            tvUpdateDate = itemView.findViewById(R.id.tvUpdateDate);
            container = itemView.findViewById(R.id.container);
            tvScore = itemView.findViewById(R.id.tvScore);
        }

        public void bind(LatestReviews latestReviews) {
            tvTitle.setText(latestReviews.getTitle());
            tvDeck.setText(latestReviews.getDeck());
            tvAuthors.setText(latestReviews.getAuthors());
            tvUpdateDate.setText(latestReviews.getUpdateDateTimeFromNow());

            String score = latestReviews.getScore();

            if (score.matches("10.0")) tvScore.setText("10");
            else tvScore.setText(score);


            String imageUrl;
            // If phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = latestReviews.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = latestReviews.getPosterPath();
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
                Intent i  = new Intent(context, LatestReviewsDetailActivity.class);
                i.putExtra("latestReviews", Parcels.wrap(latestReviews));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, itemView, "latestReviews");
                context.startActivity(i, options.toBundle());
            });
        }
    }
}
