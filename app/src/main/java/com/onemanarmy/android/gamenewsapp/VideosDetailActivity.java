package com.onemanarmy.android.gamenewsapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.onemanarmy.android.gamenewsapp.models.Videos;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VideosDetailActivity extends AppCompatActivity {

    private static final String TAG = "VideosDetailActivity";
    TextView tvTitle, tvDeck, tvPublishDate;
    Button btnSiteDetailUrl;
    VideoView videoView;
    ImageView ivPoster;
    MediaController mediaControls;
    private int videoPosition = 0;

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

        // Video
        // set the media controller buttons
        if (mediaControls == null)
        {
            mediaControls = new MediaController(VideosDetailActivity.this);
        }

        videoView = findViewById(R.id.vvVideo);

        try
        {
            // set the media controller in the VideoView
            videoView.setMediaController(mediaControls);

            // set the uri of the video to be played
            Uri video = Uri.parse(videos.getVideo());
            videoView.setVideoURI(video);

        } catch (Exception e)
        {
            Log.e("Video Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();

        // we also set an setOnPreparedListener in order to know when the video
        // file is ready for playback

        videoView.setOnPreparedListener(mediaPlayer -> {
            // if we have a position on savedInstanceState, the video
            // playback should start from here
            videoView.seekTo(videoPosition);

            Log.i(TAG, "video is ready for playing");

            if (videoPosition == 0)
            {
                videoView.start();
            } else
            {
                // if we come from a resumed activity, video playback will
                // be paused
                videoView.pause();
            }
            mediaControls.setAnchorView(videoView);
        });

        // Hide media controls when scrolling
        View scrollView = findViewById(R.id.svDetailVideos);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                mediaControls.hide();
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
