package com.example.kesha.blog.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tumblr.jumblr.types.User;

import java.util.List;

public class FollowersFragment extends Fragment {
    private final String TAG = FollowersFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private Activity activity;
    private List<User> users;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        if (getActivity() != null) {

        }
        View fragmentView = inflater.inflate(R.layout.followers_fragment, container, false);
        recyclerView = fragmentView.findViewById(R.id.followers_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
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
        getFollowers();
    }

    private void getFollowers() {
        final JumblrClient client = TumblrApplication.getClient();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "=========== > START GET_FOLLOWERS!!!");

                if (getActivity() != null && isAdded()) {
                    User user = client.user();
                    if (getActivity() != null) {
                        users = client.blogFollowers(String.format(getActivity()
                                .getString(R.string.title_blog), user.getBlogs().get(0).getName()));
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (users != null) {
                                    FollowersAdapter followersAdapter = new FollowersAdapter(activity, users, client);
                                    recyclerView.setAdapter(followersAdapter);
                                }
                            }
                        });
                    }
                }
            }
        }).start();

    }
}
