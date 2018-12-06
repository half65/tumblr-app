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
import android.widget.TextView;

import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Post;

import java.util.List;


public class PostListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText search;
    private Button btnGo;
    private PostAdapterFragment adapterFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_post_list, container, false);
        recyclerView = fragmentView.findViewById(R.id.post_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        search = fragmentView.findViewById(R.id.editText2);
        btnGo = fragmentView.findViewById(R.id.button);
        btnGo.setOnClickListener(PostListFragment.this);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final JumblrClient client = TumblrApplication.getClient();
        new Thread() {
            @Override
            public void run() {
                try {
                    final List<Post> posts = client.userDashboard();
                    if (getActivity() != null) {
                        adapterFragment = new PostAdapterFragment(getActivity(), posts, onPostClickListener);
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
        }.start();
    }

    @Override
    public void onClick(View v) {
        final String tag = search.getText().toString();
        if (!tag.equals("")) {
            new Thread() {
                @Override
                public void run() {

                    /*List<Post> posts = TumblrApplication.getClient().tagged(tag);
                    if(posts == null){

                        return;
                    }
                    adapterFragment.setPosts(posts);*/
                }
            }.start();
        } else ;
    }

    private final PostAdapterFragment.OnPostAdapterClickListener onPostClickListener = new PostAdapterFragment.OnPostAdapterClickListener() {
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
        public void onClickLike(int position, List<Post> posts) {

        }
    };
}







