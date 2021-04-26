package com.onemanarmy.android.gamenewsapp;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    public static final String GAME_RELEASES = "http://www.gamespot.com/api/releases/?api_key=" + consumerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}