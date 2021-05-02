package com.onemanarmy.android.gamenewsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.onemanarmy.android.gamenewsapp.models.Articles;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ArticleDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDeck, tvAuthors, tvPublishDate, tvBody;
    Button btnSiteDetailUrl;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_article);

        tvTitle = findViewById(R.id.tvTitle);
        tvDeck = findViewById(R.id.tvDeck);
        tvAuthors = findViewById(R.id.tvAuthors);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        ivPoster = findViewById(R.id.ivPoster);
        tvBody = findViewById(R.id.tvBody);
        btnSiteDetailUrl = findViewById(R.id.btnSiteDetailUrl);

        Articles articles = Parcels.unwrap(getIntent().getParcelableExtra("article"));
        tvTitle.setText(articles.getTitle());
        tvDeck.setText(articles.getDeck());
        tvAuthors.setText(articles.getAuthors());
        tvBody.setText(articles.getBody());
        tvPublishDate.setText(articles.getPublishDateToHumanReadable());
        btnSiteDetailUrl.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(articles.getSiteDetailUrl()));
            startActivity(intent);
        });

        String imageUrl = articles.getOriginalPosterPath();

        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop

        Glide.with(ArticleDetailActivity.this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivPoster);
    }
}
