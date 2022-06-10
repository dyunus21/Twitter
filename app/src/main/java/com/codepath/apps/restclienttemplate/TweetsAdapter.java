package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityUserDetailsBinding;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    Context context;
    List<Tweet> tweets;

    public static final String TAG = "TweetsAdapter";
    public static final int REQUEST_CODE = 20;
    ItemTweetsBinding binding;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemTweetsBinding.inflate(LayoutInflater.from(context),parent, false);
        View view = binding.getRoot();
        return new ViewHolder(view);
    }

    // bind value based on position of element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TwitterClient client;

        public ViewHolder(@NonNull View item_view) {
            super(item_view);
            client = TwitterApp.getRestClient(context);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText("@" + tweet.user.screenName);
            binding.tvName.setText(tweet.user.name);
            Glide.with(context)
                    .load(tweet.user.publicImageUrl)
                    .into(binding.ivProfileImage);
            binding.tvTimestamp.setText(tweet.timestamp);

            // If media exists in tweet, set media to be visible
            if(tweet.mediaImageUrl != "None") {
                binding.ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.mediaImageUrl)
                        .into(binding.ivMedia);
            }

            binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"Directed to " + tweet.getUser().getName() + "'s profile");
                    toUserProfile(tweet.getUser());
                }
            });


            // Like Tweet

            if(tweet.favorited)
                binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
            else
                binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);

            binding.btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"onLike " + tweet.id);

                    String action = tweet.favorited ? "destroy" : "create";
                    Log.d(TAG,action + tweet.favorited);

                    client.likeTweet(tweet.id,action, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG,"Favorited/Unfavorited tweet: " + tweet);
                            if(tweet.favorited)
                                binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
                            else
                                binding.btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG,"onFailure to favorite/unfavorite tweet!", throwable);
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


//            binding.btnReply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG,"onReply " + tweet.getId());
//
//                    Intent intent = new Intent(context, ComposeActivity.class);
//
//                }
//            });


        }


        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            Log.d(TAG, "inOnClick!");
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetailActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }

        public void toUserProfile(User user) {
            Intent intent = new Intent(context, UserDetailsActivity.class);
            intent.putExtra(User.class.getSimpleName(),Parcels.wrap(user));
            context.startActivity(intent);
        }



    }


}
