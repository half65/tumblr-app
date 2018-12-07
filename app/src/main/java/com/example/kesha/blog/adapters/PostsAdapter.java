package com.example.kesha.blog.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.utils.GlideApp;
import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.VideoPost;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PhotoPostViewHolder>  {
    private LayoutInflater inflater;
    private List<Post> posts;
    private Activity activity;


    public interface OnPostAdapterClickListener {
        void onImageClick(String imageURL);

        void onBodyTextClick(TextView textView);

        void onClickLike(int position,List<Post> posts, ImageView imageView, Boolean isLike);

        void onClickReblog(int position,List<Post> posts);

        void onVideoClick(String videoUrl);

    }

    private OnPostAdapterClickListener onPostAdapterClickListener;

    public PostsAdapter(Activity activity, List<Post> posts, OnPostAdapterClickListener onPostAdapterClickListener) {
        this.posts = posts;
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        this.onPostAdapterClickListener = onPostAdapterClickListener;

    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private static String getLikePostImg(final PhotoPost photoPost, final int iteration) {
        String imageUrl = photoPost.getPhotos().get(iteration).getSizes().get(1).getUrl();
        return imageUrl;
    }

    @NonNull
    @Override
    public PhotoPostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_post_fragment_recycler, viewGroup, false);
        return new PhotoPostViewHolder(view);
    }

    private String getLikeVideo(VideoPost videoPost){
        return videoPost.getThumbnailUrl();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull PhotoPostViewHolder viewHolder, int i) {
        viewHolder.likeBtn.setImageResource(0);
        viewHolder.lickedPostLinear.setVisibility(GONE);
        viewHolder.progressBarLickedPost.setIndeterminate(true);
        viewHolder.progressBarLickedPost.setVisibility(VISIBLE);
        viewHolder.tagText.setText(null);
        viewHolder.textBodyTitle.setVisibility(GONE);
        viewHolder.hintTextBody.setVisibility(GONE);
        viewHolder.blogAvatar.setImageResource(0);
        viewHolder.blogName.setText(null);
        viewHolder.gridRoot.removeAllViews();
        if(posts.get(viewHolder.getAdapterPosition()).isLiked()){
            viewHolder.likeBtn.setImageResource(R.drawable.ic_like_24dp);
            viewHolder.likeBtn.setImageAlpha(254);
        }else {
            viewHolder.likeBtn.setImageResource(R.drawable.ic_unlike_24dp);
            viewHolder.likeBtn.setImageAlpha(255);
        }

        final int position = viewHolder.getAdapterPosition();

        String blogName = posts.get(position).getBlogName();
        long likes = posts.get(position).getNoteCount();
        String avatarUrl = Utils.getAvatarUrl(blogName, 256);
        GlideApp.with(activity)
                .load(avatarUrl)
                .placeholder(R.drawable.text_tumblr_com)
                .transform(new RoundedCorners(10))
                .into(viewHolder.blogAvatar);
        viewHolder.blogName.setText(blogName);
        String countLike = String.valueOf((int) likes);
        viewHolder.likes.setText(countLike);

        @SuppressLint("SimpleDateFormat")
        String newFormatDate = new SimpleDateFormat("dd MMM, HH:mm")
                .format(new Date(posts.get(position).getTimestamp() * 1000));
        viewHolder.timePost.setText(newFormatDate);

        switch (posts.get(viewHolder.getAdapterPosition()).getType()) {
            case TEXT:
                TextPost textPost = (TextPost) posts.get(viewHolder.getAdapterPosition());
                if (textPost.getBody().contains("img src=\"")) {
                    String[] body = textPost.getBody().split("img src=\"");
                    String[] body2 = body[1].split("\" data-orig-height");
                    String imageUrl = body2[0];

                    LinearLayout.LayoutParams rowItemParams = new LinearLayout
                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    setImageLikedPost(rowItemParams, imageUrl, viewHolder.gridRoot);
                }
                if (textPost.getTitle() != null) {
                    viewHolder.textBodyTitle.setText(textPost.getTitle());
                    viewHolder.textBodyTitle.setVisibility(VISIBLE);
                }
                String textBodyTextPost = Html.fromHtml(textPost.getBody()).toString();
                if (textBodyTextPost.length() != 0) {
                    while (textBodyTextPost.charAt(textBodyTextPost.length() - 1) == '\n') {
                        textBodyTextPost = textBodyTextPost.substring(0, textBodyTextPost.length() - 1);
                    }

                    viewHolder.bodyText.setText(textBodyTextPost);
                    viewHolder.bodyText.setVisibility(VISIBLE);
                    if (viewHolder.bodyText.getLineCount() > 15) {
                        viewHolder.hintTextBody.setVisibility(VISIBLE);
                    }
                }
                viewHolder.progressBarLickedPost.setIndeterminate(false);
                viewHolder.progressBarLickedPost.setVisibility(GONE);
                viewHolder.lickedPostLinear.setVisibility(VISIBLE);

                break;

            case PHOTO:
                PhotoPost ps = (PhotoPost) posts.get(position);
                String textPhotoPost = ps.getCaption();
                if (textPhotoPost != null) {
                    String textBodyPhotoPost = Html.fromHtml(textPhotoPost).toString();
                    if (textBodyPhotoPost.length() != 0) {
                        while (textBodyPhotoPost.charAt(textBodyPhotoPost.length() - 1) == '\n') {
                            textBodyPhotoPost = textBodyPhotoPost.substring(0, textBodyPhotoPost.length() - 1);
                        }
                    }

                    viewHolder.bodyText.setText(textBodyPhotoPost);
                    viewHolder.bodyText.setVisibility(VISIBLE);
                    if (viewHolder.bodyText.getLineCount() > 15) {
                        viewHolder.hintTextBody.setVisibility(VISIBLE);
                    }

                }
                if (ps.getTags() != null) {
                    for (int j = 0; j < ps.getTags().size(); j++) {
                        viewHolder.tagText.append(String.format("#%s ", ps.getTags().get(j)));
                    }
                    viewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                }

                int total = ((PhotoPost) posts.get(position)).getPhotos().size();
                int columns = 2;
                int rows = total / columns;
                int imagePosition = 0;

                if (total != 1) {
                    for (int j = 0; j < rows; j++) {
                        LinearLayout rowLayout = new LinearLayout(activity);
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams rowItemParams = new LinearLayout
                                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        rowItemParams.weight = 1;
                        rowItemParams.setMargins(3, 2, 3, 2);
                        for (int k = 0; k < 2; k++) {
                            ImageView imageView = new ImageView(activity);
                            final String imageURL = getLikePostImg(((PhotoPost) posts.get(position)), imagePosition);
                            GlideApp.with(activity)
                                    .load(imageURL)
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .into(imageView);
                            imageView.setVisibility(VISIBLE);
                            rowLayout.addView(imageView, rowItemParams);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onPostAdapterClickListener.onImageClick(imageURL);
                                }
                            });
                            imagePosition++;
                        }
                        viewHolder.gridRoot.addView(rowLayout);
                    }
                    if ((total % columns) == 1) {
                        LinearLayout.LayoutParams rowItemParams = new LinearLayout
                                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        String imageUrl = getLikePostImg(((PhotoPost) posts.get(position)), imagePosition);
                        setImageLikedPost(rowItemParams, imageUrl, viewHolder.gridRoot);
                    }


                    viewHolder.progressBarLickedPost.setIndeterminate(false);
                    viewHolder.progressBarLickedPost.setVisibility(GONE);
                    viewHolder.lickedPostLinear.setVisibility(VISIBLE);
                } else {
                    LinearLayout.LayoutParams rowItemParams = new LinearLayout
                            .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    String imageUrl = getLikePostImg(((PhotoPost) posts.get(position)), total - 1);
                    setImageLikedPost(rowItemParams, imageUrl, viewHolder.gridRoot);
                    viewHolder.progressBarLickedPost.setIndeterminate(false);
                    viewHolder.progressBarLickedPost.setVisibility(GONE);
                    viewHolder.lickedPostLinear.setVisibility(VISIBLE);
                }

                break;
            case VIDEO:
                VideoPost videoPost = (VideoPost) posts.get(position);
                String textVideoPost = videoPost.getCaption();
                if (textVideoPost != null) {
                    String textBodyVideoPost = android.text.Html.fromHtml(textVideoPost).toString();
                    if (textBodyVideoPost.length() != 0) {
                        while (textBodyVideoPost.charAt(textBodyVideoPost.length() - 1) == '\n') {
                            textBodyVideoPost = textBodyVideoPost.substring(0, textBodyVideoPost.length() - 1);
                        }
                    }

                    viewHolder.bodyText.setText(textBodyVideoPost);
                    viewHolder.bodyText.setVisibility(VISIBLE);
                    if(viewHolder.bodyText.getLineCount()>15){
                        viewHolder.hintTextBody.setVisibility(VISIBLE);
                    }

                }
                if (videoPost.getTags() != null) {
                    for (int j = 0; j < videoPost.getTags().size(); j++) {
                        viewHolder.tagText.append(String.format("#%s ", videoPost.getTags().get(j)));
                    }
                    viewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                }
                FrameLayout frameLayout = new FrameLayout(activity);
                FrameLayout.LayoutParams rowItemParams = new FrameLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rowItemParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(activity);

                String imageUrlVideo = getLikeVideo(((VideoPost) posts.get(position)));
                final String videoUrl = ((VideoPost) posts.get(position)).getPermalinkUrl();
                GlideApp.with(activity)
                        .load(imageUrlVideo)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(imageView);
                FrameLayout.LayoutParams rowPlayParams = new FrameLayout
                        .LayoutParams(100,100);
                rowPlayParams.gravity = Gravity.CENTER;
                ImageView playImg = new ImageView(activity);
                playImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                playImg.setImageResource(R.drawable.play_active);
                frameLayout.addView(imageView,rowItemParams);
                frameLayout.addView(playImg,rowPlayParams);
                viewHolder.gridRoot.addView(frameLayout);
                playImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPostAdapterClickListener.onVideoClick(videoUrl);
                    }
                });

                viewHolder.progressBarLickedPost.setIndeterminate(false);
                viewHolder.progressBarLickedPost.setVisibility(GONE);
                viewHolder.lickedPostLinear.setVisibility(VISIBLE);
                break;
        }
    }

    private void setImageLikedPost(LinearLayout.LayoutParams rowItemParams, final String imageUrl
            , LinearLayout gridRoot) {
        LinearLayout rowLayout = new LinearLayout(activity);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowItemParams.weight = 1;
        rowItemParams.setMargins(3, 2, 3, 10);
        ImageView imageView = new ImageView(activity);
        GlideApp.with(activity)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostAdapterClickListener.onImageClick(imageUrl);
            }
        });
        rowLayout.addView(imageView, rowItemParams);
        gridRoot.addView(rowLayout);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PhotoPostViewHolder extends RecyclerView.ViewHolder {

        private TextView bodyText, blogName, timePost, tagText, textBodyTitle, hintTextBody, likes;
        private ProgressBar progressBarLickedPost;
        private LinearLayout gridRoot;
        private ImageView blogAvatar, likeBtn, reblogBtn;
        private LinearLayout tagRootLinearLayout;
        private LinearLayout lickedPostLinear;
        private LinearLayout textPostLinear;
        private boolean isLike;


        public PhotoPostViewHolder(@NonNull View itemView) {
            super(itemView);
            hintTextBody = itemView.findViewById(R.id.hint_text_body_post_text_view);
            textBodyTitle = itemView.findViewById(R.id.text_post_body_title);
            tagText = itemView.findViewById(R.id.tag_text_liked_post_post_fragm);
            progressBarLickedPost = itemView.findViewById(R.id.progress_bar_post);
            lickedPostLinear = itemView.findViewById(R.id.post_linear);
            tagRootLinearLayout = itemView.findViewById(R.id.grid_root_post_tag_linear_layout);
            textPostLinear = itemView.findViewById(R.id.grid_root_text_post_linear_layout);
            bodyText = itemView.findViewById(R.id.text_body_post);
            gridRoot = itemView.findViewById(R.id.grid_root_post_layout);
            blogName = itemView.findViewById(R.id.blog_name_post_recycler_textview);
            timePost = itemView.findViewById(R.id.time_post_recycler_textview);
            blogAvatar = itemView.findViewById(R.id.avatar_post_recycler_image);
            likeBtn = itemView.findViewById(R.id.button_like);
            reblogBtn = itemView.findViewById(R.id.reblog_button);
            likes = itemView.findViewById(R.id.likes);

            bodyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPostAdapterClickListener.onBodyTextClick(bodyText);
                }
            });
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPostAdapterClickListener.onClickLike(getAdapterPosition(),posts, likeBtn,isLike);
                }
            });

            reblogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPostAdapterClickListener.onClickReblog(getAdapterPosition(),posts);
                }
            });
        }
    }
}
