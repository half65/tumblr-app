package com.example.kesha.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kesha.blog.R;
import com.example.kesha.blog.utils.Utils;
import com.example.kesha.blog.adapters.FollowingAdapter;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;

import java.util.List;


public class FollowingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = FollowingFragment.class.getName();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout mSwipeRefresh;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.following_fragment, container, false);
        recyclerView = fragmentView.findViewById(R.id.following_recycler);
        recyclerView.setVisibility(View.GONE);
        progressBar = fragmentView.findViewById(R.id.progress_bar_following);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        LinearLayoutManager manager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        mSwipeRefresh = fragmentView.findViewById(R.id.containerFollowing);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
        if (getContext() != null) {
            DividerItemDecoration myDivider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            myDivider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_decoration));
            recyclerView.addItemDecoration(myDivider);
        }
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setFollowingAdapter();
    }

    private void setFollowingAdapter() {
        Utils.loadFollowing(new Utils.JumblrFollowingCallback() {
            @Override
            public void onFollowingLoaded(final List<Blog> blogs, final JumblrClient client) {
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FollowingAdapter followingAdapter = new FollowingAdapter(getActivity(), blogs);
                            recyclerView.setAdapter(followingAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.GONE);
                            mSwipeRefresh.setRefreshing(false);
                        }
                    });
            }

            @Override
            public void onLoadFailed(final String reason) {
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.text_error_load_data) + reason, Toast.LENGTH_LONG).show();
                            mSwipeRefresh.setRefreshing(false);
                        }
                    });
            }
        });
    }

    @Override
    public void onRefresh() {
        setFollowingAdapter();
    }
}


