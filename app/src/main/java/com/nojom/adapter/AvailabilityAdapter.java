package com.nojom.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.apis.UpdateTypeAPI;
import com.nojom.databinding.ItemAvailabilityBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.EditAvailabilityActivity;
import com.nojom.util.Constants;

import java.util.List;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.SimpleViewHolder> {

    private List<ProfileResponse.ProfilePayType> mDataset;
    private Context context;
    private BaseActivity activity;
    private int selectedPosition = -1;
    private UpdateTypeAPI updatePayTypesApi;

    public AvailabilityAdapter(Context context, List<ProfileResponse.ProfilePayType> objects) {
        this.mDataset = objects;
        this.context = context;
        activity = (BaseActivity) context;
        updatePayTypesApi = new UpdateTypeAPI();
        updatePayTypesApi.init(activity);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemAvailabilityBinding itemBinding =
                ItemAvailabilityBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final ProfileResponse.ProfilePayType item = mDataset.get(position);
        holder.binding.setData(item);
        Log.e("Pos", "===================  " + item.status);
        holder.binding.segmentGroup.setPosition(item.status);

        holder.binding.segmentGroup.setOnPositionChangedListener(status -> {
            ((EditAvailabilityActivity) context).isRefresh(true);
            if (status == 0) {
                holder.binding.tabYes.setTypeface(Constants.SFTEXT_REGULAR);
                holder.binding.tabNo.setTypeface(Constants.SFTEXT_BOLD);
            } else {
                holder.binding.tabNo.setTypeface(Constants.SFTEXT_REGULAR);
                holder.binding.tabYes.setTypeface(Constants.SFTEXT_BOLD);
            }
            selectedPosition = position;

            updatePayTypesApi.updatePayTypes(item.id, status);

            updatePayTypesApi.getIsShowError().observe(activity, isError -> {
                if (isError) {
                    if (selectedPosition != -1) {
                        notifyItemChanged(selectedPosition);
                    }
                }
                selectedPosition = -1;
            });
        });
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public List<ProfileResponse.ProfilePayType> getData() {
        return mDataset;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemAvailabilityBinding binding;

        SimpleViewHolder(ItemAvailabilityBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
