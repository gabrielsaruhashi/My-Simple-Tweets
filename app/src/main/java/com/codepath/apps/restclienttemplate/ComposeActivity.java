package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;


public class ComposeActivity extends AppCompatActivity {

    private Button btTweet;
    private TwitterClient client;
    private EditText tentativeMessage;
    private Tweet newTweet;
    private String screenName;

    // char count
    private TextView tvCharCount;


    // reference for possible newTweet;
    private final int RESULT_OK = 10;
    private final int MAX_CHAR = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient();
        btTweet = (Button) findViewById(R.id.btTweet);
        tentativeMessage =  (EditText) findViewById(R.id.etTweetBody);
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        screenName = (String) getIntent().getStringExtra("Screen Name");


        if (screenName != null) {
            tentativeMessage.setText(screenName);
        }

        // add text listner
        tentativeMessage.addTextChangedListener(new TextWatcher() {

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
                    tvCharCount.setTextColor(ContextCompat.getColor(ComposeActivity.this, R.color.colorAccent));
                    btTweet.setBackgroundColor(ContextCompat.getColor(ComposeActivity.this, R.color.twitter_blue_30));

                }

            }

        });

    }

    private void postTweet(String message) {
        client.sendTweet(message, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    newTweet = Tweet.fromJSON(response);

                    // Prepare data intent
                    Intent intent = new Intent();
                    // Pass relevant data back as a result
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(newTweet));
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, intent); // set result code and bundle data for response
                    finish(); // closes the activity, pass data to parent

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


    public void onSubmit(View v) {
        postTweet(tentativeMessage.getText().toString());
    }




}
