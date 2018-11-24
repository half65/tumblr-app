package com.example.kesha.blog.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kesha.blog.Constants;
import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class InfoFragment extends Fragment {
    private final String TAG = InfoFragment.class.getSimpleName();
    private ImageView avatarImageView;
    private TextView nameTextView;
    private TextView postsTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private RecyclerView informationRecycler;
    private String accessTokenKey;
    private String accessSecretTokenKey;
    private User user;
    private SharedPreferences sPref;
    private Bitmap avatarBitmap;
    private int followerCount;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_info, container, false);

        avatarImageView = fragmentView.findViewById(R.id.avatarImageView);
        nameTextView = fragmentView.findViewById(R.id.nameTextView);
        postsTextView = fragmentView.findViewById(R.id.postsTextView);
        followersTextView = fragmentView.findViewById(R.id.followersTextView);
        followingTextView = fragmentView.findViewById(R.id.followingTextView);
        informationRecycler = fragmentView.findViewById(R.id.informationRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        informationRecycler.setLayoutManager(manager);
        if (getContext() != null) {
            getInfo(getContext());
        }
        return fragmentView;
    }

    public void setAvatarImageView(Bitmap avatar) {
        avatarImageView.setImageBitmap(avatar);
    }

    public void setNameTextView(String name) {
        nameTextView.setText(name);
    }

    public void setPostsTextView(String posts) {
        postsTextView.setText(posts);
    }

    public void setFollowersTextView(String followers) {
        followersTextView.setText(followers);
    }

    public void setFollowingTextView(String following) {
        followingTextView.setText(following);
    }

    public void setInformationRecycler() {

    }

    private void getInfo(Context context) {


        sPref = context.getSharedPreferences(Constants.S_PREF_NAME, MODE_PRIVATE);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "=========== > START GET INFO!!!");


                Log.e(TAG, "============================ accessTokenKey > " + accessTokenKey);
                Log.e(TAG, "============================ accessSecretTokenKey > " + accessSecretTokenKey);

                    JumblrClient client = TumblrApplication.getClient();
                    final User user = client.user();
                    followerCount = user.getBlogs().get(0).followers().size();
                    Log.e(TAG, "============================ user.getName()> " + user.getName());

                    Log.e(TAG, "============================ followers> " + followerCount);
                    Log.e(TAG, "============================ avatar> " + user.getBlogs().get(0).avatar(512));

                    String avatarUrl = user.getBlogs().get(0).avatar(512);
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) new URL(avatarUrl).openConnection();
                        InputStream stream = connection.getInputStream();
                        avatarBitmap = BitmapFactory.decodeStream(stream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                avatarImageView.setImageBitmap(avatarBitmap);
                                setNameTextView(user.getName());
                                setPostsTextView(String.format(getString(R.string.text_info_posts_count)
                                        , user.getBlogs().get(0).getPostCount()));
                                setFollowersTextView(String.format(getString(R.string.text_info_follower_count)
                                        , followerCount));

                                setFollowingTextView(String.format(getString(R.string.text_info_following_count)
                                        , user.getFollowingCount()));
                            }
                        });
                    }

            }
        }).start();
    }

}
