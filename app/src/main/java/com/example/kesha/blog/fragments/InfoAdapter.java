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
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.kesha.blog.R;
import com.example.kesha.blog.TumblrApplication;
import com.example.kesha.blog.UtilsPackage.GlideApp;
import com.example.kesha.blog.UtilsPackage.Utils;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
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

    public InfoAdapter(Activity activity, List<Post> posts) {
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
       // final JumblrClient client = TumblrApplication.getClient();
        infoViewHolder.blogAvatar.setImageResource(0);
        infoViewHolder.blogName.setText("");
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
                    String url = body2[0];

                    LinearLayout rowLayout = new LinearLayout(activity);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams rowItemParams = new LinearLayout
                            .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    rowItemParams.weight = 1;
                    rowItemParams.setMargins(3, 2, 3, 10);
                    ImageView imageView = new ImageView(activity);

                    GlideApp.with(activity)
                            .load(url)
                            .into(imageView);
                    rowLayout.addView(imageView, rowItemParams);
                    infoViewHolder.gridRoot.addView(rowLayout);
                }
                infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);

                break;

            case PHOTO:
                PhotoPost ps = (PhotoPost) posts.get(position);
                String textPhotoPost = ps.getCaption();
                if (textPhotoPost != null) {
                    infoViewHolder.bodyText.setText(android.text.Html.fromHtml(textPhotoPost).toString());
                    infoViewHolder.bodyText.setVisibility(VISIBLE);
                }
                if(ps.getTags()!=null){
                    for (int j = 0; j <ps.getTags().size() ; j++) {
                        TextView textTag = new TextView(activity);
                        textTag.setTextColor(R.color.colorTextTag);
                        textTag.setText(String.format("#%s ",ps.getTags().get(j)));
                        infoViewHolder.tagLinearLayout.addView(textTag);
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
                            GlideApp.with(activity)
                                    .load(getLikePostImg(((PhotoPost) posts.get(position)), imagePosition))

                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .into(imageView);
                            imageView.setVisibility(VISIBLE);
                            rowLayout.addView(imageView, rowItemParams);
                            imagePosition++;
                        }
                        infoViewHolder.gridRoot.addView(rowLayout);
                    }
                    if ((total % columns) == 1) {
                        LinearLayout rowLayout = new LinearLayout(activity);
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams rowItemParams = new LinearLayout
                                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        rowItemParams.weight = 1;
                        rowItemParams.setMargins(3, 2, 3, 10);
                        ImageView imageView = new ImageView(activity);
                        GlideApp.with(activity)
                                .load(getLikePostImg(((PhotoPost) posts.get(position)), imagePosition))
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(imageView);
                        rowLayout.addView(imageView, rowItemParams);
                        infoViewHolder.gridRoot.addView(rowLayout);
                    }

                    infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                    infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                    infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                } else {
                    LinearLayout rowLayout = new LinearLayout(activity);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams rowItemParams = new LinearLayout
                            .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    rowItemParams.weight = 1;
                    rowItemParams.setMargins(3, 2, 3, 10);

                    ImageView imageView = new ImageView(activity);
                    GlideApp.with(activity)
                            .load(getLikePostImg(((PhotoPost) posts.get(position)), total - 1))
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(imageView);
                    rowLayout.addView(imageView, rowItemParams);
                    infoViewHolder.gridRoot.addView(rowLayout);
                    infoViewHolder.progressBarLickedPost.setIndeterminate(false);
                    infoViewHolder.progressBarLickedPost.setVisibility(GONE);
                    infoViewHolder.lickedPostLinear.setVisibility(VISIBLE);
                }

                break;
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class InfoViewHolder extends RecyclerView.ViewHolder {
        private TextView bodyText, blogName, timePost;
        private ProgressBar progressBarLickedPost;
        private LinearLayout gridRoot;
        private ImageView blogAvatar;
        private LinearLayout tagRootLinearLayout;
        private LinearLayout tagLinearLayout;
        private LinearLayout lickedPostLinear;

        public InfoViewHolder(View view) {
            super(view);
            progressBarLickedPost = view.findViewById(R.id.progress_bar_post);
            lickedPostLinear = view.findViewById(R.id.post_liked_linear);
            tagRootLinearLayout = view.findViewById(R.id.grid_root_tag_linear_layout);
            tagLinearLayout = view.findViewById(R.id.tag_linear_layout);
            bodyText = view.findViewById(R.id.text_body);
            gridRoot = view.findViewById(R.id.grid_root_layout);
            blogName = view.findViewById(R.id.blog_name_info_recycler_textview);
            timePost = view.findViewById(R.id.time_info_recycler_textview);
            blogAvatar = view.findViewById(R.id.avatar_post_info_recycler_test_image);


        }
    }
}
