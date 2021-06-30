package com.lauch.android.gamenewsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lauch.android.gamenewsapp.models.Reviews;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ReviewsDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDeck, tvAuthors,
            tvPublishDate, tvUpdateDate,
            tvBody, tvScore, tvGood, tvBad;
    Button btnSiteDetailUrl;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reviews);

        tvTitle = findViewById(R.id.tvTitle);
        tvDeck = findViewById(R.id.tvDeck);
        tvAuthors = findViewById(R.id.tvAuthors);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        tvUpdateDate = findViewById(R.id.tvUpdateDate);
        ivPoster = findViewById(R.id.ivPoster);
        tvBody = findViewById(R.id.tvBody);
        btnSiteDetailUrl = findViewById(R.id.btnSiteDetailUrl);
        tvScore = findViewById(R.id.tvScore);
        tvGood = findViewById(R.id.tvGood);
        tvBad = findViewById(R.id.tvBad);

        Reviews reviews = Parcels.unwrap(getIntent().getParcelableExtra("reviews"));
        tvTitle.setText(reviews.getTitle());
        tvDeck.setText(reviews.getDeck());
        tvAuthors.setText(reviews.getAuthors());
        tvBody.setText(reviews.getBody());
        tvPublishDate.setText(reviews.getPublishDateToHumanReadable());
        tvUpdateDate.setText(reviews.getUpdateDateToHumanReadable());

        String score = reviews.getScore();

        if (score.matches("10.0")) tvScore.setText("10");
        else tvScore.setText(score);

        tvGood.setText(reviews.getGood());
        tvBad.setText(reviews.getBad());

        btnSiteDetailUrl.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(reviews.getSiteDetailUrl()));
            startActivity(intent);
        });

        String imageUrl = reviews.getOriginalPosterPath();

        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop

        Glide.with(ReviewsDetailActivity.this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivPoster);
    }
}
