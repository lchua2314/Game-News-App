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
import com.onemanarmy.android.gamenewsapp.adapters.VideosAdapter;
import com.onemanarmy.android.gamenewsapp.models.Videos;
import com.onemanarmy.android.gamenewsapp.viewmodels.VideosFragmentViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class VideosFragment extends Fragment {

    public static final String consumerKey = BuildConfig.CONSUMER_KEY;
    // Obtains videos in JSON, limited to 20 videos, and sorted by latest date
    public static final String GAME_NEWS_URL = "https://www.gamespot.com/api/videos/?api_key="
            + consumerKey + "&format=json&limit=20&sort=publish_date:desc";
    public static final String TAG = "VideosFragment";

    List<Videos> videos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvVideos = view.findViewById(R.id.rvVideos);
        videos = new ArrayList<>();

        // Create the adapter
        final VideosAdapter videosAdapter = new VideosAdapter(getContext(), videos);

        // Set the adapter on the recycler view
        rvVideos.setAdapter(videosAdapter);

        // Set a Layout Manager on the recycler view
        rvVideos.setLayoutManager(new LinearLayoutManager(getContext()));

        // ViewModel that would save data over tab changes
        VideosFragmentViewModel videosFragmentViewModel = new ViewModelProvider(requireActivity()).get(VideosFragmentViewModel.class);
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
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(GAME_NEWS_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "onSuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray results = jsonObject.getJSONArray("results");
                        videosFragmentViewModel.saveResults(results);
                        Log.i(TAG, "Just Stored Results: " + videosFragmentViewModel.getResults());
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
    }
}
