package com.onemanarmy.android.gamenewsapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.onemanarmy.android.gamenewsapp.BuildConfig;
import com.onemanarmy.android.gamenewsapp.R;
import com.onemanarmy.android.gamenewsapp.adapters.LatestReviewsAdapter;
import com.onemanarmy.android.gamenewsapp.models.LatestReviews;
import com.onemanarmy.android.gamenewsapp.viewmodels.LatestReviewsFragmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class LatestReviewsFragment extends Fragment {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains latest reviews in JSON, limited to 20 latest reviews, and sorted by latest date
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

        // ViewModel that would save data over tab changes
        LatestReviewsFragmentViewModel latestReviewsFragmentViewModel = new ViewModelProvider(requireActivity()).get(LatestReviewsFragmentViewModel.class);
        JSONArray storedData = latestReviewsFragmentViewModel.getResults();

        // Check if storedData actually has data.
        // If not, fetch data from API then store it in ViewModel.
        if (storedData != null) {
            try {
                Log.i(TAG, "Stored Results: " + storedData.toString());

                latestReviews.addAll(LatestReviews.fromJsonArray(storedData));
                latestReviewsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(LATEST_REVIEWS_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "onSuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray results = jsonObject.getJSONArray("results");
                        latestReviewsFragmentViewModel.saveResults(results);
                        Log.i(TAG, "Just Stored Results: " + latestReviewsFragmentViewModel.getResults());
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
}
