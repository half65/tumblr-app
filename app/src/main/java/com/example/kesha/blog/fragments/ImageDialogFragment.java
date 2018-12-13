package com.example.kesha.blog.fragments;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.kesha.blog.R;
import com.example.kesha.blog.utils.Constants;
import com.example.kesha.blog.utils.GlideApp;

public class ImageDialogFragment extends DialogFragment {
    private ImageView imageView;
    private String imageURL;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.picture_zoom_fragment, container, false);
        imageView = v.findViewById(R.id.zoom_image);
        Bundle arg = getArguments();
        if (arg != null) imageURL = arg.getString(Constants.KEY_IMAGE_URL);
        if (getActivity() != null) {
            GlideApp.with(getActivity())
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                        }
                    });
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            if(dialog.getWindow()!=null)
            dialog.getWindow().setLayout(width, height);
        }
    }
}
