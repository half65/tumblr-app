package com.example.kesha.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.User;

import java.util.List;


public class FollowingFragment extends Fragment {
    private final String TAG = FollowingFragment.class.getName();
    private RecyclerView recyclerView;
    private List<Blog> blogs;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            getFollowing();
        }
        View fragmentView = inflater.inflate(R.layout.following_fragment, container, false);
        recyclerView = fragmentView.findViewById(R.id.following_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        if (getContext() != null) {
            DividerItemDecoration myDivider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            myDivider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_decoration));
            recyclerView.addItemDecoration(myDivider);
        }
        return fragmentView;
    }

    private void getFollowing() {
        final JumblrClient client = TumblrApplication.getClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "=========== > START GET FOLLOWING!!!");
                if (getActivity() != null && isAdded()) {
                    blogs = client.userFollowing();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getActivity() != null) {
                                    FollowingAdapter followingAdapter = new FollowingAdapter(getActivity(), blogs, client);
                                    recyclerView.setAdapter(followingAdapter);
                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
