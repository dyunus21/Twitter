package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {

    public static final String TAG = "TweetDetailActivity";
    ActivityTweetDetailsBinding binding;

    Tweet tweet;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);
        client = TwitterApp.getRestClient(this);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        Log.d(TAG,"tweet "  + tweet);

        binding.tvName.setText(tweet.getUser().getName());
        binding.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        binding.tvBody.setText(tweet.getBody());
        Glide.with(this).load(tweet.getUser().getPublicImageUrl()).into(binding.ivProfileImage);
        if(tweet.mediaImageUrl != "None") {
            binding.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(tweet.mediaImageUrl)
                    .into(binding.ivMedia);
        }

        if(tweet.favorited)
            binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
        else
            binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);

        // Like/Unlike Tweet
        binding.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onLike " + tweet.id);

                String action = tweet.favorited ? "destroy" : "create";
                Log.d(TAG, action + tweet.favorited);

                client.likeTweet(tweet.id, action, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Favorited/Unfavorited tweet: " + tweet);
                        if(tweet.favorited)
                            binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
                        else
                            binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to favorite/unfavorite tweet!", throwable);
                    }
                });
            }
        });

        // Retweet
        if(tweet.isRetweeted())
            binding.btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
        else
            binding.btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
        binding.btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onRetweet " + tweet.getId());

                String action = tweet.isRetweeted() ? "unretweet" : "retweet";
                Log.d(TAG,action + tweet.isRetweeted());

                client.reTweet(tweet.id,action, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG,action + " tweet: " + tweet);
                        if(tweet.isRetweeted())
                            binding.btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                        else
                            binding.btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to " + action + " tweet!", throwable);
                    }
                });
            }
        });

    }
}