package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";
    private final int REQUEST_CODE = 20;

    EditText etCompose;
    Button btnTweet;
//    ImageView ivProfileImage;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        // initialize views
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
//        ivProfileImage = findViewById(R.id.ivProfileImage);
//        Glide.with(this)
//                .load(tweet.user.publicImageUrl)
//                .into(ivProfileImage);
        client = TwitterApp.getRestClient(this);

        // Button onClickListener
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate tweetContent
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this,"Sorry, your tweet cannot be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this,"Sorry, your tweet is too long!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this,tweetContent, Toast.LENGTH_LONG).show();

                // Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, -2,new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG,"Published tweet says: " + tweet);
                            Intent intent = new Intent(ComposeActivity.this,TimelineActivity.class);
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // set result and bundle code for response
                            setResult(RESULT_OK,intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to publish tweet!", throwable);
                    }
                });

            }
        });

        // Return to Timeline onClickListener
        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}