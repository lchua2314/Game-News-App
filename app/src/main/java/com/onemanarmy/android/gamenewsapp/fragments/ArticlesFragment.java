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
import com.onemanarmy.android.gamenewsapp.adapters.ArticlesAdapter;
import com.onemanarmy.android.gamenewsapp.models.Articles;
import com.onemanarmy.android.gamenewsapp.viewmodels.ArticlesFragmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ArticlesFragment extends Fragment {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains news articles in JSON, limited to 20 articles, and sorted by latest date
    public static final String GAME_NEWS_URL = "https://www.gamespot.com/api/articles/?api_key="
            + consumerKey + "&format=json&limit=20&sort=publish_date:desc";
    public static final String TAG = "ArticlesFragment";

    List<Articles> articles;

    private SwipeRefreshLayout swipeContainer;
    ArticlesFragmentViewModel articlesFragmentViewModel;
    ArticlesAdapter articlesAdapter;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            refreshData();
            articlesFragmentViewModel.resetOffset();
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_purple);

        RecyclerView rvArticles = view.findViewById(R.id.rvArticles);
        articles = new ArrayList<>();

        // Create the adapter
        articlesAdapter = new ArticlesAdapter(getContext(), articles);

        // Set the adapter on the recycler view
        rvArticles.setAdapter(articlesAdapter);

        // Linear Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Set a Layout Manager on the recycler view
        rvArticles.setLayoutManager(linearLayoutManager);

        // ViewModel that would save data over tab changes
        articlesFragmentViewModel = new ViewModelProvider(requireActivity()).get(ArticlesFragmentViewModel.class);

        // EndlessRecyclerViewScrollListener - Loads the next 20 cards when user hit the bottom
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(articlesFragmentViewModel.getOffset());
                articlesFragmentViewModel.updateOffset();
            }
        };

        // Adds the scroll listener to RecyclerView
        rvArticles.addOnScrollListener(scrollListener);

        JSONArray storedData = articlesFragmentViewModel.getResults();

        // Check if storedData actually has data.
        // If not, fetch data from API then store it in ViewModel.
        if (storedData != null) {
            try {
                Log.i(TAG, "Stored Results: " + storedData.toString());

                articles.addAll(Articles.fromJsonArray(storedData));
                articlesAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            fetchOldData();
        }
    }

    private void refreshData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GAME_NEWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    // Empties the JSONArray in ViewModel
                    articlesFragmentViewModel.resetResults();

                    // Saves the data of the first 20 cards from API call
                    articlesFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + articlesFragmentViewModel.getResults());

                    // Remember to CLEAR OUT old items before appending in the new ones
                    articlesAdapter.clear();

                    // ...the data has come back, add new items to your adapter...
                    articles.addAll(Articles.fromJsonArray(results));

                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    articlesAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Articles: " + articles.size());
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
        client.get(GAME_NEWS_URL + "&offset=" + offset, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    articlesFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + articlesFragmentViewModel.getResults());
                    // ...the data has come back, add new items to your adapter...
                    articles.addAll(Articles.fromJsonArray(results));
                    articlesAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Articles: " + articles.size());
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

    public void fetchOldData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GAME_NEWS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    articlesFragmentViewModel.saveResults(results);
                    Log.i(TAG, "Just Stored Results: " + articlesFragmentViewModel.getResults());
                    // Remember to CLEAR OUT old items before appending in the new ones
                    articlesAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    articles.addAll(Articles.fromJsonArray(results));
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                    articlesAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Articles: " + articles.size());
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
