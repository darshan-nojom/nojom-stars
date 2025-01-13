package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nojom.R;
import com.nojom.databinding.ItemPlatformImgBinding;
import com.nojom.model.CampService;
import com.nojom.ui.BaseActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlatformDetailAdapter2 extends RecyclerView.Adapter<PlatformDetailAdapter2.SimpleViewHolder> {

    private List<CampService> mDatasetFiltered;
    private Context context;
    private BaseActivity activity;

    public PlatformDetailAdapter2(Context context, List<CampService> objects) {
        this.mDatasetFiltered = objects;
        this.context = context;
        activity = (BaseActivity) context;
    }

    @NotNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemPlatformImgBinding fullBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_platform_img, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NotNull final SimpleViewHolder holder, final int position) {
        CampService item = mDatasetFiltered.get(position);

        Glide.with(activity).load(item.platform.logo).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.binding.squareImage);
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<CampService> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemPlatformImgBinding binding;

        public SimpleViewHolder(ItemPlatformImgBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }
    }
}
