package com.onemanarmy.android.gamenewsapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.onemanarmy.android.gamenewsapp.BuildConfig;
import com.onemanarmy.android.gamenewsapp.R;
import com.onemanarmy.android.gamenewsapp.adapters.LatestReviewsAdapter;
import com.onemanarmy.android.gamenewsapp.models.LatestReviews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class LatestReviewsFragment extends Fragment {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains news latest reviews in JSON, limited to 20 latest reviews, and sorted by latest date
    public static final String LATEST_REVIEWS_URL = "https://www.gamespot.com/api/reviews/?api_key="
            + consumerKey + "&format=json&limit=20&sort=update_date:desc";
    public static final String TAG = "LatestReviewsFragment";

    List<LatestReviews> latestReviews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvLatestReviews = view.findViewById(R.id.rvLatestReviews);
        latestReviews = new ArrayList<>();

        // Create the adapter
        final LatestReviewsAdapter latestReviewsAdapter = new LatestReviewsAdapter(getContext(), latestReviews);

        // Set the adapter on the recycler view
        rvLatestReviews.setAdapter(latestReviewsAdapter);

        // Set a Layout Manager on the recycler view
        rvLatestReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(LATEST_REVIEWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    latestReviews.addAll(LatestReviews.fromJsonArray(results));
                    latestReviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "LatestReviews: " + latestReviews.size());
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
