package com.onemanarmy.android.gamenewsapp;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onemanarmy.android.gamenewsapp.adapters.GameAdapter;
import com.onemanarmy.android.gamenewsapp.models.Game;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains news articles in JSON, limited to 20 articles, and sorted by latest date
    public static final String GAME_NEWS_URL = "http://www.gamespot.com/api/articles/?api_key="
            + consumerKey + "&format=json&limit=20&sort=publish_date:desc";
    public static final String TAG = "MainActivity";

    List<Game> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rvGames = findViewById(R.id.rvGames);
        games = new ArrayList<>();

        // Create the adapter
        final GameAdapter gameAdapter = new GameAdapter(this, games);

        // Set the adapter on the recycler view
        rvGames.setAdapter(gameAdapter);

        // Set a Layout Manager on the recycler view
        rvGames.setLayoutManager(new LinearLayoutManager(this));
    }
}