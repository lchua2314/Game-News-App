package com.onemanarmy.android.gamenewsapp.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

    private SwipeRefreshLayout swipeContainer;
    LatestReviewsAdapter latestReviewsAdapter;
    LatestReviewsFragmentViewModel latestReviewsFragmentViewModel;
    RecyclerView rvLatestReviews;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest_reviews, container, false);
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
            latestReviewsFragmentViewModel.resetOffset();
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_purple);

        rvLatestReviews = view.findViewById(R.id.rvLatestReviews);
        latestReviews = new ArrayList<>();

        // Create the adapter
        latestReviewsAdapter = new LatestReviewsAdapter(getContext(), latestReviews);

        // Set the adapter on the recycler view
        rvLatestReviews.setAdapter(latestReviewsAdapter);

        // Linear Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Set a Layout Manager on the recycler view
        rvLatestReviews.setLayoutManager(linearLayoutManager);

        // ViewModel that would save data over tab changes
        latestReviewsFragmentViewModel = new ViewModelProvider(requireActivity()).get(LatestReviewsFragmentViewModel.class);

        // EndlessRecyclerViewScrollListener - Loads the next 20 cards when user hit the bottom
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(latestReviewsFragmentViewModel.getOffset());
                latestReviewsFragmentViewModel.updateOffset();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvLatestReviews.addOnScrollListener(scrollListener);

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
            fetchOldData();
        }
        setScrollPosition();


        // Spinner/Dropdown menu to change quality
        Spinner dropdown = view.findViewById(R.id.spinner1);

        String[] items = new String[]{"Newest", "Highest Rated", "Lowest Rated"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.articles_spinner_item, items);

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);

        // Default already sets to "Newest"
        dropdown.setSelection(latestReviewsFragmentViewModel.getReviewPosition(), false); // default set to latest

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));

                switch (position) {
                    case 0: // change to latest
                        Log.i(TAG, "case 0 - Newest = Latest");
                        latestReviewsFragmentViewModel.setReviewUrlToLatest();
                        break;
                    case 1: // change to top
                        Log.i(TAG, "case 1 - Highest Rated = Top");
                        latestReviewsFragmentViewModel.setReviewUrlToTop();
                        break;
                    default: // change to lowest
                        Log.i(TAG, "default case - Lowest Rated = Low");
                        latestReviewsFragmentViewModel.setReviewUrlToLow();
                }
                latestReviewsAdapter.clear();
                refreshData(); // So new data is loaded
                // Reset offset
                latestReviewsFragmentViewModel.resetOffset();
                // Set scroll position since refreshData() is initially only for the refresh feature.
                // 1. Save new scroll position in view model
                latestReviewsFragmentViewModel.setScrollPosition(0);
                // 2. Set new scroll position on view
                setScrollPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        latestReviewsFragmentViewModel.setScrollPosition(getPosition());
        Log.i(TAG, "onPause: " + latestReviewsFragmentViewModel.getScrollPosition());
    }

    private int getPosition() {
        int firstCompletelyVisibleItemPosition =
                ((LinearLayoutManager)rvLatestReviews.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        // If there is not a completely visible item,
        // it would return -1 so just get the first
        // visible item instead.
        if (firstCompletelyVisibleItemPosition == -1)
            return ((LinearLayoutManager)rvLatestReviews.getLayoutManager()).findFirstVisibleItemPosition();

        return firstCompletelyVisibleItemPosition;
    }

    public void setScrollPosition() {
        rvLatestReviews.getLayoutManager().scrollToPosition(latestReviewsFragmentViewModel.getScrollPosition());
        Log.i(TAG, "setScrollPosition: " + latestReviewsFragmentViewModel.getScrollPosition());
    }

    private void refreshData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(latestReviewsFragmentViewModel.getReviewUrl(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    // Empties the JSONArray in ViewModel
                    latestReviewsFragmentViewModel.resetResults();

                    // Saves the data of the first 20 cards from API call
                    latestReviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + latestReviewsFragmentViewModel.getResults());

                    // Remember to CLEAR OUT old items before appending in the new ones
                    latestReviewsAdapter.clear();

                    // ...the data has come back, add new items to your adapter...
                    latestReviews.addAll(LatestReviews.fromJsonArray(results));

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    latestReviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Latest Reviews: " + latestReviews.size());
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
        client.get(latestReviewsFragmentViewModel.getReviewUrl() + "&offset=" + offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    latestReviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + latestReviewsFragmentViewModel.getResults());
                    // ...the data has come back, add new items to your adapter...
                    latestReviews.addAll(LatestReviews.fromJsonArray(results));
                    latestReviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Latest Reviews: " + latestReviews.size());
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
        client.get(latestReviewsFragmentViewModel.getReviewUrl(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    latestReviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + latestReviewsFragmentViewModel.getResults());
                    // Remember to CLEAR OUT old items before appending in the new ones
                    latestReviewsAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    latestReviews.addAll(LatestReviews.fromJsonArray(results));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
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
    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    */
}
