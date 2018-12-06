package com.example.kesha.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.adapters.PostsAdapter;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Post;

import java.util.List;


public class PostsFragment extends Fragment {
    private final String TAG = PostsAdapter.class.getSimpleName();
    private RecyclerView recyclerView;
    private EditText fieldEnterBlogName;
    private ProgressBar postFragmentProgressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_post_list, container, false);
        recyclerView = fragmentView.findViewById(R.id.post_recycler);
        recyclerView.setVisibility(View.GONE);
        postFragmentProgressBar = fragmentView.findViewById(R.id.post_fragment_progressBar);
        postFragmentProgressBar.setVisibility(View.VISIBLE);
        postFragmentProgressBar.setIndeterminate(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        fieldEnterBlogName = fragmentView.findViewById(R.id.editText2);
        Button btnGo = fragmentView.findViewById(R.id.button);
        btnGo.setOnClickListener(startSearchBlogListener);
        return fragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserDashboard();
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        *//*new Thread() {
            @Override
            public void run() {
                try {
                    final List<Post> posts = client.userDashboard();
                    if (getActivity() != null) {
                        adapterFragment = new PostsAdapter(getActivity(), posts, onPostClickListener);
                        getActivity().runOnUiThread(new Runnable() {  //check for null
                            @Override
                            public void run() {
                                recyclerView.setAdapter(adapterFragment);
                            }
                        });
                    }
                } catch (JumblrException e) {
                    Log.e("Err", "code = " + e.getResponseCode());
                }

            }
        }.start();*//*
    }
*/
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
                            Toast.makeText(getActivity(), "loading error: " + reason, Toast.LENGTH_LONG).show();
                            postFragmentProgressBar.setVisibility(View.GONE);
                            postFragmentProgressBar.setIndeterminate(false);
                        }
                    });
            }
        });
    }

    View.OnClickListener startSearchBlogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postFragmentProgressBar.setVisibility(View.VISIBLE);
            postFragmentProgressBar.setIndeterminate(true);
            String blogName = fieldEnterBlogName.getText().toString();
            Utils.loadBlogPosts(blogName, new Utils.JumblrPostCallback() {
                @Override
                public void onPostLoaded(final List<Post> posts, JumblrClient client) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PostsAdapter postsAdapter = new PostsAdapter(getActivity(), posts, onPostClickListener);
                                recyclerView.setAdapter(postsAdapter);
                                postFragmentProgressBar.setVisibility(View.GONE);
                                postFragmentProgressBar.setIndeterminate(false);
                                recyclerView.setVisibility(View.VISIBLE);
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
                                Toast.makeText(getActivity(), "loading error: " + reason, Toast.LENGTH_LONG).show();
                                postFragmentProgressBar.setVisibility(View.GONE);
                                postFragmentProgressBar.setIndeterminate(false);
                            }
                        });
                }
            });
        }
    };

    private final PostsAdapter.OnPostAdapterClickListener onPostClickListener = new PostsAdapter.OnPostAdapterClickListener() {
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

        @Override
        public void onClickLike(final int position,final List<Post> posts) {
            final JumblrClient client = TumblrApplication.getClient();
                if(posts.get(position).isLiked()){

                    new Thread() {
                        @Override
                        public void run() {

                        }
                    }.start();
                    Log.e(TAG, "unlike()");

                }else {

                    new Thread() {
                        @Override
                        public void run() {
                            posts.get(position).unlike();
                        }
                    }.start();
                    Log.e(TAG, "like()");

                }
        }

        @Override
        public void onClickReblog(final int position,final List<Post> posts) {
            final JumblrClient client = TumblrApplication.getClient();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.postReblog(client.user().getBlogs().get(0).getName(), posts.get(position).getId(), posts.get(position).getReblogKey());
                    /*client.postReblog(posts.get(position).getBlogName()
                            , posts.get(position).getId(), posts.get(position).getReblogKey());*/
                }
            }).start();

        }
    };

    /*@Override
        public void onClick(View v) {
            final Post currentPost = posts.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.button_like:
                    if (currentPost.isLiked()) {
                        new Thread() {
                            @Override
                            public void run() {
                                currentPost.unlike();
                            }
                        };
                        Log.e("ASD", "unlike()");
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                currentPost.like();
                            }
                        };
                        Log.e("ASD", "like()");
                    }
                    break;
                case R.id.reblog_button:
                    new Thread() {
                        @Override
                        public void run() {
                            currentPost.reblog(TumblrApplication.getClient().user().getName());
                        }
                    };
                    Log.e("ASD", "reblog()");
                    break;
            }
        }*/
}







