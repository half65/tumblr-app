package com.example.kesha.blog.fragments;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kesha.blog.R;
import com.example.kesha.blog.UtilsPackage.GlideApp;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Photo> photos;
    private LayoutInflater inflater;

    public PostAdapter(Activity activity, PhotoPost photoPost) {
        this.photos = photoPost.getPhotos();
        inflater = activity.getLayoutInflater();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_image_recycler_infofragm, viewGroup, false);
        return new PostViewHolder(view);
    }

    private static void getPhoto(final Photo photo, final Observer observer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String avatarUrl = photo.getSizes().get(2).getUrl();
                observer.update(null, avatarUrl);
            }
        }).start();
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder postViewHolder, int i) {
        getPhoto(photos.get(i), new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                GlideApp.with(inflater.getContext())
                        .load((String) o)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(postViewHolder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public PostViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.post_image_recycler_infofragm);
        }
    }
}
