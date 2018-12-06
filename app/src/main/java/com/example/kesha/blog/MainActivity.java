package com.example.kesha.blog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kesha.blog.utils.PreferencesStorage;
import com.example.kesha.blog.utils.Utils;
import com.example.kesha.blog.fragments.FollowersFragment;
import com.example.kesha.blog.fragments.FollowingFragment;
import com.example.kesha.blog.fragments.FragmentSearch;
import com.example.kesha.blog.fragments.InfoFragment;
import com.example.kesha.blog.fragments.PostsFragment;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.update) {
            PreferencesStorage.removeData();
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
        mSwipeRefresh = findViewById(R.id.container);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
        if (Utils.hasConnection(MainActivity.this)) {
            ViewPager viewPager = findViewById(R.id.viewpager);
            setupViewPager(viewPager);


            TabLayout tabLayout = findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                tabLayout.getTabAt(i).setIcon(tabIcons[i]);
            }
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.textErrNoInternet), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PostsFragment(), "posts");
        adapter.addFragment(new InfoFragment(), "info");
        adapter.addFragment(new FollowingFragment(), "following");
        adapter.addFragment(new FollowersFragment(), "followers");
        adapter.addFragment(new FragmentSearch(), "search");
        viewPager.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefresh.setRefreshing(false);
            }
        }, 5000);
    }
}
