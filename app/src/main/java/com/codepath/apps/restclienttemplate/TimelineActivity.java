package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity {

    // create reference for twitter client
    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;
    // reference for possible newTweet;
    private Tweet newTweet;
    // reference for possible newTweet;
    private final int RESULT_OK = 10;
    // setup swipe thing
    private SwipeRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    // store the lowest max id
    private long lowMaxId;

    // store the page id
    private String pageName;

    private ViewPager vpPager;
    private TweetsPagerAdapter tweetsPageAdapter;
    private HomeTimelineFragment newFrag;

    // timeliene activity's main function is loading the fragments
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_activity);

        // get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        tweetsPageAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        // set the adapter
        vpPager.setAdapter(tweetsPageAdapter);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TweetsListFragment f = (TweetsListFragment) tweetsPageAdapter.getItem(position);
                f.setPage(position);
                Log.i("DEBUG", Integer.toString(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

    }

    // adds the icons in the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    // when user clicks to compose a tweet, send intent
    public void onComposeAction(MenuItem mi) {
        // create intent for the new activity
        Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
        // show the activity
        startActivityForResult(intent, REQUEST_CODE);
    }


    // once the sub-activity finishes, the onActivityResult() method in the calling activity is be invoked:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            newTweet = (Tweet) Parcels.unwrap(intent.getParcelableExtra(Tweet.class.getSimpleName()));
            newFrag = (HomeTimelineFragment) tweetsPageAdapter.getItem(0);
            vpPager.setCurrentItem(0);
            newFrag.addTweet(newTweet);
        }
    }


    public void OnProfileView(MenuItem item) {
        Intent intent = new Intent(this, ProfileActivity.class);
        //intent.putExtra(User.class.getSimpleName(), )
        startActivity(intent);
    }

    public void addTweet(Tweet tweet) {

    }
}



