package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityUserDetailsBinding;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

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
        binding.tvScreenName.setText(user.getScreenName());
        binding.tvDescription.setText(user.getDescription());
        binding.tvFollowing.setText(user.getFollowing() + " Following");
        binding.tvFollowers.setText(user.getFollowers() + " Followers");
        binding.tvLocation.setText(user.getLocation());

        Glide.with(this).load(user.getPublicImageUrl()).into(binding.ivProfileImage);
        Glide.with(this).load(user.getProfileBannerUrl()).into(binding.ivBackgroundImage);

    }
}