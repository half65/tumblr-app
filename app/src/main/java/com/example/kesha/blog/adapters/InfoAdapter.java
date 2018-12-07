package com.example.kesha.blog.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.MyWebView;
import com.example.kesha.blog.R;
import com.example.kesha.blog.utils.GlideApp;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.types.AudioPost;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.VideoPost;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.view.ViewGroup.*;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {
    public static String TAG = InfoAdapter.class.getSimpleName();
    private List<Post> posts;
    private LayoutInflater inflater;
    private Activity activity;

    public interface OnInfoAdapterClickListener {
        void onImageClick(String imageURL);

        void onBodyTextClick(TextView textView);

        void onVideoClick(String videoUrl);

        void onClickLike(int position, List<Post> posts, ImageView imageView, Boolean isLike);

        void onClickReblog(int position, List<Post> posts);
    }

    private OnInfoAdapterClickListener onImageClickListener;


    public InfoAdapter(Activity activity, List<Post> posts, OnInfoAdapterClickListener listener) {
        this.posts = posts;
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        onImageClickListener = listener;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_info_recycler, viewGroup, false);
        return new InfoViewHolder(view);
    }

    private String getLikePostImg(PhotoPost photoPost, int iteration) {
        return photoPost.getPhotos().get(iteration).getSizes().get(1).getUrl();
    }

    private String getLikeVideo(VideoPost videoPost) {
        return videoPost.getThumbnailUrl();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final InfoViewHolder infoViewHolder, int i) {
        infoViewHolder.lickedPostLinear.setVisibility(GONE);
        infoViewHolder.progressBarLickedPost.setIndeterminate(true);
        infoViewHolder.progressBarLickedPost.setVisibility(VISIBLE);
        infoViewHolder.tagText.setText(null);
        infoViewHolder.textBodyTitle.setVisibility(GONE);
        infoViewHolder.hintTextBody.setVisibility(GONE);
        infoViewHolder.blogAvatar.setImageResource(0);
        infoViewHolder.blogName.setText(null);
        infoViewHolder.gridRoot.removeAllViews();
        if (posts.get(infoViewHolder.getAdapterPosition()).isLiked()) {
            infoViewHolder.likeBtn.setImageResource(R.drawable.ic_like_24dp);
            infoViewHolder.likeBtn.setImageAlpha(254);
        } else {
            infoViewHolder.likeBtn.setImageResource(R.drawable.ic_unlike_24dp);
            infoViewHolder.likeBtn.setImageAlpha(255);
        }

        final int position = infoViewHolder.getAdapterPosition();

        String blogName = posts.get(position).getBlogName();
        long likes = posts.get(position).getNoteCount();
        String avatarUrl = Utils.getAvatarUrl(blogName, 256);
        GlideApp.with(activity)
                .load(avatarUrl)
                .placeholder(R.drawable.text_tumblr_com)
                .transform(new RoundedCorners(10))
                .into(infoViewHolder.blogAvatar);
        infoViewHolder.blogName.setText(blogName);
        String countLike = String.valueOf((int) likes);
        if (countLike != null) {
            infoViewHolder.likes.setText(countLike);
        }

        @SuppressLint("SimpleDateFormat")
        String newFormatDate = new SimpleDateFormat(activity.getString(R.string.textFormatUpdateLikedPost))
                .format(new java.util.Date(posts.get(position).getTimestamp() * 1000));
        infoViewHolder.timePost.setText(newFormatDate);

        switch (posts.get(infoViewHolder.getAdapterPosition()).getType()) {
            case TEXT:
                TextPost textPost = (TextPost) posts.get(infoViewHolder.getAdapterPosition());
                if (textPost.getBody().contains("img src=\"")) {
                    String[] body = textPost.getBody().split("img src=\"");
                    String[] body2 = body[1].split("\" data-orig-height");
                    String imageUrl = body2[0];

                    LinearLayout.LayoutParams rowItemParams = new LinearLayout
                            .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    setImageLikedPost(rowItemParams, imageUrl, infoViewHolder.gridRoot);
                }
                if (textPost.getTitle() != null) {
                    infoViewHolder.textBodyTitle.setText(textPost.getTitle());
                    infoViewHolder.textBodyTitle.setVisibility(VISIBLE);
                }
                String textBodyTextPost = android.text.Html.fromHtml(textPost.getBody()).toString();
                while (textBodyTextPost.charAt(textBodyTextPost.length() - 1) == '\n') {
                    textBodyTextPost = textBodyTextPost.substring(0, textBodyTextPost.length() - 1);
                }
                infoViewHolder.bodyText.setText(textBodyTextPost);
                infoViewHolder.bodyText.setVisibility(VISIBLE);
                if (infoViewHolder.bodyText.getLineCount() > 15) {
                    infoViewHolder.hintTextBody.setVisibility(VISIBLE);
                }

                infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);

                break;

            case PHOTO:
                PhotoPost ps = (PhotoPost) posts.get(position);
                String textPhotoPost = ps.getCaption();
                if (textPhotoPost != null) {
                    String textBodyPhotoPost = android.text.Html.fromHtml(textPhotoPost).toString();
                    if (textBodyPhotoPost.length() != 0) {
                        while (textBodyPhotoPost.charAt(textBodyPhotoPost.length() - 1) == '\n') {
                            textBodyPhotoPost = textBodyPhotoPost.substring(0, textBodyPhotoPost.length() - 1);
                        }
                    }

                    infoViewHolder.bodyText.setText(textBodyPhotoPost);
                    infoViewHolder.bodyText.setVisibility(VISIBLE);
                    if (infoViewHolder.bodyText.getLineCount() > 15) {
                        infoViewHolder.hintTextBody.setVisibility(VISIBLE);
                    }

                }
                if (ps.getTags() != null) {
                    for (int j = 0; j < ps.getTags().size(); j++) {
                        infoViewHolder.tagText.append(String.format("#%s ", ps.getTags().get(j)));
                    }
                    infoViewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
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
                                .LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
                            imageView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onImageClickListener.onImageClick(imageURL);
                                }
                            });
                            imagePosition++;
                        }
                        infoViewHolder.gridRoot.addView(rowLayout);
                    }
                    if ((total % columns) == 1) {
                        LinearLayout.LayoutParams rowItemParams = new LinearLayout
                                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        String imageUrl = getLikePostImg(((PhotoPost) posts.get(position)), imagePosition);
                        setImageLikedPost(rowItemParams, imageUrl, infoViewHolder.gridRoot);
                    }


                    infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                    infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                    infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                } else {
                    LinearLayout.LayoutParams rowItemParams = new LinearLayout
                            .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    String imageUrl = getLikePostImg(((PhotoPost) posts.get(position)), total - 1);
                    setImageLikedPost(rowItemParams, imageUrl, infoViewHolder.gridRoot);
                    infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                    infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                    infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                }

                break;
            case VIDEO:
                final VideoPost videoPost = (VideoPost) posts.get(position);
                String textVideoPost = videoPost.getCaption();
                if (textVideoPost != null) {
                    String textBodyVideoPost = android.text.Html.fromHtml(textVideoPost).toString();
                    if (textBodyVideoPost.length() != 0) {
                        while (textBodyVideoPost.charAt(textBodyVideoPost.length() - 1) == '\n') {
                            textBodyVideoPost = textBodyVideoPost.substring(0, textBodyVideoPost.length() - 1);
                        }
                    }

                    infoViewHolder.bodyText.setText(textBodyVideoPost);
                    infoViewHolder.bodyText.setVisibility(VISIBLE);
                    if (infoViewHolder.bodyText.getLineCount() > 15) {
                        infoViewHolder.hintTextBody.setVisibility(VISIBLE);
                    }

                }
                if (videoPost.getTags() != null) {
                    for (int j = 0; j < videoPost.getTags().size(); j++) {
                        infoViewHolder.tagText.append(String.format("#%s ", videoPost.getTags().get(j)));
                    }
                    infoViewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                }
                FrameLayout frameLayout = new FrameLayout(activity);
                FrameLayout.LayoutParams rowItemParams = new FrameLayout
                        .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                rowItemParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(activity);

                String imageUrlVideo = getLikeVideo(((VideoPost) posts.get(position)));
                final String videoUrl = ((VideoPost) posts.get(position)).getPermalinkUrl();
                GlideApp.with(activity)
                        .load(imageUrlVideo)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(imageView);
                FrameLayout.LayoutParams rowPlayParams = new FrameLayout
                        .LayoutParams(100, 100);
                rowPlayParams.gravity = Gravity.CENTER;
                ImageView playImg = new ImageView(activity);
                playImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                playImg.setImageResource(R.drawable.play_active);
                frameLayout.addView(imageView, rowItemParams);
                frameLayout.addView(playImg, rowPlayParams);
                infoViewHolder.gridRoot.addView(frameLayout);
                playImg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (videoUrl == null) {
                            String bodyVideo = videoPost.getVideos().get(0).getEmbedCode();
                            String[] bodyV = bodyVideo.split("source src=\"");
                            String[] bodyV2 = bodyV[1].split("\" type");
                            onImageClickListener.onVideoClick(bodyV2[0]);
                        } else {
                            onImageClickListener.onVideoClick(videoUrl);
                        }
                    }
                });

                infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                break;

            case AUDIO:
                AudioPost audioPost = (AudioPost) posts.get(infoViewHolder.getAdapterPosition());
                String textAudioPost = audioPost.getCaption();
                if (textAudioPost != null) {
                    String textBodyVideoPost = android.text.Html.fromHtml(textAudioPost).toString();
                    if (textBodyVideoPost.length() != 0) {
                        while (textBodyVideoPost.charAt(textBodyVideoPost.length() - 1) == '\n') {
                            textBodyVideoPost = textBodyVideoPost.substring(0, textBodyVideoPost.length() - 1);
                        }
                    }

                    infoViewHolder.bodyText.setText(textBodyVideoPost);
                    infoViewHolder.bodyText.setVisibility(VISIBLE);
                    if (infoViewHolder.bodyText.getLineCount() > 15) {
                        infoViewHolder.hintTextBody.setVisibility(VISIBLE);
                    }

                }
                if (audioPost.getTags() != null) {
                    for (int j = 0; j < audioPost.getTags().size(); j++) {
                        infoViewHolder.tagText.append(String.format("#%s ", audioPost.getTags().get(j)));
                    }
                    infoViewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                }
                FrameLayout linearLayout = new FrameLayout(activity);
                LinearLayout.LayoutParams layoutParams = new LinearLayout
                        .LayoutParams(LayoutParams.MATCH_PARENT,6800);
                MyWebView myWebView = new MyWebView(activity);
                WebViewClient webViewClient = new WebViewClient();
                myWebView.setWebViewClient(webViewClient);
                infoViewHolder.gridRoot.addView(myWebView,layoutParams);
                myWebView.load(audioPost.getAudioUrl());


                infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                Log.e(TAG, "music");
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
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClickListener.onImageClick(imageUrl);
            }
        });
        rowLayout.addView(imageView, rowItemParams);
        gridRoot.addView(rowLayout);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class InfoViewHolder extends RecyclerView.ViewHolder {
        private TextView bodyText, blogName, timePost, tagText, textBodyTitle, hintTextBody, likes;
        private ProgressBar progressBarLickedPost;
        private LinearLayout gridRoot;
        private ImageView blogAvatar, likeBtn, reblogBtn;
        private LinearLayout tagRootLinearLayout;
        private LinearLayout lickedPostLinear;
        private LinearLayout textPostLinear;
        private FrameLayout videoFrameLayout;
        private boolean isLike;

        @SuppressLint("ClickableViewAccessibility")
        public InfoViewHolder(View view) {
            super(view);
            likeBtn = view.findViewById(R.id.button_like_info);
            reblogBtn = view.findViewById(R.id.reblog_button_info);
            hintTextBody = view.findViewById(R.id.hint_text_body_text_view);
            textBodyTitle = view.findViewById(R.id.text_body_title);
            tagText = view.findViewById(R.id.tag_text_liked_post_info_fragm);
            progressBarLickedPost = view.findViewById(R.id.progress_bar_liked_post);
            lickedPostLinear = view.findViewById(R.id.post_liked_linear);
            tagRootLinearLayout = view.findViewById(R.id.grid_root_tag_linear_layout);
            textPostLinear = view.findViewById(R.id.grid_root_text_linear_layout);
            bodyText = view.findViewById(R.id.text_body);
            gridRoot = view.findViewById(R.id.grid_root_layout);
            blogName = view.findViewById(R.id.blog_name_info_recycler_textview);
            timePost = view.findViewById(R.id.time_info_recycler_textview);
            blogAvatar = view.findViewById(R.id.avatar_post_info_recycler_test_image);
            likes = itemView.findViewById(R.id.likes_info);

            bodyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClickListener.onBodyTextClick(bodyText);
                }
            });
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClickListener.onClickLike(getAdapterPosition(), posts, likeBtn, isLike);
                }
            });

            reblogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClickListener.onClickReblog(getAdapterPosition(), posts);
                }
            });
        }
    }
}
