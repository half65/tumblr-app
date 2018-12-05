package com.example.kesha.blog.fragments;

import android.app.Activity;
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
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.info_fragment, container, false);
        relativeLayout = fragmentView.findViewById(R.id.info_relative_layout);
        informationRecycler = fragmentView.findViewById(R.id.informationRecycler);
        relativeLayout.setVisibility(View.GONE);
        activity = getActivity();
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


    private void fillData(User user, Blog blog, int followers, List<Post> postLike) {
        if (getActivity() != null && isAdded()) {
            //process user data
            nameTextView.setText(user.getName());
            followingTextView.setText(getActivity().getString(R.string.text_info_following_count, user.getFollowingCount()));

            String avatarUrl = Utils.getAvatarUrl(blog.getName(), 512);
            //process blog data
            if (!TextUtils.isEmpty(avatarUrl)) {
                GlideApp.with(getActivity())
                        .load(avatarUrl)
                        .placeholder(R.drawable.text_tumblr_com)
                        .transform(new RoundedCorners(getActivity()
                                .getResources()
                                .getDimensionPixelSize(R.dimen.icon_size_avatar_user_info) / 2))
                        .into(avatarImageView);
            }
            postsTextView.setText(getActivity().getString(R.string.text_info_posts_count, blog.getPostCount()));
            followersTextView.setText(getActivity().getString(R.string.text_info_followers_count, followers));
            relativeLayout.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.GONE);

            InfoAdapter infoAdapter = new InfoAdapter(getActivity(), postLike, onImageClickListener);
            informationRecycler.setAdapter(infoAdapter);
        }
    }

    private final InfoAdapter.OnInfoAdapterClickListener onImageClickListener = new InfoAdapter.OnInfoAdapterClickListener() {
        @Override
        public void onImageClick(String imageURL) {
            ImageDialogFragment imageDialogFragment = new ImageDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imageUrl", imageURL);
            imageDialogFragment.setArguments(bundle);
            if(getActivity()!=null) {
                imageDialogFragment.show(getActivity().getSupportFragmentManager(), ImageDialogFragment.class.getSimpleName());
            }
        }

        @Override
        public void onBodyTextClick(TextView textView) {
            int maxLines = textView.getMaxLines();
            if(maxLines==15){
                textView.setMaxLines(2000);
            }else {
                textView.setMaxLines(15);
            }
        }
    };


    private void getInfo() {
        Utils.loadUserInfo(new Utils.JumblrUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(final User user, final Blog userBlog, final List<User> followers, final List<Post> postLike) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fillData(user, userBlog, followers.size(), postLike);
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
