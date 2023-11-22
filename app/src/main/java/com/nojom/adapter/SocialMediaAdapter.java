package com.nojom.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nojom.R;
import com.nojom.databinding.ItemSocialChannelBinding;
import com.nojom.databinding.ItemSocialMediaBinding;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.SocialMedia;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.SimpleViewHolder> {

    private List<SocialMedia> mDatasetFiltered;
    private BaseActivity context;
    private OnClickPlatformListener onClickPlatformListener;

    public interface OnClickPlatformListener {
        void onClickPlatform(SocialPlatformResponse.Data platform);
    }

    public SocialMediaAdapter(BaseActivity context, ArrayList<SocialMedia> objects, OnClickPlatformListener listener) {
        this.mDatasetFiltered = objects;
        this.context = context;
        this.onClickPlatformListener = listener;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSocialMediaBinding fullBinding = ItemSocialMediaBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            SocialMedia item = mDatasetFiltered.get(position);
            holder.binding.tvTitle.setText(item.title);

            holder.binding.rvPlatform.setLayoutManager(new GridLayoutManager(context, 4));
//            holder.binding.rvPlatform.addItemDecoration(new EqualSpacingItemDecoration(25));

            SocialPlatformAdapter mAdapter = new SocialPlatformAdapter(context, item.socialPlatformList, null,item);
            holder.binding.rvPlatform.setAdapter(mAdapter);


            holder.binding.imgArrow.setOnClickListener(v -> {
                if (holder.binding.rvPlatform.isShown()) {
                    holder.binding.imgArrow.setRotation(0);
                    holder.binding.rvPlatform.setVisibility(View.GONE);
                } else {
                    holder.binding.imgArrow.setRotation(180);
                    holder.binding.rvPlatform.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<SocialMedia> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSocialMediaBinding binding;

        public SimpleViewHolder(ItemSocialMediaBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;


        }
    }

//    public SocialPlatformResponse.Data getSelectedCategory() {
//        for (SocialPlatformResponse.Data data : mDatasetFiltered) {
//            if (data.isSelected) {
//                return data;
//            }
//        }
//        return null;
//    }

}
