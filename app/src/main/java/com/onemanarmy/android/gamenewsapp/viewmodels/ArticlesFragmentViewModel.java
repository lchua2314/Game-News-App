package com.onemanarmy.android.gamenewsapp.viewmodels;

import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

public class ArticlesFragmentViewModel extends ViewModel {

    private JSONArray savedResults;

    public JSONArray getResults() {
        // TODO: Move model data out of controller logic
        if (savedResults == null) {
        }
        return savedResults;
    }

    public void saveResults(JSONArray results) {
        savedResults = results;
    }
}
