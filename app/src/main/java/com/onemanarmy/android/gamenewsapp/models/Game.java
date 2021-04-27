package com.onemanarmy.android.gamenewsapp.models;

import android.os.Build;
import android.text.Html;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Parcel
public class Game {

    String backdropPath, posterPath, originalPosterPath,
            title, deck, authors, publishDate, body, siteDetailUrl;

    // empty constructor needed by the Parceler library
    public Game() {}

    public Game(JSONObject jsonObject) throws JSONException {
        JSONObject image = jsonObject.getJSONObject("image");
        backdropPath = image.getString("square_small");
        posterPath = image.getString("square_tiny");
        originalPosterPath = image.getString("original");

        title = jsonObject.getString("title");
        deck = jsonObject.getString("deck");
        authors = jsonObject.getString("authors");
        publishDate = jsonObject.getString("publish_date");

        body = jsonObject.getString("body");

        // Convert from HTML
        // https://stackoverflow.com/questions/2116162/how-to-display-html-in-textview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            body = Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            body = Html.fromHtml(body).toString();
        }

        siteDetailUrl = jsonObject.getString("site_detail_url");
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

    public String getOriginalPosterPath() {
        return originalPosterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getDeck() {
        return deck;
    }

    public String getAuthors() { return authors; }

    public String getFormattedPublishDate() {
        return formatDate(publishDate);
    }

    /**
     * Format date to from API to time spent ago.
     * Ex: "19 hours ago"
     * @param date String of API date
     * @return     String of new date format
     */
    private String formatDate(String date) {
        // https://stackoverflow.com/questions/35858608/how-to-convert-time-to-time-ago-in-android
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Original Format: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String getBody() { return body; }

    public String getSiteDetailUrl() { return siteDetailUrl; }
}
