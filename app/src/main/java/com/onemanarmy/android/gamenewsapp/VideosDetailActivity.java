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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.onemanarmy.android.gamenewsapp.models.Videos;
import com.onemanarmy.android.gamenewsapp.viewmodels.VideosDetailActivityViewModel;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VideosDetailActivity extends AppCompatActivity {

    private static final String TAG = "VideosDetailActivity";
    TextView tvTitle, tvDeck, tvPublishDate;
    Button btnSiteDetailUrl, btnChangeVideoQuality;
    VideoView videoView;
    ImageView ivPoster;
    MediaController mediaControls;
    VideosDetailActivityViewModel videosDetailActivityViewModel;
    private int videoPosition = 0;
    Videos videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_videos);

        tvTitle = findViewById(R.id.tvTitle);
        tvDeck = findViewById(R.id.tvDeck);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        ivPoster = findViewById(R.id.ivPoster);
        btnSiteDetailUrl = findViewById(R.id.btnSiteDetailUrl);
        btnChangeVideoQuality = findViewById(R.id.btnChangeVideoQuality);

        videos = Parcels.unwrap(getIntent().getParcelableExtra("video"));
        tvTitle.setText(videos.getTitle());
        tvDeck.setText(videos.getDeck());
        tvPublishDate.setText(videos.getPublishDateToHumanReadable());

        videosDetailActivityViewModel = new ViewModelProvider(this).get(VideosDetailActivityViewModel.class);

        btnSiteDetailUrl.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(videos.getSiteDetailUrl()));
            startActivity(intent);
        });

        btnChangeVideoQuality.setOnClickListener(v -> {
            changeVideoQuality();
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
            setVideoQuality();

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

    private void setVideoQuality() {
        String quality = videosDetailActivityViewModel.getVideoQuality();
        String btnText = "Change video quality to ";
        Uri video;

        switch (quality) {
            case "high_url":
                video = Uri.parse(videos.getHighUrlVideo());
                btnChangeVideoQuality.setText(btnText + "HD");
                break;
            case "hd_url":
                video = Uri.parse(videos.getHdUrlVideo());
                btnChangeVideoQuality.setText(btnText + "low");
                break;
            default: // "low_url"
                video = Uri.parse(videos.getLowUrlVideo());
                btnChangeVideoQuality.setText(btnText + "high");
        }

        videoView.setVideoURI(video);
    }

    private void changeVideoQuality() {
        String quality = videosDetailActivityViewModel.getVideoQuality();
        String btnText = "Change video quality to ";
        Uri video;
        int progress = videoView.getCurrentPosition();
        Log.i(TAG, "Current Position: " + progress);

        switch (quality) {
            case "high_url":
                video = Uri.parse(videos.getHdUrlVideo());
                videosDetailActivityViewModel.setVideoQuality("hd_url");
                btnChangeVideoQuality.setText(btnText + "low");
                break;
            case "hd_url":
                video = Uri.parse(videos.getLowUrlVideo());
                videosDetailActivityViewModel.setVideoQuality("low_url");
                btnChangeVideoQuality.setText(btnText + "high");
                break;
            default: // "low_url"
                video = Uri.parse(videos.getHighUrlVideo());
                videosDetailActivityViewModel.setVideoQuality("high_url");
                btnChangeVideoQuality.setText(btnText + "HD");
        }

        videoView.setVideoURI(video);
        mediaControls.hide();
        videoView.setOnPreparedListener(mediaPlayer -> {
            videoView.seekTo(progress);
            mediaControls.setAnchorView(videoView);
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
