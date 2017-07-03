package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    // inflation happens inside onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        // find the Recycler view
        rvTweets = (RecyclerView) v.findViewById(rvTweet);

        // initialize the list of tweets
        tweets = new ArrayList<>();
        // construct the adater from the data source
        adapter = new TweetAdapter(tweets);
        // recycler setup (connect a layout manager and an adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

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

}
