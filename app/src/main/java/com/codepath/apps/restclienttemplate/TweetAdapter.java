package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.content.DialogInterface.BUTTON_POSITIVE;

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
    // create reference for twitter client
    private TwitterClient client;


    // creates and inflates a new view; for each row, inflate the layout and cache references
    // into ViewHolder. Only invoked when you create new row
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // initialize the client
        client = TwitterApplication.getRestClient();

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
        String entityUrl = tweet.mediaImageUrl;
        holder.tvUserName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvName.setText('@' + tweet.user.screenName);

        if (tweet.favorited) {
            holder.ivFavorite.setColorFilter(ContextCompat.getColor(context,R.color.medium_green));
        }

        if (tweet.retweeted) {
            holder.ivRetweet.setColorFilter(ContextCompat.getColor(context,R.color.medium_green));
        }


        // load user profile image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .into(holder.ivProfileImage);

        // load user profile image using glide
        Glide.with(context)
                .load(entityUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                .into(holder.ivEntityMedia);


    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // creates ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // track view objects
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvBody;
        public TextView tvRelativeTime;
        public TextView tvName;
        public ImageView ivReply;
        public ImageView ivFavorite;
        public ImageView ivRetweet;
        public ImageView ivEntityMedia;


        public ViewHolder(View itemView) {
            super(itemView);
            // lookup objects by view
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);

            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
            ivEntityMedia = (ImageView) itemView.findViewById(R.id.ivEntityMedia);

            // for reply activity
            ivReply.setOnClickListener(this);

            // for details activity
            itemView.setOnClickListener(this);

            // for retweet
            ivRetweet.setOnClickListener(this);
            ivFavorite.setOnClickListener(this);

            // for follower list
            tvUserName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                Tweet tweet = tweets.get(position);

                // set up switch to manage the different click listeners
                switch (v.getId()) {
                    // when reply is clicked
                    case R.id.ivReply:
//                        Intent intentReply = new Intent(context, ComposeActivity.class);
//                        intentReply.putExtra("Screen Name", tvName.getText().toString());
//                        context.startActivity(intentReply);
                        showAlertDialogForCompose(tweet);
                        break;
                    case R.id.ivRetweet:
                        retweet(tweet.uid, ivRetweet);
                        tweet.retweeted = true;
                        break;
                    case R.id.ivFavorite:
                        if (tweet.favorited) {
                            toUnfavorite(tweet.uid, ivFavorite);
                            tweet.favorited = false;
                        } else {
                            toFavorite(tweet.uid, ivFavorite);
                            tweet.favorited = true;
                        }
                        break;

                    case R.id.tvUserName:
                        Intent intentFollower = new Intent(context, FollowerListActivity.class);
                        intentFollower.putExtra(User.class.getSimpleName(), tweet.user.uid);
                        context.startActivity(intentFollower);
                        break;

                    // when itemView click
                    default:
                        Intent intentDetail = new Intent(context, TweetDetailsActivity.class);

                        intentDetail.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        context.startActivity(intentDetail);
                        break;
                }

            }

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

    public void retweet(long tweetId, final ImageView imageRetweet) {
        // retweets
        client.doRetweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                imageRetweet.setColorFilter(ContextCompat.getColor(context,R.color.medium_green));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

        });
    }

    public void toFavorite(long tweetId, final ImageView tvFavoriteCount) {
        client.createFavorite(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                tvFavoriteCount.setColorFilter(ContextCompat.getColor(context,R.color.medium_green));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void toUnfavorite(long tweetId, final ImageView tvFavoriteCount) {
        client.destroyFavorite(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                tvFavoriteCount.setColorFilter(ContextCompat.getColor(context,R.color.medium_gray));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    // Display the alert that pops up the model overlay for reply
    private void showAlertDialogForCompose(final Tweet tweet) {
        // inflate message_item.xml view
        View messageView = LayoutInflater.from(context).
                inflate(R.layout.fragment_compose, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(messageView);

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final int MAX_CHAR = 140;


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // set button colors
                alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.twitter_blue));
                alertDialog.getButton(BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.white));

                // get views
                EditText etTweetBody = (EditText) alertDialog.findViewById(R.id.etTweetBody);
                TextView tvSupportReply = (TextView) alertDialog.findViewById(R.id.tvSupportReply);
                TextView tvName = (TextView) alertDialog.findViewById(R.id.tvName);
                TextView tvScreenName = (TextView) alertDialog.findViewById(R.id.tvScreenName);
                ImageView ivProfileImage = (ImageView) alertDialog.findViewById(R.id.ivProfileImage);
                final TextView tvCharCount = (TextView) alertDialog.findViewById(R.id.tvCharCount);

                etTweetBody.setText('@' + tweet.user.screenName);
                tvSupportReply.setText("In reply to " + tweet.user.screenName);
                tvCharCount.setText(String.valueOf(MAX_CHAR - etTweetBody.getText().toString().length()));
                tvName.setText(tweet.user.name);
                tvScreenName.setText(tweet.user.screenName);

                // load user profile image using glide
                Glide.with(context)
                        .load(tweet.user.profileImageUrl)
                        .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                        .into(ivProfileImage);


                // add text listner
                etTweetBody.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        tvCharCount.setText(String.valueOf(MAX_CHAR - s.length()));

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (MAX_CHAR - s.length() < 0) {
                            tvCharCount.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                            alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.twitter_blue_50));
                        }

                        if (MAX_CHAR - s.length() > 0) {
                            tvCharCount.setTextColor(ContextCompat.getColor(context, R.color.medium_gray));
                            alertDialog.getButton(BUTTON_POSITIVE).setBackgroundColor(ContextCompat.getColor(context, R.color.twitter_blue));
                        }
                    }

                });

            }
        });

        // Configure dialog button (OK)
        alertDialog.setButton(BUTTON_POSITIVE, "Tweet",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Extract content from alert dialog
                        String stringBody = ((EditText) alertDialog.findViewById(R.id.etTweetBody)).getText().toString();

                        // Posts Tweet
                        postReplyTweet(stringBody);
                    }
                });

        // Configure dialog button (Cancel)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                });

        // Display the dialog
        alertDialog.show();
    }


    public void postReplyTweet(String message) {
        client.sendTweet(message, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet newTweet = Tweet.fromJSON(response);
                    tweets.add(newTweet);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }






}
