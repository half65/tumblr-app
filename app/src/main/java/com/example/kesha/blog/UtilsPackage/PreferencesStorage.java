package com.example.kesha.blog.UtilsPackage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kesha.blog.TumblrApplication;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesStorage {
    private static final String PREFS_NAME = TumblrApplication.class.getSimpleName();
    private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    private static final String KEY_TOKEN_SECRET = "KEY_TOKEN_SECRET";
    private static Context appContext;

    public static void setAppContext(Context context) {
        appContext = context;
    }

    public static String getAccessToken() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public static String getTokenSecret() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN_SECRET, null);
    }

    public static void saveAccessToken(String accessToken){
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public static void saveTokenSecret(String secret){
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN_SECRET, secret).apply();
    }
}
