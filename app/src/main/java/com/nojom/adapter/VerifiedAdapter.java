package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemVerifiedWithBinding;
import com.nojom.model.ProfileResponse;

import java.util.List;

public class VerifiedAdapter extends RecyclerView.Adapter<VerifiedAdapter.SimpleViewHolder> {

    private List<ProfileResponse.VerifiedWith> mDataset;

    public void doRefresh(List<ProfileResponse.VerifiedWith> objects) {
        this.mDataset = objects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemVerifiedWithBinding verifiedWithBinding =
                ItemVerifiedWithBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(verifiedWithBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ProfileResponse.VerifiedWith item = mDataset.get(position);
        holder.binding.tvTitle.setText(item.name);
        if (item.isVerified > 0) {
            holder.binding.imgVerify.setImageResource(R.drawable.verified_tick);
        } else {
            holder.binding.imgVerify.setImageResource(R.drawable.not_verified);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemVerifiedWithBinding binding;

        SimpleViewHolder(ItemVerifiedWithBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
