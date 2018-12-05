package com.example.kesha.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kesha.blog.R;
import com.example.kesha.blog.UtilsPackage.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.User;

import java.util.List;

public class FollowersFragment extends Fragment {
    private final String TAG = FollowersFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.followers_fragment, container, false);
        recyclerView = fragmentView.findViewById(R.id.followers_recycler);
        recyclerView.setVisibility(View.GONE);
        progressBar = fragmentView.findViewById(R.id.progress_bar_followers);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
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
        setFollowersAdapter();
    }

    private void setFollowersAdapter() {
        Utils.loadFollowers(new Utils.JumblrFollowersCallback() {
            @Override
            public void onFollowersLoaded(final List<User> users, final JumblrClient client) {
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FollowersAdapter followersAdapter = new FollowersAdapter(getActivity(), users, client);
                            recyclerView.setAdapter(followersAdapter);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setIndeterminate(false);
                        }
                    });


            }

            @Override
            public void onLoadFailed(final String reason) {
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "loading error: " + reason, Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
    }
}


