package com.example.kesha.blog;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kesha.blog.fragments.FollowingFragment;
import com.example.kesha.blog.fragments.FollowersFragment;
import com.example.kesha.blog.fragments.FragmentSearch;
import com.example.kesha.blog.fragments.InfoFragment;
import com.example.kesha.blog.fragments.postListFragment;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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
        test();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new postListFragment(), "posts");
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
                blog.getUpdated();
                Log.d(TAG,"post dash" + posts.toString());
            }
        }).start();
    }

}
