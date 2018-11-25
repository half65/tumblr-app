package com.example.kesha.blog.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.GlideApp;
import com.example.kesha.blog.R;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {
    private List<Blog> blogs;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private Bitmap avatarBitmap;
    private JumblrClient client;
    int corner;

    FollowingAdapter(Activity activity, List<Blog> blogs, JumblrClient client) {
        this.activity = activity;
        this.blogs = blogs;
        this.client = client;
        layoutInflater = LayoutInflater.from(activity);
        corner = activity.getResources().getDimensionPixelSize(R.dimen.icon_size_middle) / 2;
    }


    @NonNull
    @Override
    public FollowingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_following, viewGroup, false);
        return new FollowingViewHolder(view);
    }


    private static void getFollowingAvatar(final Blog blog, final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String avatarUrl = blog.avatar(256);
                observer.update(null, avatarUrl);
            }
        }).start();
    }


    @Override
    public void onBindViewHolder(@NonNull final FollowingViewHolder followingViewHolder, int i) {
        followingViewHolder.avatarFollowing.setImageResource(R.drawable.text_tumblr_com);
        getFollowingAvatar(blogs.get(followingViewHolder.getAdapterPosition()), new Observer() {
            @Override
            public void update(Observable observable, final Object o) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlideApp.with(activity)
                                .load((String) o)
                                .transform(new RoundedCorners(corner))
                                .into(followingViewHolder.avatarFollowing);
                    }
                });
            }
        });

        followingViewHolder.nameFollowingTextView.setText(blogs.get(followingViewHolder.getAdapterPosition()).getName());
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
