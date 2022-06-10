package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class User {

    public String id;
    public String name;
    public String screenName;
    public String publicImageUrl;
    public String profileBannerUrl;
    public String description;
    public String location;
    public int following;
    public int followers;

    public User() {
    }

    // Unpack user data from Json Object
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getString("id_str");
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.publicImageUrl = jsonObject.getString("profile_image_url_https");
        user.profileBannerUrl = jsonObject.getString("profile_banner_url");
        user.description = jsonObject.getString("description");
        user.location = jsonObject.getString("location");
        user.followers = jsonObject.getInt("followers_count");
        user.following = jsonObject.getInt("friends_count");

        return user;
    }

    public static List<User> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            users.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return users;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getPublicImageUrl() {
        return publicImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public String getLocation() {
        return location;
    }

    public int getFollowing() {
        return following;
    }

    public int getFollowers() {
        return followers;
    }
}
