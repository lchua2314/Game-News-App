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
import com.onemanarmy.android.gamenewsapp.DetailActivity;
import com.onemanarmy.android.gamenewsapp.R;
import com.onemanarmy.android.gamenewsapp.models.Game;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    Context context;
    List<Game> games;

    public GameAdapter(Context context, List<Game> games) {
        this.context = context;
        this.games = games;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("GameAdapter", "onCreateViewHolder");
        View gameView = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new ViewHolder(gameView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("GameAdapter", "onBindViewHolder " + position);
        // Get the game at the passed in position
        Game game = games.get(position);
        // Bind the game data into the VH
        holder.bind(game);
    }

    // Return the total count of items in the list
    @Override
    public int getItemCount() {
        return games.size();
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

        public void bind(Game game) {
            tvTitle.setText(game.getTitle());
            tvDeck.setText(game.getDeck());
            tvAuthors.setText(game.getAuthors());
            tvPublishDate.setText(game.getFormattedPublishDate());

            String imageUrl;
            // If phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then imageUrl = back drop image
                imageUrl = game.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = game.getPosterPath();
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
            container.setOnClickListener((View.OnClickListener) v -> {
                // 2. Navigate to a new activity on tap
                Intent i  = new Intent(context, DetailActivity.class);
                i.putExtra("game", Parcels.wrap(game));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, (View)itemView, "games");
                context.startActivity(i, options.toBundle());
            });
        }
    }
}
