package com.lauch.android.gamenewsapp.models;

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
import java.util.Date;
import java.util.List;

@Parcel
public class Articles {

    String backdropPath, posterPath, originalPosterPath,
            title, deck, authors, publishDate, body, siteDetailUrl;

    // empty constructor needed by the Parceler library
    public Articles() {}

    public Articles(JSONObject jsonObject) throws JSONException {
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

    public static List<Articles> fromJsonArray(JSONArray gameJsonArray) throws JSONException {
        List<Articles> articles = new ArrayList<>();
        for (int i = 0; i < gameJsonArray.length(); i++) {
            articles.add(new Articles(gameJsonArray.getJSONObject(i)));
        }
        return articles;
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

    public String getPublishDateTimeFromNow() {
        return formatDateToTimeFromNow(publishDate);
    }

    /**
     * Format date to from API to time spent ago.
     * Ex: "19 hours ago"
     * @param unformattedDate String of API date
     * @return     String of new date format
     */
    private String formatDateToTimeFromNow(String unformattedDate) {
        // https://stackoverflow.com/questions/35858608/how-to-convert-time-to-time-ago-in-android
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Original Format: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        // sdf.setTimeZone(TimeZone.getTimeZone("GWT")); // Commented out because it messes up the timezone +7 hours.
        SimpleDateFormat sdf2 = new SimpleDateFormat("E, MM-dd-yyyy, h:mm a z");
        try {
            long time = sdf.parse(unformattedDate).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unformattedDate;
    }

    public String getPublishDateToHumanReadable() {
        return formatDateToHumanReadable(publishDate);
    }

    /**
     * Format date to from API to human readable date.
     * Ex: Wed, Jul 28, 2019, 11:38 AM PDT
     * @param unformattedDate String of API date
     * @return     String of new date format
     */
    private String formatDateToHumanReadable(String unformattedDate) {
        SimpleDateFormat oldSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // https://stackoverflow.com/questions/1468444/human-readable-and-parsable-date-format-in-java
        // https://stackoverflow.com/questions/12781273/what-are-the-date-formats-available-in-simpledateformat-class
        SimpleDateFormat newSdf = new SimpleDateFormat("E, MMM d, yyyy, h:mm a z");
        try {
            long time = oldSdf.parse(unformattedDate).getTime();
            Date dt = new Date(time);
            String newDate = newSdf.format(dt);
            return newDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unformattedDate;
    }

    public String getBody() { return body; }

    public String getSiteDetailUrl() { return siteDetailUrl; }
}
