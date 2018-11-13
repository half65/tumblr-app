package com.example.kesha.blog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private LinearLayout searchLayout;
    private FrameLayout postView;
    private FrameLayout infoLayout;
    private FrameLayout followingsView;
    private FrameLayout followersView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_post:
                        postView.setVisibility(View.VISIBLE);

                        return true;
                    case R.id.navigation_info_blog:

                        infoLayout.setVisibility(View.VISIBLE);

                        return true;
                    case R.id.navigation_subscriptions:

                        followingsView.setVisibility(View.VISIBLE);

                        return true;
                    case R.id.navigation_subscribers:

                        followersView.setVisibility(View.VISIBLE);

                        return true;
                    case R.id.navigation_search_by_tags:

                        searchLayout.setVisibility(View.VISIBLE);
                        return true;
                }
                return false;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void switchVisibility(Layout layout){

    }

}
