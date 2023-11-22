package com.nojom.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.databinding.ItemAvailabilityBinding;
import com.nojom.databinding.ItemWorkbaseBinding;
import com.nojom.model.Available;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.EditAvailabilityActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;

public class WorkbaseAdapter extends RecyclerView.Adapter<WorkbaseAdapter.SimpleViewHolder> {

    private ArrayList<Available.Data> mDataset;
    private Context context;
    private BaseActivity activity;
    private SparseBooleanArray workBaseArray;
    private WorkbaseClickListener workbaseClickListener;

    public interface WorkbaseClickListener {
        void onClickWorkbase(String valye, int pos);
    }

    public WorkbaseAdapter(Context context, ArrayList<Available.Data> objects, WorkbaseClickListener workbaseClickListener) {
        this.mDataset = objects;
        this.context = context;
        this.workbaseClickListener = workbaseClickListener;
        activity = (BaseActivity) context;
        workBaseArray = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemWorkbaseBinding itemBinding =
                ItemWorkbaseBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        final Available.Data item = mDataset.get(position);
        holder.binding.setData(item);


        if (item.isChecked)
            workBaseArray.put(position, true);
        holder.binding.segmentGroup.setPosition(item.isChecked ? 1 : 0);

        holder.binding.segmentGroup.setOnPositionChangedListener(status -> {
            ((EditAvailabilityActivity) context).isRefresh(true);
            if (status == 0) {
                workBaseArray.delete(position);
                holder.binding.tabYes.setTypeface(Constants.SFTEXT_REGULAR);
                holder.binding.tabNo.setTypeface(Constants.SFTEXT_BOLD);
            } else {
                workBaseArray.put(position, true);
                holder.binding.tabNo.setTypeface(Constants.SFTEXT_REGULAR);
                holder.binding.tabYes.setTypeface(Constants.SFTEXT_BOLD);
            }

            if (workBaseArray.size() == 2) {
//                updateWorkbase("2");  // For both selected
                workbaseClickListener.onClickWorkbase("2", position);
            } else {
                if (workBaseArray.size() == 0) {
//                    updateWorkbase("3");  // If nothing selected
                    workbaseClickListener.onClickWorkbase("3", position);
                } else {
//                    updateWorkbase(String.valueOf(workBaseArray.keyAt(0)));  // For one work type selected
                    workbaseClickListener.onClickWorkbase(String.valueOf(workBaseArray.keyAt(0)), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public ArrayList<Available.Data> getData() {
        return mDataset;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemWorkbaseBinding binding;

        SimpleViewHolder(ItemWorkbaseBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


}
