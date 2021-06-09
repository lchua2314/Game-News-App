package com.onemanarmy.android.gamenewsapp.viewmodels;

import androidx.lifecycle.ViewModel;

import com.onemanarmy.android.gamenewsapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewsFragmentViewModel extends ViewModel {

    private JSONArray savedResults;
    private int offset = 20;
    private int scrollPosition = 0;

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains latest reviews in JSON, limited to 20 latest reviews, and sorted by latest date
    public static final String LATEST_REVIEWS_URL = "https://www.gamespot.com/api/reviews/?api_key="
            + consumerKey + "&format=json&limit=20&sort=update_date:desc";

    // Obtains top reviews in JSON, limited to 20 latest reviews, and sorted by latest date
    public static final String TOP_REVIEWS_URL = "https://www.gamespot.com/api/reviews/?api_key="
            + consumerKey + "&format=json&limit=20&sort=score:desc,update_date:desc";

    // Obtains lowest reviews in JSON, limited to 20 latest reviews, and sorted by latest date
    public static final String LOW_REVIEWS_URL = "https://www.gamespot.com/api/reviews/?api_key="
            + consumerKey + "&format=json&limit=20&sort=score:asc,update_date:desc";

    private int reviewPosition = 0;
    private String currentUrl = LATEST_REVIEWS_URL;
    private String lastFilter = "";

    public String getLastFilter() { return lastFilter; }

    public void setLastFilter(String lastFilter) { this.lastFilter = lastFilter; }

    public void setFilterOnCurrentUrl(String filter) {
        this.currentUrl += "&filter=title:" + filter;
        setLastFilter(filter);
    }

    public int getReviewPosition() { return reviewPosition; }

    public String getCurrentUrl() { return currentUrl; }

    private void updateWithLastFilter(String currentQuery) {
        if (lastFilter.equals("")) {
            this.currentUrl = currentQuery;
            return;
        }

        this.currentUrl = currentQuery + "&filter=title:" + lastFilter;
    }

    public void setCurrentUrlToLatest() {
        updateWithLastFilter(LATEST_REVIEWS_URL);
        this.reviewPosition = 0;
    }

    public void setCurrentUrlToTop() {
        updateWithLastFilter(TOP_REVIEWS_URL);
        this.reviewPosition = 1;
    }

    public void setCurrentUrlToLow() {
        updateWithLastFilter(LOW_REVIEWS_URL);
        this.reviewPosition = 2;
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
        savedResults = null;
    }
}
