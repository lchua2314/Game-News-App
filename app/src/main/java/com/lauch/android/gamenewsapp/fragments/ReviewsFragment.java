package com.lauch.android.gamenewsapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
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
import com.lauch.android.gamenewsapp.BuildConfig;
import com.lauch.android.gamenewsapp.EndlessRecyclerViewScrollListener;
import com.lauch.android.gamenewsapp.MainActivity;
import com.lauch.android.gamenewsapp.R;
import com.lauch.android.gamenewsapp.adapters.ReviewsAdapter;
import com.lauch.android.gamenewsapp.models.Reviews;
import com.lauch.android.gamenewsapp.viewmodels.ReviewsFragmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ReviewsFragment extends Fragment {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains latest reviews in JSON, limited to 20 latest reviews, and sorted by latest date
    public static final String LATEST_REVIEWS_URL = "https://www.gamespot.com/api/reviews/?api_key="
            + consumerKey + "&format=json&limit=20&sort=update_date:desc";
    public static final String TAG = "ReviewsFragment";

    List<Reviews> reviews;

    private SwipeRefreshLayout swipeContainer;
    ReviewsAdapter reviewsAdapter;
    ReviewsFragmentViewModel reviewsFragmentViewModel;
    RecyclerView rvReviews;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Needed for search bar or else it won't show
        setHasOptionsMenu(true);

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            refreshData();
            reviewsFragmentViewModel.resetOffset();
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_purple);

        rvReviews = view.findViewById(R.id.rvReviews);
        reviews = new ArrayList<>();

        // Create the adapter
        reviewsAdapter = new ReviewsAdapter(getContext(), reviews);

        // Set the adapter on the recycler view
        rvReviews.setAdapter(reviewsAdapter);

        // Linear Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Set a Layout Manager on the recycler view
        rvReviews.setLayoutManager(linearLayoutManager);

        // ViewModel that would save data over tab changes
        reviewsFragmentViewModel = new ViewModelProvider(requireActivity()).get(ReviewsFragmentViewModel.class);

        // EndlessRecyclerViewScrollListener - Loads the next 20 cards when user hit the bottom
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(reviewsFragmentViewModel.getOffset());
                reviewsFragmentViewModel.updateOffset();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvReviews.addOnScrollListener(scrollListener);

        JSONArray storedData = reviewsFragmentViewModel.getResults();

        // Check if storedData actually has data.
        // If not, fetch data from API then store it in ViewModel.
        if (storedData != null) {
            try {
                Log.i(TAG, "Stored Results: " + storedData.toString());

                reviews.addAll(Reviews.fromJsonArray(storedData));
                reviewsAdapter.notifyDataSetChanged();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.reviews_spinner_item, items);

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);

        // Default already sets to "Newest"
        dropdown.setSelection(reviewsFragmentViewModel.getReviewPosition(), false); // default set to latest

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));

                switch (position) {
                    case 0: // change to latest
                        Log.i(TAG, "case 0 - Newest = Latest");
                        reviewsFragmentViewModel.setCurrentUrlToLatest();
                        break;
                    case 1: // change to top
                        Log.i(TAG, "case 1 - Highest Rated = Top");
                        reviewsFragmentViewModel.setCurrentUrlToTop();
                        break;
                    default: // change to lowest
                        Log.i(TAG, "default case - Lowest Rated = Low");
                        reviewsFragmentViewModel.setCurrentUrlToLow();
                }

                changeReview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);

        // Clear Query Button
        MenuItem clearItem = menu.findItem(R.id.clear);

        Button clearBtn = new Button(((MainActivity) getContext()).getSupportActionBar().getThemedContext());

        //clearItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        clearItem.setActionView(clearBtn);

        clearBtn.setText("Clear");
        clearBtn.setBackgroundColor(getResources().getColor(R.color.dark_theme_blue));

        clearBtn.setOnClickListener(v -> setFilter(""));
        clearItem.setOnMenuItemClickListener(v -> {
            setFilter("");
            return false;
        });

        // Search Bar
        MenuItem item = menu.findItem(R.id.search_bar);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        // searchView.setIconified(false);
        //item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setQuery(reviewsFragmentViewModel.getLastFilter(), false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setFilter(query);
                searchView.clearFocus(); // Allows user to open detail views and go back without the keyboard opening
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "YEP", Toast.LENGTH_SHORT);
        });
    }

    private void setFilter(String query) {
        reviewsFragmentViewModel.setFilterOnCurrentUrl(query);
        changeReview();
    }

    private void changeReview() {
        // Fixes bug in which user switches tabs too quickly before
        // refreshData() finishes executing.
        reviewsFragmentViewModel.resetResults();

        // Update UI with no data to give feedback to user that
        // it is reloading.
        reviewsAdapter.clear();

        refreshData(); // So new data is loaded

        // Reset offset
        reviewsFragmentViewModel.resetOffset();

        // Set scroll position since refreshData() is initially only for the refresh feature.
        // 1. Save new scroll position in view model
        reviewsFragmentViewModel.setScrollPosition(0);

        // 2. Set new scroll position on view
        setScrollPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        reviewsFragmentViewModel.setScrollPosition(getPosition());
        Log.i(TAG, "onPause: " + reviewsFragmentViewModel.getScrollPosition());
    }

    private int getPosition() {
        int firstCompletelyVisibleItemPosition =
                ((LinearLayoutManager)rvReviews.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        // If there is not a completely visible item,
        // it would return -1 so just get the first
        // visible item instead.
        if (firstCompletelyVisibleItemPosition == -1)
            return ((LinearLayoutManager)rvReviews.getLayoutManager()).findFirstVisibleItemPosition();

        return firstCompletelyVisibleItemPosition;
    }

    public void setScrollPosition() {
        rvReviews.getLayoutManager().scrollToPosition(reviewsFragmentViewModel.getScrollPosition());
        Log.i(TAG, "setScrollPosition: " + reviewsFragmentViewModel.getScrollPosition());
    }

    private void refreshData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(reviewsFragmentViewModel.getCurrentUrl(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    // Empties the JSONArray in ViewModel
                    reviewsFragmentViewModel.resetResults();

                    // Saves the data of the first 20 cards from API call
                    reviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + reviewsFragmentViewModel.getResults());

                    // Remember to CLEAR OUT old items before appending in the new ones
                    reviewsAdapter.clear();

                    // ...the data has come back, add new items to your adapter...
                    reviews.addAll(Reviews.fromJsonArray(results));

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    reviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Latest Reviews: " + reviews.size());
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
        client.get(reviewsFragmentViewModel.getCurrentUrl() + "&offset=" + offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    reviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + reviewsFragmentViewModel.getResults());
                    // ...the data has come back, add new items to your adapter...
                    reviews.addAll(Reviews.fromJsonArray(results));
                    reviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Latest Reviews: " + reviews.size());
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
        client.get(reviewsFragmentViewModel.getCurrentUrl(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    reviewsFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + reviewsFragmentViewModel.getResults());
                    // Remember to CLEAR OUT old items before appending in the new ones
                    reviewsAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    reviews.addAll(Reviews.fromJsonArray(results));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    reviewsAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Reviews: " + reviews.size());
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
