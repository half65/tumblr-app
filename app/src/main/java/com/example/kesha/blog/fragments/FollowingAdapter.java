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
import com.tumblr.jumblr.types.Blog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {
    private List<Blog> blogs;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private Bitmap avatarBitmap;
    private JumblrClient client;

    FollowingAdapter(Activity activity, List<Blog> blogs,JumblrClient client){
        this.activity = activity;
        this.blogs = blogs;
        this.client = client;
        layoutInflater = LayoutInflater.from(activity);
    }


    @NonNull
    @Override
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_following, viewGroup, false);
        return new FollowingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowingViewHolder followingViewHolder, int i) {
        final int position = i;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String avatarUrl = blogs.get(position).avatar(512);
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(avatarUrl).openConnection();
                    InputStream stream = connection.getInputStream();
                    avatarBitmap = BitmapFactory.decodeStream(stream);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            followingViewHolder.avatarFollowing.setImageBitmap(avatarBitmap);
                            followingViewHolder.nameFollowingTextView.setText(blogs.get(position).getName());
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
        return blogs.size();
    }

    class FollowingViewHolder extends RecyclerView.ViewHolder {
        private TextView nameFollowingTextView;
        private ImageView avatarFollowing;

        public FollowingViewHolder(View view) {
            super(view);
            nameFollowingTextView = view.findViewById(R.id.name_following_txtview);
            avatarFollowing = view.findViewById(R.id.avatar_following_imgView);
        }
    }
}
