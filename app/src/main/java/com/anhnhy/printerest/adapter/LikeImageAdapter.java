package com.anhnhy.printerest.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.anhnhy.printerest.R;
import com.anhnhy.printerest.model.Image;
import com.bumptech.glide.Glide;

import java.util.List;

public class LikeImageAdapter extends RecyclerView.Adapter<LikeImageAdapter.ImageViewHolder> {
    private Context context;
    private List<Image> images;
    private OnItemClickListener itemClickListener;

    public LikeImageAdapter(Context context, List<Image> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image uploadCurrent = images.get(position);
        Glide.with(context)
                .load(uploadCurrent.getImageUrl())
                .fitCenter()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
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
            MenuItem whatDetail = menu.add(Menu.NONE, 1, 1, "Watch detail");
            MenuItem dislike = menu.add(Menu.NONE, 2, 2, "Dislike");
            MenuItem download = menu.add(Menu.NONE, 3, 3, "Download");
            whatDetail.setOnMenuItemClickListener(this);
            dislike.setOnMenuItemClickListener(this);
            download.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            itemClickListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            itemClickListener.onDislikeClick(position);
                            return true;
                        case 3:
                            itemClickListener.onDownload(images.get(position).getImageUrl());
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDislikeClick(int position);

        void onDownload(String url);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }
}
