package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.DividerItemDecoration;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.R.id.rvTweet;

public class TimelineActivity extends AppCompatActivity {

    // create reference for twitter client
    private TwitterClient client;

    // list of tweets
    ArrayList<Tweet> tweets;

    // recycler view
    RecyclerView rvTweets;

    // the adapter wired to the new view
    TweetAdapter adapter;

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;

    // reference for possible newTweet;
    private Tweet newTweet;

    // reference for possible newTweet;
    private final int RESULT_OK = 10;

    // setup swipe thing
    private SwipeRefreshLayout swipeContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_activity);
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
                android.R.color.holo_red_light);

        // initialize the client
        client = TwitterApplication.getRestClient();

        // find the Recycler view
        rvTweets = (RecyclerView) findViewById(rvTweet);


        /* add line divider
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration); */

        // initialize the list of tweets
        tweets = new ArrayList<>();
        // construct the adater from the data source
        adapter = new TweetAdapter(tweets);
        // recycler setup (connect a layout manager and an adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        /* add the reply click listener
        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Tweet tweet = tweets.get(position);
                        String screenName = tweet.user.screenName;
                        // create intent for the new activity
                        Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(tweet));
                        // show the activity
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
        );
        */

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);

        populateTimeline();


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

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

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


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient",responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            };

        });
    }

    //
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
}



