package com.onemanarmy.android.gamenewsapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.onemanarmy.android.gamenewsapp.models.Videos;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VideosDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvDeck, tvPublishDate;
    Button btnSiteDetailUrl;
    VideoView videoView;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_videos);

        tvTitle = findViewById(R.id.tvTitle);
        tvDeck = findViewById(R.id.tvDeck);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        ivPoster = findViewById(R.id.ivPoster);
        btnSiteDetailUrl = findViewById(R.id.btnSiteDetailUrl);

        Videos videos = Parcels.unwrap(getIntent().getParcelableExtra("video"));
        tvTitle.setText(videos.getTitle());
        tvDeck.setText(videos.getDeck());
        tvPublishDate.setText(videos.getPublishDateToHumanReadable());

        videoView = findViewById(R.id.vvVideo);

        Uri video = Uri.parse(videos.getVideo());
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        btnSiteDetailUrl.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(videos.getSiteDetailUrl()));
            startActivity(intent);
        });

        String imageUrl = videos.getOriginalPosterPath();

        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop

        Glide.with(VideosDetailActivity.this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivPoster);
    }
}
