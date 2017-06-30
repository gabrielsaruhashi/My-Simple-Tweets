package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.models.FollowersAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class FollowerListActivity extends AppCompatActivity {

    // create reference for twitter client
    private TwitterClient client;
    // the list of currently playing movies
    ArrayList<User> followers;

    // the recycler view
    @BindView(R.id.rvFollower) RecyclerView rvFollowers;
    // the adapter wired to the recycler view
    FollowersAdapter adapter;
    // original user id
    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_list);
        ButterKnife.bind(this);
        // initialize the client
        client = TwitterApplication.getRestClient();

        // initialize the list of movies
        followers = new ArrayList<>();
        // initialize the adapter -- movies array cannot be reinitialized after this point
        adapter = new FollowersAdapter(followers);
        // resolve the recycler view and connect a layout manager and the adapter
        rvFollowers.setLayoutManager(new LinearLayoutManager(this));
        rvFollowers.setAdapter(adapter);

        userId = (long) getIntent().getLongExtra(User.class.getSimpleName(), 1l);
        populateFollowerList(userId);
    }

    private void populateFollowerList(Long userId) {
        client.getFollowerList(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // iterate through the results
                try {
                    JSONArray usersArray = response.getJSONArray("users");
                    for (int i = 0; i < usersArray.length(); i++) {

                            // for each entry, deserialize the JSON object
                            JSONObject json = usersArray.getJSONObject(i);

                            // convert each object to a Tweet model
                            User follower = User.fromJSON(usersArray.getJSONObject(i));

                            // add the tweet model to data source
                            followers.add(follower);

                            // notify adapter that we've added an item
                            adapter.notifyItemInserted(followers.size() - 1);

                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }
        });
    }


}
