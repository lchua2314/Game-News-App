package com.onemanarmy.android.gamenewsapp.viewmodels;

import androidx.lifecycle.ViewModel;

import com.onemanarmy.android.gamenewsapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArticlesFragmentViewModel extends ViewModel {

    private JSONArray savedResults;
    private int offset = 20;
    private int scrollPosition = 0;

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains news articles in JSON, limited to 20 articles, and sorted by latest date
    public static final String GAME_NEWS_URL = "https://www.gamespot.com/api/articles/?api_key="
            + consumerKey + "&format=json&limit=20&sort=publish_date:desc";
    private String currentUrl = GAME_NEWS_URL;
    private String lastFilter = "";

    public String getLastFilter() { return lastFilter; }

    public void setLastFilter(String lastFilter) { this.lastFilter = lastFilter; }

    public void setFilterOnCurrentUrl(String filter) {
        this.currentUrl = GAME_NEWS_URL + "&filter=title:" + filter;
        setLastFilter(filter);
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    public int getScrollPosition() {
        return scrollPosition;
    }

    public void updateOffset() {
        offset += 20;
    }

    public void resetOffset() {
        offset = 20;
    }

    public int getOffset() {
        return offset;
    }

    public JSONArray getResults() {
        // TODO: Move model data out of controller logic
        if (savedResults == null) {
        }
        return savedResults;
    }

    public void saveResults(JSONArray results) {

        if (savedResults == null) {
            savedResults = results;
            return;
        }

        try {
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonObject = results.getJSONObject(i);
                savedResults.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resetResults() {
        savedResults = new JSONArray(new ArrayList<String>());
    }
}
