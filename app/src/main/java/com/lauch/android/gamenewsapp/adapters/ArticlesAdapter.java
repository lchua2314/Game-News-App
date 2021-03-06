package com.lauch.android.gamenewsapp.adapters;

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
import com.lauch.android.gamenewsapp.ArticlesDetailActivity;
import com.lauch.android.gamenewsapp.R;
import com.lauch.android.gamenewsapp.models.Articles;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    Context context;
    List<Articles> articles;

    public ArticlesAdapter(Context context, List<Articles> articles) {
        this.context = context;
        this.articles = articles;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("ArticlesAdapter", "onCreateViewHolder");
        View articlesView = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(articlesView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ArticlesAdapter", "onBindViewHolder " + position);
        // Get the game at the passed in position
        Articles articles = this.articles.get(position);
        // Bind the game data into the VH
        holder.bind(articles);
    }

    // Clean all elements of the recycler
    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }

    // Return the total count of items in the list
    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        TextView tvTitle, tvDeck, tvAuthors, tvPublishDate;
        ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDeck = itemView.findViewById(R.id.tvDeck);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvAuthors = itemView.findViewById(R.id.tvAuthors);
            tvPublishDate = itemView.findViewById(R.id.tvPublishDate);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(Articles articles) {
            tvTitle.setText(articles.getTitle());
            tvDeck.setText(articles.getDeck());
            tvAuthors.setText(articles.getAuthors());
            tvPublishDate.setText(articles.getPublishDateTimeFromNow());

            String imageUrl;
            // If phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = articles.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = articles.getPosterPath();
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
                Intent i  = new Intent(context, ArticlesDetailActivity.class);
                i.putExtra("article", Parcels.wrap(articles));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, itemView, "article");
                context.startActivity(i, options.toBundle());
            });
        }
    }
}
