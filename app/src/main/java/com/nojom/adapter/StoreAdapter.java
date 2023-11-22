package com.nojom.adapter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.nojom.databinding.ItemInfStoreBinding;
import com.nojom.ui.BaseActivity;

import java.util.List;


public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.SimpleViewHolder> {

//    private List<AgentProfile.StoreList> mDataset;
    private BaseActivity activity;

    /*public void doRefresh(List<AgentProfile.StoreList> objects, BaseActivity activity) {
        this.mDataset = objects;
        this.activity = activity;
        notifyDataSetChanged();
    }*/

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemInfStoreBinding verifiedWithBinding =
                ItemInfStoreBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(verifiedWithBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
//        AgentProfile.StoreList item = mDataset.get(position);

//        holder.binding.txtName.setText(item.name);
//        holder.binding.tvSendOffer.setText(item.button_title);

        /*if (!TextUtils.isEmpty(item.image_url)) {
            Glide.with(holder.binding.imgProfile.getContext()).load(item.image_url)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.binding.imgProfile);
        } else {
            holder.binding.imgProfile.setImageResource(R.mipmap.ic_launcher_round);
        }*/
    }

    @Override
    public int getItemCount() {
        return /*mDataset != null ? mDataset.size() :*/ 0;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemInfStoreBinding binding;

        SimpleViewHolder(ItemInfStoreBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.getRoot().setOnClickListener(v -> {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(getBindingAdapterPosition()).url));
//                activity.startActivity(intent);
            });
        }
    }
}
