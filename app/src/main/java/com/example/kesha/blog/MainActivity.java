package com.example.kesha.blog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefresh;
    private AlertDialog.Builder ad;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    PostsFragment postsFragment;




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.update:
                String title = "Смена профиля";
                String message = "Вы уверены?";
                ad = new AlertDialog.Builder(this);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                PreferencesStorage.removeData();
                                startActivity(new Intent(MainActivity.this, StartActivity.class));
                                finish();
                            }
                        });
                ad.setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                ad.setCancelable(true);
                ad.show();
                break;
            case R.id.exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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
        if (Utils.hasConnection(MainActivity.this)) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utils.myBlogName = TumblrApplication.getClient().user().getBlogs().get(0).getName();
                }
            }).start();

            ViewPager viewPager = findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = findViewById(R.id.sliding_tabs);
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
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        postsFragment = new PostsFragment();
        adapter.addFragment(postsFragment, "posts");
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
    public void search(String data) {
        Utils.loadBlogPosts(data, new Utils.JumblrPostCallback() {

            @Override
            public void onLoadFailed(final String reason) {
            }

            @Override
            public void onPostLoaded(final List<Post> posts, JumblrClient client) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(postsFragment != null){
                            postsFragment.isDashboard = false;
                            tabLayout.getTabAt(0).select();
                            postsFragment.setSearchResult(posts);
                        }
                    }
                });
            }
        });
    }
}
