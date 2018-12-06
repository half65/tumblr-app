package com.example.kesha.blog.UtilsPackage;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.kesha.blog.TumblrApplication;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.User;
import com.tumblr.jumblr.types.VideoPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static final String HOSTNAME = "api.tumblr.com";

    public interface JumblrUserInfoCallback {
        void onUserInfoLoaded(User user, Blog userBlog, List<User> followers,List<Post> postLike);

        void onLoadFailed(String reason);
    }


    private static String blogPath(String blogName, String extPath) {
        return "https://" + HOSTNAME + "/v2" + "/blog/" + blogUrl(blogName) + extPath;
    }

    private static String blogUrl(String blogName) {
        return blogName.contains(".") ? blogName : blogName + ".tumblr.com";
    }

    public static String getAvatarUrl(String blogName, int size) {
        String pathExt = "/" + size;
        return blogPath(blogName, "/avatar" + pathExt);
    }

    public static void loadUserInfo(final JumblrUserInfoCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JumblrClient client = TumblrApplication.getClient();
                try {
                    User user = client.user();
                    Blog blog = user.getBlogs().get(0);
                    List<User> followers = blog.followers();
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("limit", 100);
                    List<Post> posts = client.blogLikes(String.format("%s.tumblr.com",blog.getName()),params);
                    callback.onUserInfoLoaded(user, blog,followers,posts);
                } catch (Exception e) {
                    Log.println(Log.ASSERT, TAG, "loadUserInfo exception: " + e.toString());
                    callback.onLoadFailed(e.toString());
                }
            }
        }).start();
    }

    public interface JumblrFollowersCallback {
        void onFollowersLoaded(List<User> users, JumblrClient client);

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
                    callback.onFollowersLoaded(users, client);
                } catch (Exception e) {
                    Log.println(Log.ASSERT, TAG, "loadFollowingInfo exception: " + e.toString());
                    callback.onLoadFailed(e.toString());
                }
            }
        }).start();
    }

    public interface JumblrFollowingCallback {
        void onFollowingLoaded(List<Blog> blogs, JumblrClient client);

        void onLoadFailed(String reason);
    }

    public static void loadFollowing(final JumblrFollowingCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JumblrClient client = TumblrApplication.getClient();
                try {
                    List<Blog> blogs = client.userFollowing();
                    callback.onFollowingLoaded(blogs, client);
                } catch (Exception e) {
                    Log.println(Log.ASSERT, TAG, "loadFollowingInfo exception: " + e.toString());
                    callback.onLoadFailed(e.toString());
                }
            }
        }).start();
    }

    public static Post convertPost(Post post) {
        switch (post.getType()) {
            case TEXT:
                return (TextPost) post;
            case PHOTO:
                return (PhotoPost) post;
            case VIDEO:
                return (VideoPost) post;
                default:return null;
        }

        /*TEXT("text"),
                PHOTO("photo"),
                QUOTE("quote"),
                LINK("link"),
                CHAT("chat"),
                AUDIO("audio"),
                VIDEO("video"),
                ANSWER("answer"),
                POSTCARD("postcard"),
                UNKNOWN("unknown");*/
    }

    public static boolean hasConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
