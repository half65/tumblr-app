package com.example.kesha.blog.fragments;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.adapters.InfoAdapter;
import com.example.kesha.blog.utils.Constants;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SearchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private EditText fieldSearch;
    private ImageButton startSearchBtn;
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
                        if (posts.size() == 0 && getActivity() != null) {
                            Toast toast = Toast.makeText(getActivity(),
                                    R.string.no_results, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            progressBarSearch.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            mSwipeRefresh.setRefreshing(false);
                        }
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
            bundle.putString(Constants.KEY_IMAGE_URL, imageURL);
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

        @Override
        public void onVideoClick(String videoUrl) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));

        }

        @Override
        public void onClickLike(final int position, final List<Post> posts, ImageView imageView, Boolean isLike, TextView likeCount) {
            if (isLike) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            posts.get(position).unlike();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                }.start();
                imageView.setImageResource(R.drawable.ic_unlike_24dp);
                Log.e(TAG, "unlike()");
                String likes = likeCount.getText().toString();
                likeCount.setText(String.valueOf(Long.valueOf(likes) - 1));
                if (getActivity() != null)
                    Toast.makeText(getActivity(), getActivity().getText(R.string.text_toast_unlike), Toast.LENGTH_SHORT).show();
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            posts.get(position).like();
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                    }
                }.start();
                imageView.setImageResource(R.drawable.ic_like_24dp);
                Log.e(TAG, "like()");
                String likes = likeCount.getText().toString();
                likeCount.setText(String.valueOf(Long.valueOf(likes) + 1));
                if (getActivity() != null)
                    Toast.makeText(getActivity(), getActivity().getText(R.string.text_toast_like), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onClickReblog(final int position, final List<Post> posts) {
            final JumblrClient client = TumblrApplication.getClient();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.postReblog(client.user().getBlogs().get(0).getName()
                            , posts.get(position).getId(), posts.get(position).getReblogKey());
                }
            }).start();
            if (getActivity() != null)
                Toast.makeText(getActivity(), getActivity().getText(R.string.text_toast_reblog), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRefresh() {
        if (recyclerView.getAdapter() != null) {
            if (!TextUtils.isEmpty(fieldSearch.getText())) {
                progressBarSearch.setVisibility(View.VISIBLE);
                progressBarSearch.setIndeterminate(true);
                getResponseSearch(fieldSearch.getText().toString());
            } else {
                mSwipeRefresh.setRefreshing(false);
            }
        } else {
            mSwipeRefresh.setRefreshing(false);
        }
    }
}
