package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class FollowersActivity extends AppCompatActivity {
    
    User user;
    String type;
    TwitterClient client;
    List<User> users;
    public static final String TAG = "FollowersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        client = TwitterApp.getRestClient(this);
        users = new ArrayList<User>();
        
        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        type = getIntent().getStringExtra("Type");
        populateList(user,type);
    }

    private void populateList(User user, String type) {
        Log.d(TAG,"populateList");
//        client.getFollowers(type, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                Log.i(TAG, "onSuccess! " + json.toString());
//                JSONArray jsonArray = json.jsonArray;
//                try {
//                    users.addAll(User.fromJsonArray(jsonArray));
//                    adapter.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    Log.e(TAG, "JSON Exception", e);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.e(TAG, "onFailure " + response, throwable);
//            }
//        });
    }
}