package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.DividerItemDecoration;
import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.R.id.rvTweet;

/**
 * Created by gabesaruhashi on 7/3/17.
 */

public class TweetsListFragment extends Fragment {

    // list of tweets
    ArrayList<Tweet> tweets;
    // recycler view
    RecyclerView rvTweets;
    // the adapter wired to the new view
    TweetAdapter adapter;

    // setup swipe thing
    private SwipeRefreshLayout swipeContainer;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    // store the lowest max id
    private long lowMaxId;

    // create reference for twitter client
    private TwitterClient client;

    private int page;

    // inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        // find the Recycler view
        rvTweets = (RecyclerView) v.findViewById(rvTweet);

        // initialize the client
        client = TwitterApplication.getRestClient();

        // initialize the list of tweets
        tweets = new ArrayList<>();
        // construct the adater from the data source
        adapter = new TweetAdapter(tweets);
        // recycler setup (connect a layout manager and an adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(page);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Infinite Pagination. Retain an instance so that you can call 'resetState() for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                client.getHomeTimeline(lowMaxId, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        addItems(response);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("OnLoadMore failure", responseString);
                        throwable.printStackTrace();
                    }
                });
            }
        };

        // adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        return v;
    }

    public void addItems(JSONArray response) {
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


    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        if (page == 0) {
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
        } else {
            client.geMentionsTimeline(new JsonHttpResponseHandler() {
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
    };

    public void setPage(int page) { this.page = page;}

    public void addTweet(Tweet tweet) {
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }
}
