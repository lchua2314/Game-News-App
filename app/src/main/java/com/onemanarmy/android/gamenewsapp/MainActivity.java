package com.onemanarmy.android.gamenewsapp;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.onemanarmy.android.gamenewsapp.adapters.GameAdapter;
import com.onemanarmy.android.gamenewsapp.models.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains news articles in JSON, limited to 20 articles, and sorted by latest date
    public static final String GAME_NEWS_URL = "https://www.gamespot.com/api/articles/?api_key="
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

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GAME_NEWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    games.addAll(Game.fromJsonArray(results));
                    gameAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Games: " + games.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure " + statusCode);
            }
        });
    }
}