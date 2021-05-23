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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.onemanarmy.android.gamenewsapp.BuildConfig;
import com.onemanarmy.android.gamenewsapp.EndlessRecyclerViewScrollListener;
import com.onemanarmy.android.gamenewsapp.R;
import com.onemanarmy.android.gamenewsapp.adapters.TopReviewsAdapter;
import com.onemanarmy.android.gamenewsapp.models.Articles;
import com.onemanarmy.android.gamenewsapp.models.TopReviews;
import com.onemanarmy.android.gamenewsapp.viewmodels.TopReviewsFragmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TopReviewsFragment extends Fragment {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains top reviews in JSON, limited to 20 latest reviews, and sorted by latest date
    public static final String TOP_REVIEWS_URL = "https://www.gamespot.com/api/reviews/?api_key="
            + consumerKey + "&format=json&limit=20&sort=score:desc,update_date:desc";
    public static final String TAG = "TopReviewsFragment";

    List<TopReviews> topReviews;
    private SwipeRefreshLayout swipeContainer;
    TopReviewsAdapter topReviewsAdapter;
    TopReviewsFragmentViewModel topReviewsFragmentViewModel;
    RecyclerView rvTopReviews;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            refreshData();
            topReviewsFragmentViewModel.resetOffset();
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_purple);

        rvTopReviews = view.findViewById(R.id.rvTopReviews);
        topReviews = new ArrayList<>();

        // Create the adapter
        topReviewsAdapter = new TopReviewsAdapter(getContext(), topReviews);

        // Set the adapter on the recycler view
        rvTopReviews.setAdapter(topReviewsAdapter);

        // Linear Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Set a Layout Manager on the recycler view
        rvTopReviews.setLayoutManager(linearLayoutManager);

        // ViewModel that would save data over tab changes
        topReviewsFragmentViewModel = new ViewModelProvider(requireActivity()).get(TopReviewsFragmentViewModel.class);

        // EndlessRecyclerViewScrollListener - Loads the next 20 cards when user hit the bottom
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(topReviewsFragmentViewModel.getOffset());
                topReviewsFragmentViewModel.updateOffset();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvTopReviews.addOnScrollListener(scrollListener);

        JSONArray storedData = topReviewsFragmentViewModel.getResults();

        // Check if storedData actually has data.
        // If not, fetch data from API then store it in ViewModel.
        if (storedData != null) {
            try {
                Log.i(TAG, "Stored Results: " + storedData.toString());
                topReviews.addAll(TopReviews.fromJsonArray(storedData));
                topReviewsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            fetchOldData();
        }
        setScrollPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        topReviewsFragmentViewModel.setScrollPosition(
                ((LinearLayoutManager)rvTopReviews.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
        Log.i(TAG, "onPause: " + topReviewsFragmentViewModel.getScrollPosition());
    }

    public void setScrollPosition() {
        rvTopReviews.getLayoutManager().scrollToPosition(topReviewsFragmentViewModel.getScrollPosition());
        Log.i(TAG, "setScrollPosition: " + topReviewsFragmentViewModel.getScrollPosition());
    }

    private void refreshData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(TOP_REVIEWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    // Empties the JSONArray in ViewModel
                    topReviewsFragmentViewModel.resetResults();

                    // Saves the data of the first 20 cards from API call
                    topReviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + topReviewsFragmentViewModel.getResults());

                    // Remember to CLEAR OUT old items before appending in the new ones
                    topReviewsAdapter.clear();

                    // ...the data has come back, add new items to your adapter...
                    topReviews.addAll(TopReviews.fromJsonArray(results));

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    topReviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Top Reviews: " + topReviews.size());
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

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(TOP_REVIEWS_URL + "&offset=" + offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    topReviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + topReviewsFragmentViewModel.getResults());
                    // ...the data has come back, add new items to your adapter...
                    topReviews.addAll(TopReviews.fromJsonArray(results));
                    topReviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Top Reviews: " + topReviews.size());
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

    private void fetchOldData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(TOP_REVIEWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    topReviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + topReviewsFragmentViewModel.getResults());
                    // Remember to CLEAR OUT old items before appending in the new ones
                    topReviewsAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    topReviews.addAll(TopReviews.fromJsonArray(results));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    topReviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "TopReviews: " + topReviews.size());
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