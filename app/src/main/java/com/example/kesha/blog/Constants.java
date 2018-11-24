package com.example.kesha.blog;

public class Constants {

    //require authentication or api key = >
    public static String taggedUrl = "https://api.tumblr.com/v2/tagged?tag=";

    //require authentication = >
    public static String userInfoUrl = "https://api.tumblr.com/v2/user/info";
    public static String userDashboardUrl = "https://api.tumblr.com/v2/user/dashboard";
    public static String userLikesUrl = "https://api.tumblr.com/v2/user/likes";
    public static String userFollowingUrl = "https://api.tumblr.com/v2/user/following";
    public static String userFollowUrl = "api.tumblr.com/v2/user/follow";
    public static String userUnFollowUrl = "api.tumblr.com/v2/user/unfollow";
    public static String userLikeUrl = "https://api.tumblr.com/v2/user/like";
    public static String userUnLikeUrl = "https://api.tumblr.com/v2/user/unlike";
    public static String postReblog = "https://api.tumblr.com/v2/blog/{blog-identifier}/post/reblog";
    public static String fetchingPost = "https://api.tumblr.com/v2/blog/{blog-identifier}/posts/{post-id}";
    public static String postDelete = "https://api.tumblr.com/v2/blog/{blog-identifier}/post/delete";

    //do not require authentication, use the api key = >

    public static String blogInfo = "https://api.tumblr.com/v2/blog/%1s/info?api_key=yuWfjh1UkS8UgAZWmjOPHQMmoJOXUXilND42nZTYYstohhxKsE";
    public static String blogAvatar = "https://api.tumblr.com/v2/blog/%1s/avatar";
    public static String blogLikes = "https://api.tumblr.com/v2/blog/%1s/likes?api_key=yuWfjh1UkS8UgAZWmjOPHQMmoJOXUXilND42nZTYYstohhxKsE";
    public static String blogFollowing = "https://api.tumblr.com/v2/blog/following";
    public static String blogFollowers = "https://api.tumblr.com/v2/blog/followers";
    public static String blogPosts = "https://api.tumblr.com/v2/blog/%1s/posts?api_key=yuWfjh1UkS8UgAZWmjOPHQMmoJOXUXilND42nZTYYstohhxKsE";

    public static final String callbackURL = "http://www.tumblr.com/connect/login_success.html";
    public static final String consumerKey = "yuWfjh1UkS8UgAZWmjOPHQMmoJOXUXilND42nZTYYstohhxKsE";
    public static final String consumerSecret = "X3B5Pm1xuqqerpLdqXFeozYJZNVnkvNAzK43UmkXHHMerHBlt8";
    public static final String S_PREF_NAME = "S_PREF_NAME";
    public static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    public static final String KEY_ACCESS_SECRET_TOKEN = "KEY_ACCESS_SECRET_TOKEN";
    public static final String SAVE_AUTHORIZATION_FLAG_KEY = "SAVE_AUTHORIZATION_FLAG_KEY";




}
