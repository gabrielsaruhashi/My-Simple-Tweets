package com.codepath.apps.restclienttemplate.fragments.otherUserFrag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.FollowersAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.R.id.rvFollower;

/**
 * Created by gabesaruhashi on 7/5/17.
 */

public class FollowersListFragment extends TweetsListFragment {
    // create reference for twitter client
    private TwitterClient client;
    // the list of currently playing movies
    ArrayList<User> followers;
    // the adapter wired to the recycler view
    FollowersAdapter adapter;
    RecyclerView rvFollowers;


    public static FollowersListFragment newInstance(long uid) {
        FollowersListFragment followersListFragment = new FollowersListFragment();
        Bundle args = new Bundle();
        args.putLong("uid", uid);
        followersListFragment.setArguments(args);
        return followersListFragment;
    }

    /*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the client
        client = TwitterApplication.getRestClient();
        populateFollowerList();

    } */

    // inflation happens inside onCreateView
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragments_followers_list, container, false);
        // find the Recycler view
        rvFollowers = (RecyclerView) v.findViewById(rvFollower);
        // initialize the client
        client = TwitterApplication.getRestClient();
        // initialize the list of tweets
        followers = new ArrayList<>();
        // construct the adater from the data source
        adapter = new FollowersAdapter(followers);
        // recycler setup (connect a layout manager and an adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvFollowers.setLayoutManager(linearLayoutManager);
        rvFollowers.setAdapter(adapter);

        populateFollowerList();

        return v;
    }


    private void populateFollowerList() {
        // comes from the activity
        long uid = getArguments().getLong("uid");
        client.getFollowerList(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray usersArray = response.getJSONArray("users");
                    for (int i = 0; i < usersArray.length(); i++) {


                        // convert each object to a Tweet model
                        User follower = User.fromJSON(usersArray.getJSONObject(i));

                        // add the tweet model to data source
                        followers.add(follower);

                        // notify adapter that we've added an item
                        adapter.notifyItemInserted(followers.size() - 1);

                        rvFollowers.smoothScrollToPosition(0);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
