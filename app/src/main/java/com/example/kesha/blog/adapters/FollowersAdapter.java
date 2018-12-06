package com.example.kesha.blog.adapters;

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
import com.example.kesha.blog.utils.GlideApp;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.User;

import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {
    private List<User> followers;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private JumblrClient client;
    private int corner;

    public FollowersAdapter(Activity act, List<User> users, JumblrClient client) {
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

    @Override
    public void onBindViewHolder(@NonNull final FollowersViewHolder followersViewHolder, int i) {
        followersViewHolder.avatarFollower.setImageResource(R.drawable.text_tumblr_com);
        String avatarUrl = Utils.getAvatarUrl(followers.get(followersViewHolder.getAdapterPosition()).getName(), 512);
                GlideApp.with(activity)
                                .load(avatarUrl)
                                .transform(new RoundedCorners(corner))
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(followersViewHolder.avatarFollower);

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
            nameFollowerTextView = view.findViewById(R.id.name_followers_text_view);
            avatarFollower = view.findViewById(R.id.avatar_followers_imgView);
        }
    }
}
