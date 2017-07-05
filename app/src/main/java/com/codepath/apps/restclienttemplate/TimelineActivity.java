package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

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


    // timeliene activity's main function is loading the fragments
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_activity);

        /* REFRESHING THING
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light); */



        /*
        // Infinite Pagination. Retain an instance so that you can call 'resetState() for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                client.getHomeTimeline(lowMaxId, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        fragmentTweetsLists.addItems(response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("OnLoadMore failure",responseString);
                        throwable.printStackTrace();
                    }
                });
            }
        };

        // adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration); */

        // get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);

        // set the adapter
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(), this));
        // setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);


    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    // adds the icons in the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // when user clicks to compose a tweet, send intent
    public void onComposeAction(MenuItem mi) {
        // create intent for the new activity
        Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
        // show the activity
        startActivityForResult(intent, REQUEST_CODE);
    }

    /*
    // once the sub-activity finishes, the onActivityResult() method in the calling activity is be invoked:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            newTweet = (Tweet) Parcels.unwrap(intent.getParcelableExtra(Tweet.class.getSimpleName()));

            tweets.add(0, newTweet);
            adapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);

            // Toast the name to display temporarily on screen
            Toast.makeText(this, "Tweeted!", Toast.LENGTH_LONG).show();
        }
    }
    */


    /*
    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();
                // iterate through the results
                for (int i = 0; i < response.length(); i++) {
                    try {
                        // for each entry, deserialize the JSON object
                        JSONObject json = response.getJSONObject(i);

                        // convert each object to a Tweet model
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));

                        // add the tweet model to data source
                        tweets.add(tweet);

                        // notify adapter that we've added an item
                        adapter.notifyItemInserted(tweets.size() - 1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                Log.i("Refresh", "ay");
            }



            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }
    */

    public void OnProfileView(MenuItem item) {
        Intent intent = new Intent(this, ProfileActivity.class);
        //intent.putExtra(User.class.getSimpleName(), )

        startActivity(intent);
    }
}



