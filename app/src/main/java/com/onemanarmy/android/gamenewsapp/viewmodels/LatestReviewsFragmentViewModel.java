package com.onemanarmy.android.gamenewsapp.viewmodels;

import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LatestReviewsFragmentViewModel extends ViewModel {

    private JSONArray savedResults;
    private int offset = 20;

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
}
