package com.law1111776.android.gamenewsapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.law1111776.android.gamenewsapp.BuildConfig;
import com.law1111776.android.gamenewsapp.EndlessRecyclerViewScrollListener;
import com.law1111776.android.gamenewsapp.MainActivity;
import com.law1111776.android.gamenewsapp.R;
import com.law1111776.android.gamenewsapp.adapters.VideosAdapter;
import com.law1111776.android.gamenewsapp.models.Videos;
import com.law1111776.android.gamenewsapp.viewmodels.VideosFragmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class VideosFragment extends Fragment {

    public static final String TAG = "VideosFragment";

    List<Videos> videos;
    private SwipeRefreshLayout swipeContainer;
    VideosAdapter videosAdapter;
    VideosFragmentViewModel videosFragmentViewModel;
    RecyclerView rvVideos;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos, container, false);
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
            videosFragmentViewModel.resetOffset();
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_purple);

        rvVideos = view.findViewById(R.id.rvVideos);
        videos = new ArrayList<>();

        // Create the adapter
        videosAdapter = new VideosAdapter(getContext(), videos);

        // Set the adapter on the recycler view
        rvVideos.setAdapter(videosAdapter);

        // Linear Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Set a Layout Manager on the recycler view
        rvVideos.setLayoutManager(linearLayoutManager);

        // ViewModel that would save data over tab changes
        videosFragmentViewModel = new ViewModelProvider(requireActivity()).get(VideosFragmentViewModel.class);

        // EndlessRecyclerViewScrollListener - Loads the next 20 cards when user hit the bottom
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(videosFragmentViewModel.getOffset());
                videosFragmentViewModel.updateOffset();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvVideos.addOnScrollListener(scrollListener);

        JSONArray storedData = videosFragmentViewModel.getResults();

        // Check if storedData actually has data.
        // If not, fetch data from API then store it in ViewModel.
        if (storedData != null) {
            try {
                Log.i(TAG, "Stored Results: " + storedData.toString());
                videos.addAll(Videos.fromJsonArray(storedData));
                videosAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            fetchOldData();
        }
        setScrollPosition();
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
        searchView.setQuery(videosFragmentViewModel.getLastFilter(), false);
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

        videosFragmentViewModel.setFilterOnCurrentUrl(query);

        // Fixes bug in which user switches tabs too quickly before
        // refreshData() finishes executing.
        videosFragmentViewModel.resetResults();

        // Update UI with no data to give feedback to user that
        // it is reloading.
        videosAdapter.clear();

        refreshData(); // So new data is loaded

        // Reset offset
        videosFragmentViewModel.resetOffset();

        // Set scroll position since refreshData() is initially only for the refresh feature.
        // 1. Save new scroll position in view model
        videosFragmentViewModel.setScrollPosition(0);

        // 2. Set new scroll position on view
        setScrollPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        videosFragmentViewModel.setScrollPosition(getPosition());
        Log.i(TAG, "onPause: " + videosFragmentViewModel.getScrollPosition());
    }

    private int getPosition() {
        int firstCompletelyVisibleItemPosition =
                ((LinearLayoutManager)rvVideos.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        // If there is not a completely visible item,
        // it would return -1 so just get the first
        // visible item instead.
        if (firstCompletelyVisibleItemPosition == -1)
            return ((LinearLayoutManager)rvVideos.getLayoutManager()).findFirstVisibleItemPosition();

        return firstCompletelyVisibleItemPosition;
    }

    public void setScrollPosition() {
        rvVideos.getLayoutManager().scrollToPosition(videosFragmentViewModel.getScrollPosition());
        Log.i(TAG, "setScrollPosition: " + videosFragmentViewModel.getScrollPosition());
    }

    private void refreshData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(videosFragmentViewModel.getCurrentUrl(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    // Empties the JSONArray in ViewModel
                    videosFragmentViewModel.resetResults();

                    // Saves the data of the first 20 cards from API call
                    videosFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + videosFragmentViewModel.getResults());

                    // Remember to CLEAR OUT old items before appending in the new ones
                    videosAdapter.clear();

                    // ...the data has come back, add new items to your adapter...
                    videos.addAll(Videos.fromJsonArray(results));

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    videosAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Videos: " + videos.size());
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
        client.get(videosFragmentViewModel.getCurrentUrl() + "&offset=" + offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    videosFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + videosFragmentViewModel.getResults());
                    // ...the data has come back, add new items to your adapter...
                    videos.addAll(Videos.fromJsonArray(results));
                    videosAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Videos: " + videos.size());
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
        client.get(videosFragmentViewModel.getCurrentUrl(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    videosFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + videosFragmentViewModel.getResults());
                    // Remember to CLEAR OUT old items before appending in the new ones
                    videosAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    videos.addAll(Videos.fromJsonArray(results));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    videosAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Videos: " + videos.size());
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
