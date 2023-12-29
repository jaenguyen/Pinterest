package com.anhnhy.printerest.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.anhnhy.printerest.R;
import com.anhnhy.printerest.model.Image;
import com.bumptech.glide.Glide;

import java.util.List;

public class ExploreSearchImageAdapter extends RecyclerView.Adapter<ExploreSearchImageAdapter.ExploreImageViewHolder> {
    private Context context;
    private List<Image> exploreImages;
    private OnItemClickListener itemClickListener;

    public ExploreSearchImageAdapter(Context context, List<Image> exploreImages) {
        this.context = context;
        this.exploreImages = exploreImages;
    }

    @Override
    public ExploreImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.explore_image_item, parent, false);
        return new ExploreImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ExploreImageViewHolder holder, int position) {
        Image exploreImage = exploreImages.get(position);
        holder.txt_e_name.setText(exploreImage.getName());
        int likeCount = exploreImage.getLikeIds() == null ? 0 : exploreImage.getLikeIds().size();
        holder.txt_e_like_count.setText(likeCount + "");
        Glide.with(context)
                .load(exploreImage.getImageUrl())
                .fitCenter()
                .into(holder.e_imageView);
    }

    @Override
    public int getItemCount() {
        return exploreImages.size();
    }

    public class ExploreImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView txt_e_name;
        public TextView txt_e_like_count;
        public ImageView e_imageView;

        public ExploreImageViewHolder(View itemView) {
            super(itemView);
            txt_e_name = itemView.findViewById(R.id.txt_e_name);
            txt_e_like_count = itemView.findViewById(R.id.txt_e_like_count);
            e_imageView = itemView.findViewById(R.id.e_image_view);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            itemClickListener.onWatchDetailClick(position);
                            return true;
                        case 2:
                            itemClickListener.onLikeImageClick(position);
                            return true;
                        case 3:
                            itemClickListener.onDislikeImageClick(position);
                            return true;
                        case 4:
                            itemClickListener.onDownload(exploreImages.get(position).getImageUrl());
                            return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem watchDetail = menu.add(Menu.NONE, 1, 1, "Watch detail");
            MenuItem likeImage = menu.add(Menu.NONE, 2, 2, "Like image");
            MenuItem dislikeImage = menu.add(Menu.NONE, 3, 3, "Dislike image");
            MenuItem download = menu.add(Menu.NONE, 4, 4, "Download");

            watchDetail.setOnMenuItemClickListener(this);
            likeImage.setOnMenuItemClickListener(this);
            dislikeImage.setOnMenuItemClickListener(this);
            download.setOnMenuItemClickListener(this);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWatchDetailClick(int position);

        void onLikeImageClick(int position);

        void onDislikeImageClick(int position);

        void onDownload(String url);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }
}
