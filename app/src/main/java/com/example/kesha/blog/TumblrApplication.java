package com.example.kesha.blog;

import android.app.Application;

import com.example.kesha.blog.UtilsPackage.Constants;
import com.example.kesha.blog.UtilsPackage.PreferencesStorage;
import com.tumblr.jumblr.JumblrClient;

public class TumblrApplication extends Application {

    private static JumblrClient client;

    public static void initJumblrClient(String token, String secret) {
        client = new JumblrClient(Constants.consumerKey, Constants.consumerSecret);
        client.setToken(token, secret);
    }

    public static JumblrClient getClient() {
        return client;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferencesStorage.setAppContext(getApplicationContext());
    }
}
