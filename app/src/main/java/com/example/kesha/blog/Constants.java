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
       public static String blogAvatar = "https://api.tumblr.com/v2/blog/{blog-identifier}/avatar[/size]";
       public static String blogLikes = "https://api.tumblr.com/v2/blog/{blog-identifier}/likes?api_key=yuWfjh1UkS8UgAZWmjOPHQMmoJOXUXilND42nZTYYstohhxKsE";
       public static String blogFollowing = "https://api.tumblr.com/v2/blog/{blog-identifier}/following";
       public static String blogFollowers = "https://api.tumblr.com/v2/blog/{blog-identifier}/followers";
       public static String blogPosts = "https://api.tumblr.com/v2/blog/{blog-identifier}?api_key={key}&[optional-params=]";


}
