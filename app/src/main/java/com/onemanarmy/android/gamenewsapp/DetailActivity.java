package com.onemanarmy.android.gamenewsapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.onemanarmy.android.gamenewsapp.models.Game;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDeck, tvAuthors, tvPublishDate, tvBody;
    ImageView tvPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvTitle);
        tvDeck = findViewById(R.id.tvDeck);
        tvAuthors = findViewById(R.id.tvAuthors);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        tvPoster = findViewById(R.id.tvPoster);
        tvBody = findViewById(R.id.tvBody);

        Game game = Parcels.unwrap(getIntent().getParcelableExtra("game"));
        tvTitle.setText(game.getTitle());
        tvDeck.setText(game.getDeck());
        tvAuthors.setText(game.getAuthors());
        tvBody.setText(game.getBody());
        tvPublishDate.setText(game.getFormattedPublishDate());

        String imageUrl = game.getOriginalPosterPath();

        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop

        Glide.with(DetailActivity.this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(tvPoster);
    }
}
