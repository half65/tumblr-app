package com.example.kesha.blog.UtilsPackage;

import android.util.Log;

import com.example.kesha.blog.TumblrApplication;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.util.List;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public interface JumblrUserInfoCallback {
        void onUserInfoLoaded(User user, Blog userBlog, String avatarUrl, List<User> followers);

        void onLoadFailed(String reason);
    }

    public static void loadUserInfo(final JumblrUserInfoCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JumblrClient client = TumblrApplication.getClient();
                try {
                    User user = client.user();
                    Blog blog = user.getBlogs().get(0);
                    String avatarUrl = blog.avatar(512);
                    List<User> followers = blog.followers();
                    callback.onUserInfoLoaded(user, blog, avatarUrl, followers);
                } catch (Exception e) {
                    Log.println(Log.ASSERT, TAG, "loadUserInfo exception: " + e.toString());
                    callback.onLoadFailed(e.toString());
                }
            }
        }).start();
    }

    public interface JumblrFollowersCallback{
        void onFollowersLoaded(List<User> users,JumblrClient client);
        void onLoadFailed(String reason);
    }

    public static void loadFollowers(final JumblrFollowersCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JumblrClient client = TumblrApplication.getClient();
                try {
                    User user = client.user();
                    List<User> users = client.blogFollowers(String.format("%s.tumblr.com", user.getBlogs().get(0).getName()));
                    callback.onFollowersLoaded(users,client);
                } catch (Exception e) {
                    Log.println(Log.ASSERT, TAG, "loadFollowingInfo exception: " + e.toString());
                    callback.onLoadFailed(e.toString());
                }
            }
        }).start();
    }

    public interface JumblrFollowingCallback{
        void onFollowingLoaded(List<Blog> blogs,JumblrClient client);
        void onLoadFailed(String reason);
    }

    public static void loadFollowing(final JumblrFollowingCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JumblrClient client = TumblrApplication.getClient();
                try {
                    List<Blog> blogs = client.userFollowing();
                    callback.onFollowingLoaded(blogs,client);
                } catch (Exception e) {
                    Log.println(Log.ASSERT, TAG, "loadFollowingInfo exception: " + e.toString());
                    callback.onLoadFailed(e.toString());
                }
            }
        }).start();
    }

}
