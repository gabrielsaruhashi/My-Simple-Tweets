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

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class SearchActivity extends AppCompatActivity {
    private ArrayList<Tweet> tweetsSearch;
    private TwitterClient client;
    private String query;
    private TweetAdapter adapter;
    private RecyclerView rvTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        query = getIntent().getStringExtra("Search Query");
        // find the Recycler view
        rvTweet = (RecyclerView) findViewById(R.id.rvTweet);
        // initialize client
        client = TwitterApplication.getRestClient();

        // initialize the list of tweets
        tweetsSearch = new ArrayList<>();
        // construct the adater from the data source
        adapter = new TweetAdapter(tweetsSearch);
        // recycler setup (connect a layout manager and an adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweet.setLayoutManager(linearLayoutManager);
        rvTweet.setAdapter(adapter);

        client.getSearchTweets(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray usersArray = response.getJSONArray("statuses");
                    for (int i = 0; i < usersArray.length(); i++) {

                        // convert each object to a Tweet model
                        Tweet tweet = Tweet.fromJSON(usersArray.getJSONObject(i));

                        // add the tweet model to data source
                        tweetsSearch.add(tweet);

                        adapter.notifyItemInserted(tweetsSearch.size() - 1);
                        rvTweet.smoothScrollToPosition(0);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("DEBUG", tweetsSearch.toString());
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
