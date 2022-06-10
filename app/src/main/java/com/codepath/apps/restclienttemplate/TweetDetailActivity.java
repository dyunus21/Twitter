package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
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
        View view = binding.getRoot();
        setContentView(view);
        client = TwitterApp.getRestClient(this);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        Log.d(TAG, "tweet " + tweet);

        binding.tvName.setText(tweet.getUser().getName());
        binding.tvScreenName.setText(tweet.getUser().getScreenName());
        binding.tvBody.setText(tweet.getBody());
        Glide.with(this).load(tweet.getUser().getPublicImageUrl()).into(binding.ivProfileImage);
        if (tweet.mediaImageUrl != "None") {
            binding.ivMedia.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(tweet.mediaImageUrl)
                    .into(binding.ivMedia);
        }

        binding.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TweetDetailActivity.this, ComposeActivity.class);
                intent.putExtra("reply_to", Parcels.wrap(tweet));
                startActivity(intent);
            }
        });

        if (tweet.favorited)
            binding.ibLike.setBackgroundResource(R.drawable.ic_vector_heart);
        else
            binding.ibLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
        binding.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeTweet();
            }
        });

        if (tweet.isRetweeted())
            binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
        else
            binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
        binding.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retweet();
            }
        });

        binding.tvLikeCount.setText(String.valueOf(tweet.favoriteCount));
        binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));

    }

    public void toUserProfile(View view) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(tweet.getUser()));
        startActivity(intent);
    }

    public void likeTweet() {
        Log.d(TAG, "onLike " + tweet.id);

        String action = tweet.favorited ? "destroy" : "create";
        Log.d(TAG, action + tweet.favorited);

        client.likeTweet(tweet.id, action, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Favorited/Unfavorited tweet: " + tweet);
                if (tweet.favorited) {
                    tweet.favorited = false;
                    binding.ibLike.setBackgroundResource(R.drawable.ic_vector_heart);
                    tweet.favoriteCount -=1;
                    binding.tvLikeCount.setText(String.valueOf(tweet.favoriteCount));
                }
                else {
                    tweet.favorited = true;
                    binding.ibLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                    tweet.favoriteCount +=1;
                    binding.tvLikeCount.setText(String.valueOf(tweet.favoriteCount));
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure to favorite/unfavorite tweet!", throwable);
            }
        });
    }

    public void retweet() {
        Log.d(TAG, "onRetweet " + tweet.getId());

        String action = tweet.isRetweeted() ? "unretweet" : "retweet";
        Log.d(TAG, action + tweet.isRetweeted());

        client.reTweet(tweet.id, action, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, action + " tweet: " + tweet);
                if (tweet.isRetweeted()) {
                    tweet.retweeted = false;
                    binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                    tweet.retweetCount -=1;
                    binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
                }
                else {
                    tweet.retweeted = true;
                    binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                    tweet.retweetCount +=1;
                    binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure to " + action + " tweet!", throwable);
            }
        });
    }
}