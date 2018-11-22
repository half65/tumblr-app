package com.example.kesha.blog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

public class SplashScreen extends Activity {
    private SharedPreferences sPref;
    private String accessTokenKey;
    private String accessSecretTokenKey;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sPref = getSharedPreferences(Constants.S_PREF_NAME, MODE_PRIVATE);
                accessTokenKey = sPref.getString(Constants.KEY_ACCESS_TOKEN, null);
                accessSecretTokenKey = sPref.getString(Constants.KEY_ACCESS_SECRET_TOKEN, null);
                if (accessTokenKey != null || accessSecretTokenKey != null) {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashScreen.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);

    }


}