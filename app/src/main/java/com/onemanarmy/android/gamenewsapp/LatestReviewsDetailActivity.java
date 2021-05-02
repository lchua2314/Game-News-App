package com.onemanarmy.android.gamenewsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.onemanarmy.android.gamenewsapp.models.LatestReviews;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class LatestReviewsDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDeck, tvAuthors,
            tvPublishDate, tvBody, tvScore,
            tvGood, tvBad;
    Button btnSiteDetailUrl;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_latest_reviews);

        tvTitle = findViewById(R.id.tvTitle);
        tvDeck = findViewById(R.id.tvDeck);
        tvAuthors = findViewById(R.id.tvAuthors);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        ivPoster = findViewById(R.id.ivPoster);
        tvBody = findViewById(R.id.tvBody);
        btnSiteDetailUrl = findViewById(R.id.btnSiteDetailUrl);
        tvScore = findViewById(R.id.tvScore);
        tvGood = findViewById(R.id.tvGood);
        tvBad = findViewById(R.id.tvBad);

        LatestReviews latestReviews = Parcels.unwrap(getIntent().getParcelableExtra("latestReviews"));
        tvTitle.setText(latestReviews.getTitle());
        tvDeck.setText(latestReviews.getDeck());
        tvAuthors.setText(latestReviews.getAuthors());
        tvBody.setText(latestReviews.getBody());
        tvPublishDate.setText(latestReviews.getPublishDateToHumanReadable());
        tvScore.setText(latestReviews.getScore());
        tvGood.setText(latestReviews.getGood());
        tvBad.setText(latestReviews.getBad());

        btnSiteDetailUrl.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(latestReviews.getSiteDetailUrl()));
            startActivity(intent);
        });

        String imageUrl = latestReviews.getOriginalPosterPath();

        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop

        Glide.with(LatestReviewsDetailActivity.this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivPoster);
    }
}
