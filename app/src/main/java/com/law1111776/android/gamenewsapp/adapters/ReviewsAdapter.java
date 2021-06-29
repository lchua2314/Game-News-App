package com.law1111776.android.gamenewsapp.adapters;

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
import com.law1111776.android.gamenewsapp.ReviewsDetailActivity;
import com.law1111776.android.gamenewsapp.R;
import com.law1111776.android.gamenewsapp.models.Reviews;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    Context context;
    List<Reviews> reviews;

    public ReviewsAdapter(Context context, List<Reviews> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("ReviewsAdapter", "onCreateViewHolder");
        View reviewsView = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(reviewsView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ReviewsAdapter", "onBindViewHolder " + position);
        // Get the game at the passed in position
        Reviews reviews = this.reviews.get(position);
        // Bind the game data into the VH
        holder.bind(reviews);
    }

    // Clean all elements of the recycler
    public void clear() {
        reviews.clear();
        notifyDataSetChanged();
    }

    // Return the total count of items in the list
    @Override
    public int getItemCount() {
        return reviews.size();
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

        public void bind(Reviews reviews) {
            tvTitle.setText(reviews.getTitle());
            tvDeck.setText(reviews.getDeck());
            tvAuthors.setText(reviews.getAuthors());
            tvUpdateDate.setText(reviews.getUpdateDateTimeFromNow());

            String score = reviews.getScore();

            if (score.matches("10.0")) tvScore.setText("10 ");
            else tvScore.setText(score);

            String imageUrl;
            // If phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = reviews.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = reviews.getPosterPath();
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
                Intent i  = new Intent(context, ReviewsDetailActivity.class);
                i.putExtra("reviews", Parcels.wrap(reviews));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, itemView, "reviews");
                context.startActivity(i, options.toBundle());
            });
        }
    }
}
