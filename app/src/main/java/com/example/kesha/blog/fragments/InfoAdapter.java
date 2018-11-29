package com.example.kesha.blog.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.UtilsPackage.GlideApp;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder>{
    private List<Post> posts;
    private LayoutInflater inflater;
    private Activity activity;

    public InfoAdapter(Activity activity,List<Post> posts) {
        this.posts = posts;
        this.activity = activity;
        inflater = activity.getLayoutInflater();
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_info_recycler, viewGroup, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InfoViewHolder infoViewHolder, int i) {
        final JumblrClient client = TumblrApplication.getClient();
        final ImageView[] postImage = new ImageView[]{infoViewHolder.image1, infoViewHolder.image2
                , infoViewHolder.image3, infoViewHolder.image4, infoViewHolder.image5, infoViewHolder.image6
                , infoViewHolder.image7, infoViewHolder.image8, infoViewHolder.image9, infoViewHolder.image10};
        final int position = i;
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (posts.get(position).getType()){
                    case TEXT:
                        TextPost textPost = (TextPost) posts.get(position);
                        infoViewHolder.postText.setText((textPost.getTitle()));
                        break;
                    case PHOTO:
                        final Blog blog = client.blogInfo(String.format("%s.tumblr.com",posts.get(position).getBlogName()));
                        final String avatarPostUrl = blog.avatar(256);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GlideApp.with(activity)
                                        .load(avatarPostUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .into(infoViewHolder.blogAvatar);
                                infoViewHolder.blogName.setText(blog.getName());
                            }
                        });
                        @SuppressLint("SimpleDateFormat")
                        String newFormatDate = new SimpleDateFormat("dd MMM, HH:mm")
                                .format(new java.util.Date(posts.get(position).getTimestamp()*1000));
                        infoViewHolder.timePost.setText(newFormatDate);

                        for (int j = 0; j <((PhotoPost) posts.get(position)).getPhotos().size() ; j++) {

                            final String url = ((PhotoPost) posts.get(position)).getPhotos().get(j).getSizes().get(5).getUrl();
                            final int iteration = j;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    postImage[iteration].setMaxHeight(((PhotoPost) posts.get(position)).getPhotos().get(iteration).getOriginalSize().getHeight());
                                    GlideApp.with(activity)
                                            .load(url)
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .into(postImage[iteration]);
                                    postImage[iteration].setVisibility(View.VISIBLE);
                                }
                            });
                        }


                        /*activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PostAdapter postAdapter = new PostAdapter(activity,(PhotoPost) posts.get(position));
                                infoViewHolder.recyclerViewImage.setAdapter(postAdapter);
                                infoViewHolder.postText.setText(((PhotoPost) posts.get(position)).getCaption());
                            }
                        });*/

                        break;
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class InfoViewHolder extends RecyclerView.ViewHolder {
        private TextView postText,blogName,timePost;
        private ImageView blogAvatar,image1,image2,image3,image4,image5,image6,image7,image8,image9,image10;

        public InfoViewHolder(View view) {
            super(view);
           //recyclerViewImage = view.findViewById(R.id.info_image_recycler);
           postText = view.findViewById(R.id.text_post_info_recycler);
            blogName = view.findViewById(R.id.blog_name_info_recycler_textview);
            timePost = view.findViewById(R.id.time_info_recycler_textview);
           blogAvatar = view.findViewById(R.id.avatar_post_info_recycler_image);
            image1 = view.findViewById(R.id.imageView1);
            image2 = view.findViewById(R.id.imageView2);
            image3 = view.findViewById(R.id.imageView3);
            image4 = view.findViewById(R.id.imageView4);
            image5 = view.findViewById(R.id.imageView5);
            image6 = view.findViewById(R.id.imageView6);
            image7 = view.findViewById(R.id.imageView7);
            image8 = view.findViewById(R.id.imageView8);
            image9 = view.findViewById(R.id.imageView9);
            image10 = view.findViewById(R.id.imageView10);

        }
    }
}
