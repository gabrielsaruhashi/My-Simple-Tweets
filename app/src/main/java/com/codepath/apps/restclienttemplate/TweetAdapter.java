package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by gabesaruhashi on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    // list of tweets
    ArrayList<Tweet> tweets;

    // pass in the Tweets array in the constructor
    public TweetAdapter(ArrayList<Tweet> tweets) { this.tweets = tweets; };

    // initialize context
    Context context;

    // creates and inflates a new view; for each row, inflate the layout and cache references
    // into ViewHolder. Only invoked when you create new row
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // create the view using the item_tweet layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(tweetView);


    }

    // associates an inflated view to a new item / binds the values based on the position of the element
    // happens when user scrolls down, repopulates view
    @Override
    public void onBindViewHolder(TweetAdapter.ViewHolder holder, int position) {
        // get the tweet data at the specified position
        Tweet tweet = tweets.get(position);

        // populate the views
        String imageUrl = tweet.user.profileImageUrl;
        holder.tvUserName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);

        holder.tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvName.setText('@' + tweet.user.screenName);




        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))

                .into(holder.ivProfileImage);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // creates ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvRelativeTime;
        public TextView tvName;

        public ViewHolder(View itemView) {

            super(itemView);

            // lookup objects by view
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
            tvName = (TextView) itemView.findViewById(R.id.tvName);




        }
    }


    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    /* Swipe Refresher methods */
    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }





}
