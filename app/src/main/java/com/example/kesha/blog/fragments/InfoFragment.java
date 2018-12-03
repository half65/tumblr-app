package com.example.kesha.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.UtilsPackage.GlideApp;
import com.example.kesha.blog.R;
import com.example.kesha.blog.UtilsPackage.Utils;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.util.List;

public class InfoFragment extends Fragment {
    private final String TAG = InfoFragment.class.getSimpleName();
    private RecyclerView informationRecycler;
    private ImageView avatarImageView;
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private TextView nameTextView;
    private TextView postsTextView;
    private TextView followersTextView;
    private TextView followingTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.info_fragment, container, false);
        relativeLayout = fragmentView.findViewById(R.id.info_relative_layout);
        informationRecycler = fragmentView.findViewById(R.id.informationRecycler);
        relativeLayout.setVisibility(View.GONE);
        progressBar = fragmentView.findViewById(R.id.progressBar_info_fragment);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        avatarImageView = fragmentView.findViewById(R.id.avatarImageView);
        nameTextView = fragmentView.findViewById(R.id.nameTextView);
        postsTextView = fragmentView.findViewById(R.id.postsTextView);
        followersTextView = fragmentView.findViewById(R.id.followersTextView);
        followingTextView = fragmentView.findViewById(R.id.followingTextView);
        LinearLayoutManager manager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        informationRecycler.setLayoutManager(manager);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInfo();
    }


    private void fillData(User user, Blog blog, String avatarUrl, int followers) {
        if (getActivity() != null && isAdded()) {
            //process user data
            nameTextView.setText(user.getName());
            followingTextView.setText(getActivity().getString(R.string.text_info_following_count, user.getFollowingCount()));

            //process blog data
            if (!TextUtils.isEmpty(avatarUrl)) {
                GlideApp.with(getActivity())
                        .load(avatarUrl)
                        .placeholder(R.drawable.text_tumblr_com)
                        .transform(new RoundedCorners(getActivity()
                                .getResources()
                                .getDimensionPixelSize(R.dimen.icon_size_avatar_user_info)/2))
                        .into(avatarImageView);
            }
            postsTextView.setText(getActivity().getString(R.string.text_info_posts_count, blog.getPostCount()));
            followersTextView.setText(getActivity().getString(R.string.text_info_followers_count, followers));
            relativeLayout.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getInfo() {
        Utils.loadUserInfo(new Utils.JumblrUserInfoCallback() {

            @Override
            public void onUserInfoLoaded(final User user,final Blog userBlog,final String avatarUrl, final List<User> followers,final List<Post> postLike) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillData(user, userBlog,avatarUrl, followers.size());
                            InfoAdapter infoAdapter = new InfoAdapter(getActivity(),postLike);
                            informationRecycler.setAdapter(infoAdapter);
                        }
                    });

                }
            }

            @Override
            public void onLoadFailed(final String reason) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

}
