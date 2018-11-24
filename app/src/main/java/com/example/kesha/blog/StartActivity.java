package com.example.kesha.blog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = StartActivity.class.getSimpleName();

    private static final int REQUEST_CODE_AUTH = 33;

    private OAuth10aService oAuthService;
    public OAuth1RequestToken requestToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        findViewById(R.id.registration_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.registration_btn).setClickable(false);
                authentication();
            }
        });
    }

    private void authentication() {
        oAuthService = new ServiceBuilder(Constants.consumerKey)
                .apiSecret(Constants.consumerSecret)
                .callback(Constants.callbackURL)
                .build(TumblrApi.instance());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestToken = oAuthService.getRequestToken();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                gotoWebView(oAuthService.getAuthorizationUrl(requestToken));
            }
        }).start();

    }

    private void gotoWebView(final String authUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.ARG_AUTH_URL, authUrl);
                startActivityForResult(intent, REQUEST_CODE_AUTH);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTH && resultCode == Activity.RESULT_OK && data != null) {
            processVerifier(data.getStringExtra(WebViewActivity.RES_AUTH_VERIFIER));
        }
    }

    private void processVerifier(final String oauthVerifier) {
        Log.e(TAG, "processVerifier: " + oauthVerifier);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OAuth1AccessToken oauthAccessToken = null;
                try {
                    if (requestToken != null) {
                        oauthAccessToken = oAuthService.getAccessToken(requestToken, oauthVerifier);
                        Log.e(TAG, "oauth token: " + oauthAccessToken);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (oauthAccessToken != null) {
                    final String token = oauthAccessToken.getToken();
                    final String secret = oauthAccessToken.getTokenSecret();

                    if (token != null && secret != null) {
                        TumblrApplication.initJumblrClient(token, secret);
                        PreferencesStorage.saveAccessToken(token);
                        PreferencesStorage.saveTokenSecret(secret);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
