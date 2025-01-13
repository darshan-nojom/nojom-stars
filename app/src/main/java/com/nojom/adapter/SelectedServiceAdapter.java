package com.nojom.adapter;

import static com.nojom.util.NumberTextWatcherForThousand.getDecimalFormat;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.databinding.ItemInfServiceBinding;
import com.nojom.model.Serv;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.gigs.ProfileUpdateActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class SelectedServiceAdapter extends RecyclerView.Adapter<SelectedServiceAdapter.SimpleViewHolder> {

    private final List<Serv> mDataset;
    private final BaseActivity activity;

    public SelectedServiceAdapter(ArrayList<Serv> services, BaseActivity profileUpdateActivity) {
        this.mDataset = services;
        this.activity = profileUpdateActivity;
    }

//    public void doRefresh(ArrayList<Serv> objects, BaseActivity activity) {
//        this.mDataset = objects;
//        this.activity = activity;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemInfServiceBinding verifiedWithBinding = ItemInfServiceBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(verifiedWithBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Serv item = mDataset.get(position);
        if (item.id == -1) {
            holder.binding.tvName.setText(activity.getString(R.string.all_platforms));
        } else {
            holder.binding.tvName.setText(item.getName(activity.language));
        }

        if (item.followers != 0) {
            String formattedNumber = activity.formatNumber(item.followers);
            holder.binding.tvCount.setText("(" + formattedNumber + ")");
        }
        if (item.price != 0) {
            String formattedNumber = getDecimalFormat(activity.enFormatValue(item.price));
            holder.binding.tvPrice.setText(formattedNumber + " " + activity.getString(R.string.sar));
        }

        if (!TextUtils.isEmpty(item.filename)) {
            holder.binding.imgProfile.setVisibility(View.VISIBLE);
            Glide.with(holder.binding.imgProfile.getContext()).load(item.filename).placeholder(R.mipmap.ic_launcher_round).error(R.mipmap.ic_launcher_round).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.binding.imgProfile);
        } else {
            holder.binding.imgProfile.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemInfServiceBinding binding;

        SimpleViewHolder(ItemInfServiceBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (activity.language.equals("ar")) {
                activity.setArFont(binding.tvName, Constants.FONT_AR_MEDIUM);
                activity.setArFont(binding.tvCount, Constants.FONT_AR_REGULAR);
                activity.setArFont(binding.tvPrice, Constants.FONT_AR_MEDIUM);
            }
            binding.getRoot().setOnClickListener(v -> {

            });
        }
    }
}
