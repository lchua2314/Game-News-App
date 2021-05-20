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
import com.onemanarmy.android.gamenewsapp.TopReviewsDetailActivity;
import com.onemanarmy.android.gamenewsapp.R;
import com.onemanarmy.android.gamenewsapp.models.TopReviews;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TopReviewsAdapter extends RecyclerView.Adapter<TopReviewsAdapter.ViewHolder> {

    Context context;
    List<TopReviews> topReviews;

    public TopReviewsAdapter(Context context, List<TopReviews> topReviews) {
        this.context = context;
        this.topReviews = topReviews;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("TopReviewsAdapter", "onCreateViewHolder");
        View topReviewsView = LayoutInflater.from(context).inflate(R.layout.item_top_review, parent, false);
        return new ViewHolder(topReviewsView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("TopReviewsAdapter", "onBindViewHolder " + position);
        // Get the game at the passed in position
        TopReviews topReviews = this.topReviews.get(position);
        // Bind the game data into the VH
        holder.bind(topReviews);
    }

    // Clean all elements of the recycler
    public void clear() {
        topReviews.clear();
        notifyDataSetChanged();
    }

    // Return the total count of items in the list
    @Override
    public int getItemCount() {
        return topReviews.size();
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

        public void bind(TopReviews topReviews) {
            tvTitle.setText(topReviews.getTitle());
            tvDeck.setText(topReviews.getDeck());
            tvAuthors.setText(topReviews.getAuthors());
            tvUpdateDate.setText(topReviews.getUpdateDateTimeFromNow());

            String score = topReviews.getScore();

            if (score.matches("10.0")) tvScore.setText("10");
            else tvScore.setText(score);


            String imageUrl;
            // If phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = topReviews.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = topReviews.getPosterPath();
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
                Intent i  = new Intent(context, TopReviewsDetailActivity.class);
                i.putExtra("topReviews", Parcels.wrap(topReviews));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, itemView, "topReviews");
                context.startActivity(i, options.toBundle());
            });
        }
    }
}
