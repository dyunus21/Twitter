package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
@Parcel
public class Tweet {

    public static final String TAG = "Tweet";

    // Define Tweet variables
    public String body;
    public String createdAt;
    public User user;
//    public String mediaImageUrl;

    public Tweet(){}
    // Unpack Tweet data from JsonObject
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
//        Log.d("Tweet","tweet: " + tweet);
//        tweet.body = jsonObject.getString("full_text");
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
//        Log.d("User", "Entities " + ((jsonObject.getJSONObject("entities"))));
//        tweet.mediaImageUrl = jsonObject.getJSONObject("entities").getJSONArray("media")
//                    .getJSONObject(0).getString("media_url_https");
//        Log.d(TAG, tweet.mediaImageUrl);
        return tweet;
    }

    // Gets list of tweets
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException{
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i<jsonArray.length();i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    // Getters
    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }
}
