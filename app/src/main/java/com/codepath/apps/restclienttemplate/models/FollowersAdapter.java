package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.ProfileActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by gabesaruhashi on 6/30/17.
 */

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {
    // list of followers
    ArrayList<User> followers;
    // pass in the Tweets array in the constructor
    public FollowersAdapter(ArrayList<User> followers) { this.followers = followers; };
    Context context;
    // create reference for twitter client
    private TwitterClient client;

    // create the follower reference
    User follower;

    @Override
    public FollowersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // initialize the client
        client = TwitterApplication.getRestClient();

        // create the view using the item_tweet layout
        View followerView = inflater.inflate(R.layout.item_follower, parent, false);
        return new ViewHolder(followerView);
    }

    @Override
    public void onBindViewHolder(FollowersAdapter.ViewHolder holder, int position) {
        follower = followers.get(position);
        holder.tvName.setText(follower.name);
        holder.tvUserName.setText(follower.screenName);
        String imageUrl = follower.profileImageUrl;

        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // track view objects
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intentDetail = new Intent(context, ProfileActivity.class);
            intentDetail.putExtra("screen_name", follower.screenName);
            context.startActivity(intentDetail);
        }
    }

}
