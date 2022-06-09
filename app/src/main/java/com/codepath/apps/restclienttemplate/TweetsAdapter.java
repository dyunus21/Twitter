package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    Context context;
    List<Tweet> tweets;

    public static final String TAG = "TweetsAdapter";
    public static final int REQUEST_CODE = 20;


    // Press in the contacts and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate a layout for a tweet
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweets,parent, false);
        return new ViewHolder(view);
    }

    // bind value based on position of element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // bind the data to viewholder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivProfileImage;
        TextView tvScreenName;
        TextView tvBody;
        ImageView ivMedia;
        TextView tvTimestamp;
        TextView tvName;
        Button btnReply;
//        Button btnRetweet;
        Button btnLike;
//        Button btnShare;
        TwitterClient client;
//        TwitterApp

        public ViewHolder(@NonNull View item_view) {
            super(item_view);
            client = TwitterApp.getRestClient(context);
            ivProfileImage = item_view.findViewById(R.id.ivProfileImage);
            tvScreenName = item_view.findViewById(R.id.tvScreenName);
            tvName = item_view.findViewById(R.id.tvName);
            tvBody = item_view.findViewById(R.id.tvBody);
            tvTimestamp = item_view.findViewById(R.id.tvTimestamp);
            ivMedia = item_view.findViewById(R.id.ivMedia);

            btnLike = (Button) item_view.findViewById(R.id.btnLike);
            btnReply = (Button) item_view.findViewById(R.id.btnReply);


//            // Reply to Tweet
//            btnReply = item_view.findViewById(R.id.btnReply);
//            btnReply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG,"onReply");
//                    return;
//                }
//            });
//
//            // Retweet Tweet
//            btnRetweet = item_view.findViewById(R.id.btnRetweet);
//            btnRetweet.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG,"onRetweet");
//                    return;
//
//                }
//            });

//            // Like Tweet
//            btnLike = (Button) item_view.findViewById(R.id.btnLike);
//            btnLike.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG,"onLike");
//                    // Make an API call to Twitter to publish the tweet
//                    client.likeTweet(, new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Headers headers, JSON json) {
//                            try {
//                                Tweet tweet = Tweet.fromJson(json.jsonObject);
//                                Log.i(TAG,"Published tweet says: " + tweet);
//                                Intent intent = new Intent(ComposeActivity.this,TimelineActivity.class);
//                                intent.putExtra("tweet", Parcels.wrap(tweet));
//                                // set result and bundle code for response
//                                setResult(RESULT_OK,intent);
//                                finish();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                            Log.e(TAG,"onFailure to publish tweet!", throwable);
//                        }
//                    });
//                }
//            });

//            // Share tweet
//            btnShare = item_view.findViewById(R.id.btnShare);
//            btnShare.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG,"onShare");
//                    return;
//                }
//            });

            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvName.setText(tweet.user.name);
//            Log.d("TweetsAdapter",tweet.user.name);
            Glide.with(context)
                    .load(tweet.user.publicImageUrl)
                    .into(ivProfileImage);
            tvTimestamp.setText(tweet.timestamp);

            // If media exists in tweet, set media to be visible
            if(tweet.mediaImageUrl != "None") {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.mediaImageUrl)
                        .into(ivMedia);
            }


            // Like Tweet

            btnLike.setOnClickListener(new View.OnClickListener() {
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
                                btnLike.setBackgroundColor(Color.parseColor("#ff0000"));
                            else
                                btnLike.setBackgroundColor(R.drawable.ic_vector_heart_stroke);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG,"onFailure to favorite/unfavorite tweet!", throwable);
                        }
                    });

//                    // Make an API call to Twitter to publish the tweet
//                    client.likeTweet(tweet.id, new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Headers headers, JSON json) {
//                                Log.i(TAG,"Favorited tweet: " + tweet);
//                                btnLike.setBackgroundColor(Color.parseColor("#ff0000"));
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                            Log.e(TAG,"onFailure to favorite tweet!", throwable);
//                        }
//                    });
                }
            });

//            btnReply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG, "onReply ");
//                    Intent intent = new Intent(context,ComposeActivity.class);
//                    startActivityForResult(intent, REQUEST_CODE);
//                    return true;
//                }
//            });

        }

        @Override
        public void onClick(View v) {
            int position = getBindingAdapterPosition();
            Log.d(TAG, "inOnClick!");
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, TweetDetailActivity.class);
                // serialize the movie using parceler, use its short name as a key

                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // show the activity
                context.startActivity(intent);
            }
        }

    }


}
