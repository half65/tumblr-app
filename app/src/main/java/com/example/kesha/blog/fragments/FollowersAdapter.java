package com.example.kesha.blog.fragments;

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
import com.example.kesha.blog.R;
import com.example.kesha.blog.UtilsPackage.GlideApp;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.User;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {
    private List<User> followers;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private JumblrClient client;
    private int corner;

    FollowersAdapter(Activity act, List<User> users, JumblrClient client) {
        followers = users;
        this.client = client;
        layoutInflater = LayoutInflater.from(act);
        activity = act;
        corner = activity.getResources().getDimensionPixelSize(R.dimen.icon_size_middle) / 2;
    }


    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(activity!=null){
        View view = layoutInflater.inflate(R.layout.item_followers, viewGroup, false);
        return new FollowersViewHolder(view);}return null;
    }

    private static void getFollowerAvatar(final JumblrClient client,final User follower, final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String avatarUrl = client.blogAvatar(String.format("%s.tumblr.com", follower.getName()));
                observer.update(null, avatarUrl);
            }
        }).start();
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowersViewHolder followersViewHolder, int i) {
        followersViewHolder.avatarFollower.setImageResource(R.drawable.text_tumblr_com);
        getFollowerAvatar(client,followers.get(followersViewHolder.getAdapterPosition()), new Observer() {
            @Override
            public void update(Observable observable, final Object o) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlideApp.with(activity)
                                .load((String) o)
                                .transform(new RoundedCorners(corner))
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(followersViewHolder.avatarFollower);
                    }
                });
            }
        });
        followersViewHolder.nameFollowerTextView.setText(followers.get(i).getName());

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
