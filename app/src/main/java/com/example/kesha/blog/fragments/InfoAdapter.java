package com.example.kesha.blog.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.R;
import com.example.kesha.blog.UtilsPackage.GlideApp;
import com.example.kesha.blog.UtilsPackage.Utils;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.view.ViewGroup.*;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {
    private List<Post> posts;
    private LayoutInflater inflater;
    private Activity activity;

    public interface OnInfoAdapterClickListener {
        void onImageClick(String imageURL);

        void onBodyTextClick(TextView textView);
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

    private static String getLikePostImg(final PhotoPost photoPost, final int iteration) {
        String imageUrl = photoPost.getPhotos().get(iteration).getSizes().get(1).getUrl();
        return imageUrl;
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

        final int position = infoViewHolder.getAdapterPosition();

        String blogName = posts.get(position).getBlogName();
        String avatarUrl = Utils.getAvatarUrl(blogName, 256);
        GlideApp.with(activity)
                .load(avatarUrl)
                .placeholder(R.drawable.text_tumblr_com)
                .transform(new RoundedCorners(10))
                .into(infoViewHolder.blogAvatar);
        infoViewHolder.blogName.setText(blogName);

        @SuppressLint("SimpleDateFormat")
        String newFormatDate = new SimpleDateFormat("dd MMM, HH:mm")
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
                if(infoViewHolder.bodyText.getLineCount()>15){
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
                    if(infoViewHolder.bodyText.getLineCount()>15){
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
        private TextView bodyText, blogName, timePost, tagText, textBodyTitle,hintTextBody;
        private ProgressBar progressBarLickedPost;
        private LinearLayout gridRoot;
        private ImageView blogAvatar;
        private LinearLayout tagRootLinearLayout;
        private LinearLayout lickedPostLinear;
        private LinearLayout textPostLinear;

        @SuppressLint("ClickableViewAccessibility")
        public InfoViewHolder(View view) {
            super(view);
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

            bodyText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageClickListener.onBodyTextClick(bodyText);
                }
            });
        }
    }
}
