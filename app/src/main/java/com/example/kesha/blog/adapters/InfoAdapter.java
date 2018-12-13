package com.example.kesha.blog.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.MyWebView;
import com.example.kesha.blog.R;
import com.example.kesha.blog.utils.GlideApp;
import com.example.kesha.blog.utils.SearchClickListener;
import com.example.kesha.blog.utils.Utils;
import com.tumblr.jumblr.types.AudioPost;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.VideoPost;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.view.ViewGroup.GONE;
import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.OnClickListener;
import static android.view.ViewGroup.VISIBLE;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {
    public static String TAG = InfoAdapter.class.getSimpleName();
    private List<Post> posts;
    private LayoutInflater inflater;
    private Activity activity;
    private int screenWidth;

    public interface OnInfoAdapterClickListener {
        void onImageClick(String imageURL);

        void onBodyTextClick(TextView textView);

        void onVideoClick(String videoUrl);

        void onClickLike(int position, List<Post> posts, ImageView imageView, Boolean isLike, TextView likeCount);

        void onClickReblog(int position, List<Post> posts);
    }

    private OnInfoAdapterClickListener onImageClickListener;


    public InfoAdapter(Activity activity, List<Post> posts, OnInfoAdapterClickListener listener) {
        this.posts = posts;
        this.activity = activity;
        if (activity != null)
            inflater = activity.getLayoutInflater();
        onImageClickListener = listener;

        Point size = new Point();
        if (activity != null)
            activity.getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(inflater!=null){
            View view = inflater.inflate(R.layout.item_info_recycler, viewGroup, false);
            return new InfoViewHolder(view);
        }else return null;

    }

    private String getLikePostImg(PhotoPost photoPost, int iteration) {
        return photoPost.getPhotos().get(iteration).getSizes().get(1).getUrl();
    }

    private String getLikeVideo(VideoPost videoPost) {
        return videoPost.getThumbnailUrl();
    }


    private int getPostHeight(int position) {
        PhotoPost photoPost = (PhotoPost) posts.get(position);
        int total = ((PhotoPost) posts.get(position)).getPhotos().size();
        int columns = 2;
        int rows = total / columns;
        int imagePosition = 0;
        int height = 0;

        if (total / columns > 0) {
            height += 6 * rows;
            float imageHeight = (photoPost.getPhotos().get(imagePosition).getSizes().get(1).getHeight()
                    / ((float) photoPost.getPhotos().get(imagePosition).getSizes().get(1).getWidth()))
                    * ((screenWidth-20) / 2);
            int rowHeight = 0;
            rowHeight = rows * (int) imageHeight;
            height += rowHeight;
            if ((total % columns) == 1) {
                height += (photoPost.getPhotos().get(imagePosition).getSizes().get(1).getHeight()
                        / ((float) photoPost.getPhotos().get(imagePosition).getSizes().get(1).getWidth()))
                        * (screenWidth + 100);
            }
        } else {
            height += (photoPost.getPhotos().get(imagePosition).getSizes().get(1).getHeight()
                    / ((float) photoPost.getPhotos().get(imagePosition).getSizes().get(1).getWidth()))
                    * (screenWidth + 100);
        }


        return height;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final InfoViewHolder infoViewHolder, int i) {
        infoViewHolder.tagRootLinearLayout.setVisibility(GONE);
        infoViewHolder.likeBtn.setVisibility(VISIBLE);
        infoViewHolder.reblogBtn.setVisibility(VISIBLE);
        infoViewHolder.textPostLinear.setVisibility(GONE);
        infoViewHolder.lickedPostLinear.setVisibility(GONE);
        infoViewHolder.progressBarLickedPost.setIndeterminate(true);
        infoViewHolder.progressBarLickedPost.setVisibility(VISIBLE);
        infoViewHolder.tagText.setText(null);
        infoViewHolder.textBodyTitle.setVisibility(GONE);
        infoViewHolder.hintTextBody.setVisibility(GONE);
        infoViewHolder.blogAvatar.setImageResource(0);
        infoViewHolder.blogName.setText(null);
        infoViewHolder.gridRoot.removeAllViews();
        infoViewHolder.gridRoot.setLayoutParams(new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (posts.get(infoViewHolder.getAdapterPosition()).isLiked()) {
            infoViewHolder.likeBtn.setImageResource(R.drawable.ic_like_24dp);
            infoViewHolder.isLike = true;
        } else {
            infoViewHolder.likeBtn.setImageResource(R.drawable.ic_unlike_24dp);
            infoViewHolder.isLike = false;
        }
        if (posts.get(infoViewHolder.getAdapterPosition()).getBlogName().equals(Utils.myBlogName)) {
            infoViewHolder.likeBtn.setVisibility(GONE);
            infoViewHolder.reblogBtn.setVisibility(GONE);
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
        infoViewHolder.likes.setText(countLike);


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

                    setImageLikedPost(imageUrl, infoViewHolder);
                }
                if (textPost.getTitle() != null) {
                    infoViewHolder.textBodyTitle.setText(textPost.getTitle());
                    infoViewHolder.textBodyTitle.setVisibility(VISIBLE);
                    infoViewHolder.textPostLinear.setVisibility(VISIBLE);
                }
                String textBodyTextPost = android.text.Html.fromHtml(textPost.getBody()).toString();
                if (textBodyTextPost.length() != 0) {
                    while (textBodyTextPost.charAt(textBodyTextPost.length() - 1) == '\n') {
                        textBodyTextPost = textBodyTextPost.substring(0, textBodyTextPost.length() - 1);
                    }
                    if (textBodyTextPost.length() != 0) {
                        infoViewHolder.bodyText.setText(textBodyTextPost);
                        infoViewHolder.bodyText.setVisibility(VISIBLE);
                        infoViewHolder.textPostLinear.setVisibility(VISIBLE);
                    }
                    if (infoViewHolder.bodyText.getLineCount() > 15) {
                        infoViewHolder.hintTextBody.setVisibility(VISIBLE);
                    }
                }
                if (textPost.getTags().size() != 0) {
                    for (int j = 0; j < textPost.getTags().size(); j++) {
                        infoViewHolder.tagText.append(String.format(activity.getString(R.string.tag_stting_format), textPost.getTags().get(j)));
                    }

                    infoViewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                    infoViewHolder.textPostLinear.setVisibility(VISIBLE);
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

                        if (textBodyPhotoPost.length() != 0) {
                            infoViewHolder.bodyText.setText(textBodyPhotoPost);
                            infoViewHolder.bodyText.setVisibility(VISIBLE);
                            infoViewHolder.textPostLinear.setVisibility(VISIBLE);
                        }
                    }


                    if (infoViewHolder.bodyText.getLineCount() > 15) {
                        infoViewHolder.hintTextBody.setVisibility(VISIBLE);
                    }

                }
                if (ps.getTags().size() != 0) {
                    for (int j = 0; j < ps.getTags().size(); j++) {
                        infoViewHolder.tagText.append(String.format(activity.getString(R.string.tag_stting_format)
                                , ps.getTags().get(j)));
                    }
                    infoViewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                    infoViewHolder.textPostLinear.setVisibility(VISIBLE);
                }

                LinearLayout.LayoutParams gridRootParams = new LinearLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getPostHeight(position));
                infoViewHolder.gridRoot.setLayoutParams(gridRootParams);

                int total = ((PhotoPost) posts.get(position)).getPhotos().size();
                int columns = 2;
                int rows = total / columns;
                int imagePosition = 0;

                if (total / columns > 0) {
                    for (int j = 0; j < rows; j++) {
                        LinearLayout rowLayout = new LinearLayout(activity);
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
                        LinearLayout.LayoutParams rowItemParams = new LinearLayout
                                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        rowItemParams.weight = 1;
                        rowItemParams.setMargins(0,2,0,2);
                        for (int k = 0; k < 2; k++) {
                            ImageView imageView = new ImageView(activity);
                            if(k==0){
                                imageView.setPadding(0,0,3,0);
                            }else {
                                imageView.setPadding(3,0,0,0);
                            }

                            final String imageURL = getLikePostImg(((PhotoPost) posts.get(position)), imagePosition);
                            GlideApp.with(activity)
                                    .load(imageURL)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imageView);
                            imageView.setVisibility(VISIBLE);
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            rowLayout.addView(imageView, rowItemParams);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onImageClickListener.onImageClick(imageURL);
                                }
                            });
                            imagePosition++;
                        }
                        infoViewHolder.gridRoot.addView(rowLayout);
                        infoViewHolder.gridRoot.setVisibility(VISIBLE);
                        infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                        infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                    }
                    if ((total % columns) == 1) {
                        String imageUrl = getLikePostImg(((PhotoPost) posts.get(position)), imagePosition);
                        setImageLikedPost(imageUrl, infoViewHolder);
                    }
                } else {
                    String imageUrl = getLikePostImg(((PhotoPost) posts.get(position)), total - 1);
                    setImageLikedPost(imageUrl, infoViewHolder);
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
                    infoViewHolder.textPostLinear.setVisibility(VISIBLE);
                    if (infoViewHolder.bodyText.getLineCount() > 15) {
                        infoViewHolder.hintTextBody.setVisibility(VISIBLE);
                    }

                }
                if (videoPost.getTags() != null) {
                    for (int j = 0; j < videoPost.getTags().size(); j++) {
                        infoViewHolder.tagText.append(String.format(activity.getString(R.string.tag_stting_format), videoPost.getTags().get(j)));
                    }
                    infoViewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                    infoViewHolder.textPostLinear.setVisibility(VISIBLE);
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
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
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
                        infoViewHolder.tagText.append(String.format(activity.getString(R.string.tag_stting_format), audioPost.getTags().get(j)));
                    }
                    infoViewHolder.tagRootLinearLayout.setVisibility(VISIBLE);
                }
                FrameLayout linearLayout = new FrameLayout(activity);
                LinearLayout.LayoutParams layoutParams = new LinearLayout
                        .LayoutParams(LayoutParams.MATCH_PARENT, 6800);
                MyWebView myWebView = new MyWebView(activity);
                WebViewClient webViewClient = new WebViewClient();
                myWebView.setWebViewClient(webViewClient);
                infoViewHolder.gridRoot.addView(myWebView, layoutParams);
                myWebView.load(audioPost.getAudioUrl());


                infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                Log.e(TAG, "music");
                break;
        }

    }


    private void setImageLikedPost(final String imageUrl
            , InfoAdapter.InfoViewHolder infoViewHolder) {
        LinearLayout rowLayout = new LinearLayout(activity);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowItemParams = new LinearLayout
                .LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowItemParams.weight = 1;
        rowItemParams.setMargins(0, 2, 0, 2);
        ImageView imageView = new ImageView(activity);
        GlideApp.with(activity)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClickListener.onImageClick(imageUrl);
            }
        });
        rowLayout.addView(imageView, rowItemParams);
        infoViewHolder.gridRoot.addView(rowLayout);
        infoViewHolder.progressBarLickedPost.setIndeterminate(false);
        infoViewHolder.progressBarLickedPost.setVisibility(GONE);
        infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class InfoViewHolder extends RecyclerView.ViewHolder {
        private TextView bodyText, blogName, timePost, tagText, textBodyTitle, hintTextBody, likes;
        private ProgressBar progressBarLickedPost;
        private LinearLayout gridRoot;
        private ImageView blogAvatar;
        private ImageButton likeBtn, reblogBtn;
        private LinearLayout tagRootLinearLayout;
        private LinearLayout lickedPostLinear;
        private LinearLayout textPostLinear;
        private boolean isLike;
        private Animation likeZoomAnim;

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
            blogName.setOnClickListener(new SearchClickListener(activity));///////////////////////////////////////
            timePost = view.findViewById(R.id.time_info_recycler_textview);
            blogAvatar = view.findViewById(R.id.avatar_post_info_recycler_test_image);
            likes = itemView.findViewById(R.id.likes_info);
            reblogBtn.setBackgroundResource(R.drawable.reply_24dp);
            likeZoomAnim = AnimationUtils.loadAnimation(activity, R.anim.anim_like);
            bodyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClickListener.onBodyTextClick(bodyText);
                }
            });

            likeBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            likeBtn.startAnimation(likeZoomAnim);
                            onImageClickListener.onClickLike(getAdapterPosition(), posts, likeBtn, isLike, likes);
                            isLike = !isLike;
                            break;
                        case MotionEvent.ACTION_UP:
                            likeBtn.clearAnimation();
                    }
                    return false;
                }
            });
            reblogBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            reblogBtn.startAnimation(likeZoomAnim);
                            onImageClickListener.onClickReblog(getAdapterPosition(), posts);
                            isLike = !isLike;
                            break;
                        case MotionEvent.ACTION_UP:
                            reblogBtn.clearAnimation();
                            break;
                    }
                    return false;
                }
            });
        }
    }
}
