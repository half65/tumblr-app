package com.example.kesha.blog.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.UtilsPackage.GlideApp;
import com.example.kesha.blog.R;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowingViewHolder> {
    private List<Blog> blogs;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private JumblrClient client;
    private int corner;

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
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(followingViewHolder.avatarFollowing);
                    }
                });
            }
        });
        followingViewHolder.nameFollowingTextView.setText(blogs.get(followingViewHolder.getAdapterPosition()).getName());
        followingViewHolder.dateUpdated.setText(dateConvertFromUNIX(blogs.get(followingViewHolder.getAdapterPosition()).getUpdated()));

    }

    private String dateConvertFromUNIX(Long date) {
        long dv = date * 1000;// its need to be in milisecond
        Date df = new java.util.Date(dv);
        @SuppressLint("SimpleDateFormat")
        String newFormatDate = new SimpleDateFormat("dd MMMM yyyy, HH:mm").format(df);
        if (activity != null) {
            return String.format(activity.getString(R.string.updateString), newFormatDate);
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    class FollowingViewHolder extends RecyclerView.ViewHolder {
        private TextView nameFollowingTextView;
        private TextView dateUpdated;
        private ImageView avatarFollowing;

        public FollowingViewHolder(View view) {
            super(view);
            dateUpdated = view.findViewById(R.id.date_update_textview);
            nameFollowingTextView = view.findViewById(R.id.name_following_txtview);
            avatarFollowing = view.findViewById(R.id.avatar_following_imgView);
        }
    }
}
