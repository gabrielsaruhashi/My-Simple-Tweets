package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.otherUserFrag.OtherUserPagerAdapter;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;

    private ViewPager vpPager;
    private OtherUserPagerAdapter otherUserPagerAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");
        long uid = getIntent().getLongExtra("uid", 0);


        client = TwitterApplication.getRestClient();

        // in case the intent comes from an image click
        if (screenName != null) {
            client.getOtherUserInfo(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        // deserialize the user object
                        user = User.fromJSON(response);

                        setupView(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        // in case intent comes from menu click
        } else {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        // deserialize the user object
                        user = User.fromJSON(response);

                        setupView(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // instantiate pageviewer and tab layou

        // get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        otherUserPagerAdapter = new OtherUserPagerAdapter(getSupportFragmentManager(), screenName, uid, this);
        // set the adapter
        vpPager.setAdapter(otherUserPagerAdapter);

        // setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

    }

    public void setupView(User user) {
        // set the tile of the Action bar based on the user information
        getSupportActionBar().setTitle(user.screenName);

        // populate the user headline
        populateUserheadline(user);
    }

    public void populateUserheadline(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName.setText(user.name);

        tvTagline.setText(user.tagline);
        tvFollowers.setText(user.followersCount + " Followers");
        tvFollowing.setText(user.followingCount + " Following");
        // load profile image with glide
        Glide.with(this)
                .load(user.profileImageUrl)
                .into(ivProfileImage);

    }
}
