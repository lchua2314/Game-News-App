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
    String backdropPath;
    String posterPath;
    String title;
    String deck;

    // empty constructor needed by the Parceler library
    public Game() {}

    public Game(JSONObject jsonObject) throws JSONException {
        JSONObject image = jsonObject.getJSONObject("image");
        backdropPath = image.getString("square_small");
        posterPath = image.getString("square_tiny");

        title = jsonObject.getString("title");
        deck = jsonObject.getString("deck");
        gameId = jsonObject.getInt("id");
    }

    public static List<Game> fromJsonArray(JSONArray gameJsonArray) throws JSONException {
        List<Game> games = new ArrayList<>();
        for (int i = 0; i < gameJsonArray.length(); i++) {
            games.add(new Game(gameJsonArray.getJSONObject(i)));
        }
        return games;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public String getDeck() {
        return deck;
    }

    public int getGameId() { return gameId; }
}
