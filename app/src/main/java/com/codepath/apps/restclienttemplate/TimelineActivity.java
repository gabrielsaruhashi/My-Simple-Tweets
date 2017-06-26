package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_activity);


        // initialize the client
        client = TwitterApplication.getRestClient();

        // find the Recycler view
        rvTweets = (RecyclerView) findViewById(rvTweet);

        // initialize the list of tweets
        tweets = new ArrayList<>();

        // construct the adater from the data source
        adapter = new TweetAdapter(tweets);

        // recycler setup (connect a layout manager and an adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateTimeline();
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


}
