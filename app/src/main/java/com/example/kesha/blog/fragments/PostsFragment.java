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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kesha.blog.MainActivity;
import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.adapters.PostsAdapter;
import com.example.kesha.blog.utils.Constants;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.util.List;


public class PostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = PostsAdapter.class.getSimpleName();
    private RecyclerView recyclerView;
    private EditText fieldEnterBlogName;
    private ProgressBar postFragmentProgressBar;
    private SwipeRefreshLayout mSwipeRefresh;
    public boolean isDashboard = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_post_list, container, false);
        recyclerView = fragmentView.findViewById(R.id.post_recycler);
        recyclerView.setVisibility(View.GONE);
        postFragmentProgressBar = fragmentView.findViewById(R.id.post_fragment_progressBar);
        postFragmentProgressBar.setVisibility(View.VISIBLE);
        postFragmentProgressBar.setIndeterminate(true);
        mSwipeRefresh = fragmentView.findViewById(R.id.containerPost);
        mSwipeRefresh.setOnRefreshListener(this);
        mSwipeRefresh.setColorSchemeResources(R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fieldEnterBlogName = fragmentView.findViewById(R.id.field_enter_blog_name);
        ImageButton startSearchBlogPostsBtn = fragmentView.findViewById(R.id.start_search_blogs_btn);
        startSearchBlogPostsBtn.setOnClickListener(startSearchBlogListener);
        return fragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        if (isDashboard) {
            loadUserDashboard();
        } else {
            isDashboard = true;
        }
    }

    private void loadUserDashboard() {
        Utils.loadPostsDashBoard(new Utils.JumblrUserDashboardCallback() {
            @Override
            public void onDashboardPostsLoaded(final List<Post> posts, JumblrClient client) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PostsAdapter postsAdapter = new PostsAdapter(getActivity(), posts, onPostClickListener);
                            recyclerView.setAdapter(postsAdapter);
                            postFragmentProgressBar.setVisibility(View.GONE);
                            postFragmentProgressBar.setIndeterminate(false);
                            recyclerView.setVisibility(View.VISIBLE);
                            mSwipeRefresh.setRefreshing(false);
                        }
                    });

                }
            }

            @Override
            public void onLoadFailed(final String reason) {
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.text_error_load_data) + reason, Toast.LENGTH_LONG).show();
                            postFragmentProgressBar.setVisibility(View.GONE);
                            postFragmentProgressBar.setIndeterminate(false);
                            mSwipeRefresh.setRefreshing(false);
                        }
                    });
            }
        });
    }

    View.OnClickListener startSearchBlogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getActivity() != null) {
                MainActivity activity = (MainActivity) getActivity();
                if (!TextUtils.isEmpty(fieldEnterBlogName.getText().toString()))
                    activity.search(fieldEnterBlogName.getText().toString());
            }
        }
    };

    public void setSearchResult(List<Post> posts, String blogName) {
        if (posts.size() == 0) {
            Toast.makeText(getActivity(), getString(R.string.error_text_no_posts), Toast.LENGTH_LONG).show();
            fieldEnterBlogName.setText(blogName);
            postFragmentProgressBar.setVisibility(View.GONE);
            postFragmentProgressBar.setIndeterminate(false);
            return;
        }
        postFragmentProgressBar.setVisibility(View.VISIBLE);
        postFragmentProgressBar.setIndeterminate(true);
        if (getActivity() != null) {
            fieldEnterBlogName.setText(blogName);
            PostsAdapter postsAdapter = new PostsAdapter(getActivity(), posts, onPostClickListener);
            recyclerView.setAdapter(postsAdapter);
        }
        postFragmentProgressBar.setVisibility(View.GONE);
        postFragmentProgressBar.setIndeterminate(false);
        recyclerView.setVisibility(View.VISIBLE);
        mSwipeRefresh.setRefreshing(false);
    }

    private final PostsAdapter.OnPostAdapterClickListener onPostClickListener = new PostsAdapter.OnPostAdapterClickListener() {
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
        public void onClickLike(final int position, final List<Post> posts, final ImageView imageView, Boolean isLike, TextView likeCount) {

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

        @Override
        public void onVideoClick(String videoUrl) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
        }
    };

    @Override
    public void onRefresh() {
        if (fieldEnterBlogName.getText().toString().equals("")) {
            loadUserDashboard();
        } else {
            if (getActivity() != null) {
                MainActivity activity = (MainActivity) getActivity();
                activity.search(fieldEnterBlogName.getText().toString());
            }
        }
    }

    public interface GetSearchField {
        void searchVis(LinearLayout linearLayout);
    }
}







