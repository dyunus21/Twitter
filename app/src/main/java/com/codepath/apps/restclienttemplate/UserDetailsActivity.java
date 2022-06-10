package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityUserDetailsBinding;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class UserDetailsActivity extends AppCompatActivity {

    public static final String TAG = "UserDetailActivity";
    ActivityUserDetailsBinding binding;

    User user;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        client = TwitterApp.getRestClient(this);
        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        binding.tvName.setText(user.getName());
        binding.tvScreenName.setText("@" + user.getScreenName());
        binding.tvDescription.setText(user.getDescription());
        binding.tvFollowing.setText(user.getFollowing() + " Following");
        binding.tvFollowers.setText(user.getFollowers() + " Followers");
        binding.tvLocation.setText(user.getLocation());

        Glide.with(this).load(user.getPublicImageUrl()).into(binding.ivProfileImage);
        Glide.with(this).load(user.getProfileBannerUrl()).into(binding.ivBackgroundImage);

        binding.tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "followers");
                Intent intent = new Intent(UserDetailsActivity.this,FollowersActivity.class);
                intent.putExtra(User.class.getSimpleName(),Parcels.wrap(user));
                intent.putExtra("Type", "followers");
                startActivity(intent);
            }
        });

        binding.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"follow " + user.id);

//                String action = tweet.favorited ? "destroy" : "create";
//                Log.d(TAG,action + tweet.favorited);
                // CHANGE LATER
                client.follow(user.getId(), "destroy", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        binding.btnFollow.setText("Follow");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"onFailure to follow user!", throwable);
                    }
                });
            }
        });

    }
}