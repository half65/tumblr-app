package com.example.kesha.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.adapters.InfoAdapter;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.util.List;

public class FragmentSearch extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private EditText fieldSearch;
    private Button startSearchBtn;
    private ProgressBar progressBarSearch;
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = fragmentView.findViewById(R.id.recycler_search_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setVisibility(View.GONE);
        fieldSearch = fragmentView.findViewById(R.id.fild_search_edit);
        startSearchBtn = fragmentView.findViewById(R.id.start_search_batton);
        progressBarSearch = fragmentView.findViewById(R.id.progress_bar_search);
        progressBarSearch.setVisibility(View.GONE);
        mSwipeRefresh = fragmentView.findViewById(R.id.containerSearch);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startSearchBtn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!TextUtils.isEmpty(fieldSearch.getText())) {
                progressBarSearch.setVisibility(View.VISIBLE);
                progressBarSearch.setIndeterminate(true);
                getResponseSearch(fieldSearch.getText().toString());
            }

        }
    };

    private void makeAdapter(final List<Post> posts) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && isAdded()) {
                        if (posts.size() != 0 && getActivity() != null) {
                            InfoAdapter infoAdapter = new InfoAdapter(getActivity(), posts, onSearchClickListener);
                            recyclerView.setAdapter(infoAdapter);
                            progressBarSearch.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            mSwipeRefresh.setRefreshing(false);
                        }
                    }
                }
            });
        }
    }

    private void getResponseSearch(final String tagText) {
        Utils.loadTaggedPosts(tagText, new Utils.TaggedPostsCallback() {
            @Override
            public void onPostsLoaded(List<Post> posts) {
                makeAdapter(posts);
            }
        });
    }

    private final InfoAdapter.OnInfoAdapterClickListener onSearchClickListener = new InfoAdapter.OnInfoAdapterClickListener() {
        @Override
        public void onImageClick(String imageURL) {
            ImageDialogFragment imageDialogFragment = new ImageDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imageUrl", imageURL);
            imageDialogFragment.setArguments(bundle);
            if (getActivity() != null) {
                imageDialogFragment.show(getActivity().getSupportFragmentManager(), ImageDialogFragment.class.getSimpleName());
            }
        }

        @Override
        public void onBodyTextClick(TextView textView) {
            int maxLines = textView.getMaxLines();
            if (maxLines == 15) {
                textView.setMaxLines(2000);
            } else {
                textView.setMaxLines(15);
            }
        }
    };

    @Override
    public void onRefresh() {
        if(recyclerView.getAdapter()!=null){
            if (!TextUtils.isEmpty(fieldSearch.getText())) {
                progressBarSearch.setVisibility(View.VISIBLE);
                progressBarSearch.setIndeterminate(true);
                getResponseSearch(fieldSearch.getText().toString());
            }else {
                mSwipeRefresh.setRefreshing(false);
            }
        }else {
            mSwipeRefresh.setRefreshing(false);
        }
    }
}
