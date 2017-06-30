package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailsActivity extends AppCompatActivity {

    // the movie to display
    Tweet tweet;
    // create reference for twitter client
    private TwitterClient client;

    // the view objects
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
    @BindView(R.id.tvRetweetCount) TextView tvRetweetCount;
    @BindView(R.id.tvFavoriteCount) TextView tvFavoriteCount;
    @BindView(R.id.ivFavorite) ImageView ivFavorite;
    @BindView(R.id.ivRetweet) ImageView ivRetweet;
    boolean favorited;
    boolean retweeted;
    String imageUrl;

    String mediaEntityUrl;
    @BindView(R.id.ivEntityMedia) ImageView ivEntityMedia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);

        // initialize the client
        client = TwitterApplication.getRestClient();
        // unwrap parcel
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        // populate views
        imageUrl = tweet.user.profileImageUrl;
        tvUserName.setText(tweet.user.screenName);
        tvName.setText('@' + tweet.user.name);
        tvBody.setText(tweet.body);
        tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt));
        tvRetweetCount.setText(Integer.toString(tweet.retweetCount));
        tvFavoriteCount.setText(Integer.toString(tweet.favouriteCount));
        mediaEntityUrl = tweet.mediaImageUrl;

        // booleans for color picking
        favorited = tweet.favorited;
        retweeted = tweet.retweeted;

        // load image using glide
        Glide.with(TweetDetailsActivity.this)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(TweetDetailsActivity.this, 15, 0))
                .into(ivProfileImage);

        if (mediaEntityUrl != null) {
            // load image using glide
            Glide.with(TweetDetailsActivity.this)
                    .load(mediaEntityUrl)
                    .bitmapTransform(new RoundedCornersTransformation(TweetDetailsActivity.this, 15, 0))
                    .into(ivEntityMedia);
        }

        // check favorited to change color
        if (favorited) {
            ivFavorite.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this,R.color.medium_green));
        } else {
            ivFavorite.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this,R.color.medium_gray_50));
        }

        if (retweeted) {
            ivRetweet.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this,R.color.medium_green));
        } else {
            ivRetweet.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this,R.color.medium_gray_50));
        }


        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favorited) {
                    toUnfavorite(tweet.uid);
                    favorited = false;
                    ivFavorite.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this,R.color.medium_gray_50));
                    tweet.favorited = false;
                } else {
                    toFavorite(tweet.uid);
                    favorited = true;
                    ivFavorite.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this,R.color.medium_green));
                    tweet.favorited = true;
                }
            }
        });
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

    public void retweet(long tweetId) {
        // retweets
        client.doRetweet(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet updatedTweet = Tweet.fromJSON(response);
                    tvRetweetCount.setText(Integer.toString(updatedTweet.retweetCount));
                    ivRetweet.setColorFilter(ContextCompat.getColor(TweetDetailsActivity.this,R.color.medium_green));
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

        });
    }

    public void toFavorite(long tweetId) {
        client.createFavorite(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet updatedTweet = Tweet.fromJSON(response);
                    tvFavoriteCount.setText(Integer.toString(updatedTweet.favouriteCount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void toUnfavorite(long tweetId) {
        client.destroyFavorite(tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet updatedTweet = Tweet.fromJSON(response);
                    tvFavoriteCount.setText(Integer.toString(updatedTweet.favouriteCount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void onRetweeting(View v) {
        retweet(tweet.uid);
    }

    public void onReplying(View v) {
        Intent intentReply = new Intent(this, ComposeActivity.class);
        intentReply.putExtra("Screen Name", tvName.getText().toString());
        this.startActivity(intentReply);
    }

}
