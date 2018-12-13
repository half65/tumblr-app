package com.example.kesha.blog.fragments;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.adapters.InfoAdapter;
import com.example.kesha.blog.utils.GlideApp;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.User;

import java.util.List;

public class InfoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = InfoFragment.class.getSimpleName();
    private RecyclerView informationRecycler;
    private ImageView avatarImageView;
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private TextView nameTextView;
    private TextView postsTextView;
    private TextView followersTextView;
    private TextView followingTextView;
    private SwipeRefreshLayout mSwipeRefresh;
    private RelativeLayout topElement;
    private boolean isDown = true;
    private boolean vid = true;
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            Log.e("ASD", "newState = " + newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            Log.e("ASD", "dx = " + dx + " dy = " + dy);
            if (dy > 0 && isDown) {
                topElement.animate().translationY(-550);
                isDown = false;
                vid = false;
                if (vid = false){
                    topElement.setVisibility(View.GONE);
                }
            }
            if (dy < 0 && !isDown) {
                topElement.animate().translationY(Math.abs(0));
                isDown = true;
                vid = true;
                if (vid = true ){
                    topElement.setVisibility(View.VISIBLE);
                }
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.info_fragment, container, false);
        topElement = fragmentView.findViewById(R.id.upper_insert);
        mSwipeRefresh = fragmentView.findViewById(R.id.containerInfo);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
        relativeLayout = fragmentView.findViewById(R.id.info_relative_layout);
        informationRecycler = fragmentView.findViewById(R.id.informationRecycler);
        informationRecycler.setOnScrollListener(scrollListener);
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
            mSwipeRefresh.setRefreshing(false);
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

        @Override
        public void onVideoClick(String videoUrl) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
        }

        @Override
        public void onClickLike(final int position,final List<Post> posts, ImageView imageView, Boolean isLike,TextView likeCount) {

            if(isLike){
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            posts.get(position).unlike();
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
                        }
                    }
                }.start();
                imageView.setImageResource(R.drawable.ic_unlike_24dp);
                Log.e(TAG, "unlike()");
                String likes = likeCount.getText().toString();
                likeCount.setText(String.valueOf(Long.valueOf(likes)-1));
                Toast.makeText(getActivity(),"unlike",Toast.LENGTH_SHORT).show();
            }else {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            posts.get(position).like();
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
                        }

                    }
                }.start();
                imageView.setImageResource(R.drawable.ic_like_24dp);
                Log.e(TAG, "like()");
                String likes = likeCount.getText().toString();
                likeCount.setText(String.valueOf(Long.valueOf(likes)+1));
                Toast.makeText(getActivity(),"like",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onClickReblog(final int position,final List<Post> posts) {
            final JumblrClient client = TumblrApplication.getClient();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.postReblog(client.user().getBlogs().get(0).getName()
                            , posts.get(position).getId(), posts.get(position).getReblogKey());
                }
            }).start();
            Toast.makeText(getActivity(),"reblog",Toast.LENGTH_SHORT).show();
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
                            mSwipeRefresh.setRefreshing(false);
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onRefresh() {
        getInfo();
    }

}
