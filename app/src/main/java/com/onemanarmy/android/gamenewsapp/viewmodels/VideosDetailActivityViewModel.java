package com.onemanarmy.android.gamenewsapp.viewmodels;

import androidx.lifecycle.ViewModel;

public class VideosDetailActivityViewModel extends ViewModel {

    private String videoQuality = "high_url";

    public String getVideoQuality() { return videoQuality; }

    public void setVideoQuality(String videoQuality) { this.videoQuality = videoQuality; }
}
