package com.example.kesha.blog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kesha.blog.Fragments.FragmentFollowers;
import com.example.kesha.blog.Fragments.FragmentFollowing;
import com.example.kesha.blog.Fragments.FragmentSearch;
import com.example.kesha.blog.Fragments.InfoFragment;
import com.example.kesha.blog.Fragments.postListFragment;
import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.seratch.signedrequest4j.HttpResponse;
import com.github.seratch.signedrequest4j.OAuthAccessToken;
import com.github.seratch.signedrequest4j.OAuthConsumer;
import com.github.seratch.signedrequest4j.SignedRequest;
import com.github.seratch.signedrequest4j.SignedRequestFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_AUTH = 33;

    private final String consumerKey = "yuWfjh1UkS8UgAZWmjOPHQMmoJOXUXilND42nZTYYstohhxKsE";
    private final String consumerSecret = "X3B5Pm1xuqqerpLdqXFeozYJZNVnkvNAzK43UmkXHHMerHBlt8";
    private final String callbackURL = "http://www.tumblr.com/connect/login_success.html";

    private String accessTokenKey;
    private String accessSecretTokenKey;

    private OAuth10aService oAuthService;
    private OAuth1RequestToken requestToken;

    private int[] tabIcons = {
            R.drawable.ic_view_post_icon_black_24dp,
            R.drawable.ic_info_black_24dp,
            R.drawable.ic_favorite_black_24dp,
            R.drawable.ic_subscribers_black_24dp,
            R.drawable.ic_find_in_tags_black_24dp};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
        authentication();
    }




        private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new postListFragment(), "posts");
        adapter.addFragment(new InfoFragment(), "info");
        adapter.addFragment(new FragmentFollowing(), "following");
        adapter.addFragment(new FragmentFollowers(), "followers");
        adapter.addFragment(new FragmentSearch(), "search");
        viewPager.setAdapter(adapter);
    }


    private void authentication() {
        oAuthService = new ServiceBuilder(consumerKey)
                .apiSecret(consumerSecret)
                .callback(callbackURL)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTH && resultCode == Activity.RESULT_OK && data != null) {
            processVerifier(data.getStringExtra(WebViewActivity.RES_AUTH_VERIFIER));
        }
    }

    private void processVerifier(final String oauthVerifier) {
        Log.d(TAG, "processVerifier: " + oauthVerifier);
        Utils.showDialogSave(MainActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                OAuth1AccessToken accessToken = null;
                try {
                    if (requestToken != null) {
                        accessToken = oAuthService.getAccessToken(requestToken, oauthVerifier);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if (accessToken != null && accessToken.getToken() != null && accessToken.getTokenSecret() != null) {
                    accessTokenKey = accessToken.getToken();
                    accessSecretTokenKey = accessToken.getTokenSecret();
                }
            }
        }).start();

    }

    private HttpResponse completeRequestURL(String url) {
//Example url: "http://api.tumblr.com/v2/blog/anime-fyi/followers"

        OAuthConsumer copyoAuthConsumer = new OAuthConsumer(consumerKey, consumerSecret);
        OAuthAccessToken copyAccessToken = new OAuthAccessToken(accessTokenKey, accessSecretTokenKey);
        SignedRequest signedRequestResponse = SignedRequestFactory.create(copyoAuthConsumer, copyAccessToken);

        HttpResponse responseResult = null;
        try {
            responseResult = signedRequestResponse.doGet(url, "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.toString());
        }
        //  response.getStatusCode(); // -> int
        if (responseResult != null) {
            Log.e(TAG, "response: " + responseResult.getTextBody());
        } else {
            Log.e(TAG, "response: null");
        }
        return responseResult;
    }

    private void gotoWebView(final String authUrl) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra(WebViewActivity.ARG_AUTH_URL, authUrl);
                startActivityForResult(intent, REQUEST_CODE_AUTH);
            }
        });
    }

}
