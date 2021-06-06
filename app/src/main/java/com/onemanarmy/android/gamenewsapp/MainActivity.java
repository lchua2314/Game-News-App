package com.onemanarmy.android.gamenewsapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onemanarmy.android.gamenewsapp.fragments.ArticlesFragment;
import com.onemanarmy.android.gamenewsapp.fragments.LatestReviewsFragment;
import com.onemanarmy.android.gamenewsapp.fragments.VideosFragment;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final String KEY_INDEX = "index";
    private String tab;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set title of action bar since app name and action bar are separate.
        getSupportActionBar().setTitle(R.string.actionBarNameMainActivity);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            Fragment fragment;
            switch (menuItem.getItemId()) {
                case R.id.action_articles:
                    fragment = new ArticlesFragment();
                    tab = "articles";
                    break;
                case R.id.action_latest_reviews:
                    fragment = new LatestReviewsFragment();
                    tab = "latest_reviews";
                    break;
                case R.id.action_videos:
                default:
                    fragment = new VideosFragment();
                    tab = "videos";
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return true;
        });

        // Checks if savedInstanceState is null
        // This happens because it hasn't been initialized yet
        // until after first activity is created
        // If not null, retrieve the tab data
        // This makes sure the data gets retrieved after configuration changes.
        if (savedInstanceState != null) {
            String currentTab = savedInstanceState.getString(KEY_INDEX, "null");
            switch (currentTab) {
                case "latest_reviews":
                    bottomNavigationView.setSelectedItemId(R.id.action_latest_reviews);
                    break;
                case "videos":
                    bottomNavigationView.setSelectedItemId(R.id.action_videos);
                    break;
                case "articles":
                default:
                    bottomNavigationView.setSelectedItemId(R.id.action_articles);
            }
        } else bottomNavigationView.setSelectedItemId(R.id.action_articles);
    }

    // Saves what tab the user is on over configuration changes.
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(KEY_INDEX, tab);
    }
}