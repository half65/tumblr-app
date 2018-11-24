package com.example.kesha.blog.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kesha.blog.R;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {
    private List<User> followers;
    LayoutInflater layoutInflater;
    private Activity activity;
    private Bitmap avatarBitmap;
    private JumblrClient client;

    FollowersAdapter(Activity act, List<User> users, JumblrClient client) {
        followers = users;
        this.client = client;
        layoutInflater = LayoutInflater.from(act);
        activity = act;
    }


    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_followers, viewGroup, false);
        return new FollowersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowersViewHolder followersViewHolder, int i) {
        final int position = i;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String avatarUrl = client.blogAvatar(String.format(activity.getString(R.string.title_blog), followers.get(position).getName()));
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(avatarUrl).openConnection();
                    InputStream stream = connection.getInputStream();
                    avatarBitmap = BitmapFactory.decodeStream(stream);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            followersViewHolder.avatarFollower.setImageBitmap(avatarBitmap);
                            followersViewHolder.nameFollowerTextView.setText(followers.get(position).getName());
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    class FollowersViewHolder extends RecyclerView.ViewHolder {
        private TextView nameFollowerTextView;
        private ImageView avatarFollower;


        public FollowersViewHolder(View view) {
            super(view);

            nameFollowerTextView = view.findViewById(R.id.name_followers_txtview);
            avatarFollower = view.findViewById(R.id.avatar_followers_imgView);
        }
    }
}
