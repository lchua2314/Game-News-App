package com.onemanarmy.android.gamenewsapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.onemanarmy.android.gamenewsapp.fragments.ArticlesFragment;
import com.onemanarmy.android.gamenewsapp.fragments.ReviewsFragment;
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
                case R.id.action_reviews:
                    fragment = new ReviewsFragment();
                    tab = "reviews";
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
                case "reviews":
                    bottomNavigationView.setSelectedItemId(R.id.action_reviews);
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
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate menu with items using MenuInflator
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        // Initialise menu item search bar
        // with id and take its object
        MenuItem searchViewItem
                = menu.findItem(R.id.search_bar);
        SearchView searchView
                = (SearchView) MenuItemCompat
                .getActionView(searchViewItem);

        // attach setOnQueryTextListener
        // to search view defined above
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                // Override onQueryTextSubmit method
                // which is call
                // when submitquery is searched

                @Override
                public boolean onQueryTextSubmit(String query)
                {
                        Toast
                                .makeText(MainActivity.this,
                                        "Not found",
                                        Toast.LENGTH_LONG)
                                .show();
                    return false;
                }

                // This method is overridden to filter
                // the adapter according to a search query
                // when the user is typing search
                @Override
                public boolean onQueryTextChange(String newText)
                {
                    Toast
                            .makeText(MainActivity.this,
                                    "onQueryTextChange",
                                    Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
            });
        return super.onCreateOptionsMenu(menu);
    }
 */
}