package com.example.kesha.blog.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentSearch extends Fragment {
    private RecyclerView recyclerView;
    private EditText fieldSearch;
    private Button startSearchBtn;
    private ProgressBar progressBarSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = fragmentView.findViewById(R.id.recycler_search_fragment);
        recyclerView.setVisibility(View.GONE);
        fieldSearch = fragmentView.findViewById(R.id.fild_search_edit);
        startSearchBtn = fragmentView.findViewById(R.id.start_search_batton);
        progressBarSearch = fragmentView.findViewById(R.id.progress_bar_search);
        progressBarSearch.setVisibility(View.GONE);
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
            progressBarSearch.setVisibility(View.VISIBLE);
            progressBarSearch.setIndeterminate(true);
            getResponseSearch(fieldSearch.getText().toString());

        }
    };

    private void getResponseSearch(final String tagText) {
        final JumblrClient client = TumblrApplication.getClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("limit", 10);
                params.put("filter", "html");

                final List<Post> posts = client.tagged(tagText/*, params*/);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (posts.size() != 0 && getActivity() != null) {
                            InfoAdapter infoAdapter = new InfoAdapter(getActivity(), posts, onSearchClickListener);
                            recyclerView.setAdapter(infoAdapter);
                            progressBarSearch.setVisibility(View.GONE);
                            progressBarSearch.setIndeterminate(false);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
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
}
