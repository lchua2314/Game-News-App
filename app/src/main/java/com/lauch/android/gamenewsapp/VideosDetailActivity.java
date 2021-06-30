package com.lauch.android.gamenewsapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lauch.android.gamenewsapp.models.Videos;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class VideosDetailActivity extends AppCompatActivity {

    private static final String TAG = "VideosDetailActivity";
    TextView tvTitle, tvDeck, tvPublishDate;
    Button btnSiteDetailUrl;
    VideoView videoView;
    ImageView ivPoster;
    MediaController mediaControls;
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

        videos = Parcels.unwrap(getIntent().getParcelableExtra("video"));
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
            setInitialVideoQuality();

        } catch (Exception e)
        {
            Log.e("Video Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();

        // we also set an setOnPreparedListener in order to know when the video
        // file is ready for playback

        videoView.setOnPreparedListener(mediaPlayer -> {
            Log.i(TAG, "video is ready for playing");

            mediaControls.setAnchorView(videoView);
        });

        videoView.start();

        // Hide media controls when scrolling
        View scrollView = findViewById(R.id.svDetailVideos);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                mediaControls.hide();
        });

        // Spinner/Dropdown menu to change quality

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);

        //create a list of items for the spinner.
        String[] items = new String[]{"Low", "High", "HD"};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, items);

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setSelection(1); // default: high

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));

                Uri video;
                int progress = videoView.getCurrentPosition();
                Log.i(TAG, "Current Position: " + progress);

                switch (position) {
                    case 0: // low
                        video = Uri.parse(videos.getLowUrlVideo());
                        break;
                    case 1: // high
                        video = Uri.parse(videos.getHighUrlVideo());
                        break;
                    default: // HD
                        video = Uri.parse(videos.getHdUrlVideo());
                }

                videoView.setVideoURI(video);
                mediaControls.hide();
                videoView.setOnPreparedListener(mediaPlayer -> {
                    videoView.seekTo(progress);
                    mediaControls.setAnchorView(videoView);
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void setInitialVideoQuality() {
        Uri video = Uri.parse(videos.getHighUrlVideo());
        videoView.setVideoURI(video);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
