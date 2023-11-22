package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.databinding.ItemReviewsBinding;
import com.nojom.model.ClientReviews;
import com.nojom.model.ProfileResponse;
import com.nojom.util.Utils;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.SimpleViewHolder> {

    private boolean isUsedClientProfile = false;
    private List<ClientReviews.Data> mDatasetClient;
    private List<ProfileResponse.ProjectReview> mDataset;

    public void doRefresh(List<ClientReviews.Data> objects, boolean isUsedClientProfile) {
        this.isUsedClientProfile = isUsedClientProfile;
        this.mDatasetClient = objects;
        notifyDataSetChanged();
    }

    public void doRefresh(List<ProfileResponse.ProjectReview> objects) {
        this.mDataset = objects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemReviewsBinding reviewsBinding =
                ItemReviewsBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(reviewsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            if (isUsedClientProfile) {
                ClientReviews.Data item = mDatasetClient.get(position);
                holder.binding.tvProjectName.setText(item.title);
                holder.binding.tvReview.setText(item.comment);
                String rate = Utils.numberFormat(item.rate, 1);
                holder.binding.ratingbar.setRating(Float.parseFloat(rate.replaceAll(",", ".")));
                holder.binding.tvRating.setText(String.format("(%s)", rate.replaceAll(",", ".")));
                holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "MMM yyyy", item.timestamp));
            } else {
                ProfileResponse.ProjectReview item = mDataset.get(position);
                holder.binding.tvProjectName.setText(item.title);
                holder.binding.tvReview.setText(item.comment);
                String rate = Utils.numberFormat(item.rate, 1);
                holder.binding.ratingbar.setRating(Float.parseFloat(rate.replaceAll(",", ".")));
                holder.binding.tvRating.setText(String.format("(%s)", rate.replaceAll(",", ".")));
                holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "MMM yyyy", item.timestamp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (isUsedClientProfile)
            return mDatasetClient != null ? mDatasetClient.size() : 0;
        else
            return mDataset != null ? mDataset.size() : 0;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemReviewsBinding binding;

        SimpleViewHolder(ItemReviewsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
