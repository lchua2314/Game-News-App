package com.onemanarmy.android.gamenewsapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Game {

    int gameId;
    // String backdropPath;
    // String posterPath;
    String title;
    String deck;
    // double rating;

    // empty constructor needed by the Parceler library
    public Game() {}

    public Game(JSONObject jsonObject) throws JSONException {
        // backdropPath = jsonObject.getString("backdrop_path");
        // posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        deck = jsonObject.getString("deck");
        // rating = jsonObject.getDouble("vote_average");
        gameId = jsonObject.getInt("id");
    }

    public static List<Game> fromJsonArray(JSONArray gameJsonArray) throws JSONException {
        List<Game> games = new ArrayList<>();
        for (int i = 0; i < gameJsonArray.length(); i++) {
            games.add(new Game(gameJsonArray.getJSONObject(i)));
        }
        return games;
    }

    /*
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }
     */

    public String getTitle() {
        return title;
    }

    public String getDeck() {
        return deck;
    }

    // public double getRating() { return rating; }

    public int getGameId() { return gameId; }
}
