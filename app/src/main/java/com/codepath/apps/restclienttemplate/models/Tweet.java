package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gabesaruhashi on 6/26/17.
 */

@org.parceler.Parcel
public class Tweet {

    // list out the attributes
    public String body;
    public long uid; // databse ID for the tweet
    public String createdAt;
    public int retweetCount;
    public int favouriteCount;
    public User user;
    public boolean retweeted;
    public boolean favorited;
    public String mediaImageUrl;

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favouriteCount = jsonObject.getInt("favorite_count");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");

        // media folder
        JSONObject entities = jsonObject.getJSONObject("entities");

        try {
            if (entities.getJSONArray("media").length() > 0) {
                tweet.mediaImageUrl = entities.getJSONArray("media").getJSONObject(0).getString("media_url_https");
        }

        } catch (JSONException e) {
            tweet.mediaImageUrl = null;
        }

        // get media
        return tweet;
    }

    Tweet() {}



}

