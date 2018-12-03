package com.example.kesha.blog.UtilsPackage;

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

    public interface JumblrUserInfoCallback {
        void onUserInfoLoaded(User user, Blog userBlog, String avatarUrl, List<User> followers,List<Post> postLike);

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
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("limit", 100);
                    List<Post> posts = client.blogLikes(String.format("%s.tumblr.com",blog.getName()),params);
                    callback.onUserInfoLoaded(user, blog, avatarUrl, followers,posts);
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

    /*public interface JumblrInfoLikesPhotoPostCallback {
        void onInfoLikesLoaded(List<Post> posts, JumblrClient client);

        void onLoadFailed(String reason);
    }

    public static void loadLikesPost(final PhotoPost post,final JumblrInfoLikesPhotoPostCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JumblrClient client = TumblrApplication.getClient();
                try {
                    List<String> url;
                    for (int i = 0; i < post.getPhotos().size(); i++) {
                        url.add(post.getPhotos().get(i).getSizes().get(1).)
                    }
                    callback.onInfoLikesLoaded(posts, client);
                } catch (Exception e) {
                    Log.println(Log.ASSERT, TAG, "loadFollowingInfo exception: " + e.toString());
                    callback.onLoadFailed(e.toString());
                }
            }
        }).start();
    }*/

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
}
