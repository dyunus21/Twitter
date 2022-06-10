package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    public static final String TAG = "TweetsAdapter";
    public static final int REQUEST_CODE = 20;
    Context context;
    List<Tweet> tweets;
    ItemTweetsBinding binding;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemTweetsBinding.inflate(LayoutInflater.from(context), parent, false);
        View view = binding.getRoot();
        return new ViewHolder(view);
    }

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TwitterClient client;

        public ViewHolder(@NonNull View item_view) {
            super(item_view);
            client = TwitterApp.getRestClient(context);
            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvScreenName.setText(tweet.getUser().getScreenName());
            binding.tvName.setText(tweet.user.name);
            Glide.with(context).load(tweet.user.publicImageUrl).into(binding.ivProfileImage);
            binding.tvTimestamp.setText("· " + tweet.timestamp);

            if (tweet.mediaImageUrl != "None") {
                binding.ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.mediaImageUrl).into(binding.ivMedia);
            } else
                binding.ivMedia.setVisibility(View.GONE);
            binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Directed to " + tweet.getUser().getName() + "'s profile");
                    toUserProfile(tweet.getUser());
                }
            });

            if (tweet.isFavorited())
                binding.ibLike.setBackgroundResource(R.drawable.ic_vector_heart);
            else
                binding.ibLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
            binding.ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likeTweet(tweet);
                }
            });
            binding.tvLikeCount.setText(String.valueOf(tweet.favoriteCount));

            if (tweet.isRetweeted())
                binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
            else
                binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
            binding.ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retweet(tweet);
                }
            });
            binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));

            binding.ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ComposeActivity.class);
                    intent.putExtra("reply_to", Parcels.wrap(tweet));
                    ((Activity) context).startActivityForResult(intent,TimelineActivity.REQUEST_CODE);
                }
            });
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
            intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
            context.startActivity(intent);
        }

        public void likeTweet(Tweet tweet) {
            Log.d(TAG, "onLike " + tweet.id);

            String action = tweet.favorited ? "destroy" : "create";
            Log.d(TAG, action + tweet.favorited);

            client.likeTweet(tweet.id, action, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, "Favorited/Unfavorited tweet: " + tweet);
                    if (tweet.favorited) {
                        tweet.favorited = false;
                        binding.ibLike.setImageDrawable(context.getDrawable(R.drawable.ic_vector_heart));
                        tweet.favoriteCount -=1;
                        binding.tvLikeCount.setText(String.valueOf(tweet.favoriteCount));
                    }
                    else {
                        tweet.favorited = true;
                        binding.ibLike.setImageDrawable(context.getDrawable(R.drawable.ic_vector_heart_stroke));
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

        public void retweet(Tweet tweet) {
            Log.d(TAG, "onRetweet " + tweet.getId());

            String action = tweet.isRetweeted() ? "unretweet" : "retweet";
            Log.d(TAG, action + tweet.isRetweeted());

            client.reTweet(tweet.id, action, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.i(TAG, action + " tweet: " + tweet);
                    if (tweet.isRetweeted()) {
                        tweet.retweeted = false;
                        binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                        tweet.retweetCount -=1;
                        binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
                    }
                    else {
                        tweet.retweeted = true;
                        binding.ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
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


}
