package com.nojom.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nojom.R;
import com.nojom.databinding.ItemSocialChannelBinding;
import com.nojom.databinding.ItemSocialFollowerBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.GigCategoryModel;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocialChannelAdapter extends RecyclerView.Adapter<SocialChannelAdapter.SimpleViewHolder> {

    private List<ConnectedSocialMedia.Data> mDatasetFiltered;
    private BaseActivity context;
    private OnClickPlatformListener onClickPlatformListener;
    private GigCategoryModel.Data selectedPosData;
    private ConnectedSocialMedia.Data selectedPlatform;

    public void seTSelectedPlatform(ConnectedSocialMedia.Data selectedPlatform) {
        this.selectedPlatform = selectedPlatform;
    }

    // Initializing a String Array
    public interface OnClickPlatformListener {
        void onClickPlatform(ConnectedSocialMedia.Data platform);
    }

    public SocialChannelAdapter(BaseActivity context, ArrayList<ConnectedSocialMedia.Data> objects, OnClickPlatformListener listener) {
        this.mDatasetFiltered = objects;
        this.context = context;
        this.onClickPlatformListener = listener;

    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemSocialFollowerBinding fullBinding =
                ItemSocialFollowerBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            ConnectedSocialMedia.Data item = mDatasetFiltered.get(position);
            holder.binding.tvUsername.setText("@" + item.username);

            Glide.with(context)
                    .load(Uri.parse(item.filename))
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.image);

            if (selectedPlatform != null) {
                if (selectedPlatform.id == item.id || selectedPlatform.social_platform_id == item.id) {
                    holder.binding.rlView.setBackground(context.getResources().getDrawable(R.drawable.blue_button_bg_stroke));
                } else {
                    holder.binding.rlView.setBackground(context.getResources().getDrawable(R.drawable.white_button_bg_stroke));
                }
            } else {
                holder.binding.rlView.setBackground(context.getResources().getDrawable(R.drawable.white_button_bg_stroke));
            }

            /*if (item.isSelected) {
                holder.binding.linSocial.setVisibility(View.VISIBLE);
                holder.binding.tvCategory.setVisibility(View.GONE);
            } else {
                holder.binding.linSocial.setVisibility(View.GONE);
                holder.binding.tvCategory.setVisibility(View.VISIBLE);
            }*/

            String formattedNumber = context.formatNumber(item.followers);

            holder.binding.tvCount.setText(formattedNumber);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<ConnectedSocialMedia.Data> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialFollowerBinding binding;

        public SimpleViewHolder(ItemSocialFollowerBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.getRoot().setOnClickListener(v -> {
                if (onClickPlatformListener != null) {
                    onClickPlatformListener.onClickPlatform(mDatasetFiltered.get(getAdapterPosition()));
                }
                selectedPlatform = mDatasetFiltered.get(getAbsoluteAdapterPosition());
                notifyDataSetChanged();
            });
        }
    }

}
