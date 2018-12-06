package com.example.kesha.blog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.Constraints;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kesha.blog.UtilsPackage.PreferencesStorage;
import com.example.kesha.blog.fragments.FollowingFragment;
import com.example.kesha.blog.fragments.FollowersFragment;
import com.example.kesha.blog.fragments.FragmentSearch;
import com.example.kesha.blog.fragments.InfoFragment;
import com.example.kesha.blog.fragments.PostListFragment;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        mSwipeRefresh = findViewById(R.id.container);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources
                (R.color.light_blue, R.color.middle_blue,R.color.deep_blue);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PostListFragment(), "posts");
        adapter.addFragment(new InfoFragment(), "info");
        adapter.addFragment(new FollowingFragment(), "following");
        adapter.addFragment(new FollowersFragment(), "followers");
        adapter.addFragment(new FragmentSearch(), "search");
        viewPager.setAdapter(adapter);
    }

    private void test(){
        final JumblrClient client = TumblrApplication.getClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Blog blog = client.blogInfo("anime.fyi.tumblr.com");
                Log.d(TAG,"info blog" + blog.toString());
                List<Post> posts = client.userDashboard();
                List<Post> postss = client.blogLikes("keshaplastilin.tumblr.com");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("type", "photo");
                PhotoPost photoPost = (PhotoPost) posts.get(5);
                List<Post> postsL = client.blogPosts(String.format("%s.tumblr.com",postss.get(0).getBlogName()), params);
                Log.d(TAG,"post dash");
                Log.d(TAG,"post dash");
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                mSwipeRefresh.setRefreshing(false)
                ;}}, 5000);
    }
}
