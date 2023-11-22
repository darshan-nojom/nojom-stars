package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.nojom.R;
import com.nojom.databinding.ItemReferralHistoryBinding;
import com.nojom.model.ReferralHistory;
import com.nojom.ui.BaseActivity;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReferralHistoryAdapter extends RecyclerView.Adapter<ReferralHistoryAdapter.SimpleViewHolder> {

    private List<ReferralHistory.Data> mDataset;
    private Context context;
    private BaseActivity activity;

    public ReferralHistoryAdapter(Context context, List<ReferralHistory.Data> objects) {
        this.mDataset = objects;
        this.context = context;
        activity = (BaseActivity) context;
    }

    @NotNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemReferralHistoryBinding itemReferralHistoryBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_referral_history, parent, false);
        return new SimpleViewHolder(itemReferralHistoryBinding);
    }

    @Override
    public void onBindViewHolder(@NotNull final SimpleViewHolder holder, final int position) {
        final ReferralHistory.Data item = mDataset.get(position);
        if (position == 0) {
            holder.binding.txtName.setTextColor(context.getResources().getColor(R.color.textgrayAccent));
            holder.binding.txtDate.setTextColor(context.getResources().getColor(R.color.textgrayAccent));
            holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.textgrayAccent));
            holder.binding.txtStatus.setText(activity.getString(R.string.status));
            holder.binding.txtName.setText(item.username);
            holder.binding.txtDate.setText(item.timestamp);
        } else {
            holder.binding.txtName.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.txtDate.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.txtStatus.setTextColor(context.getResources().getColor(R.color.black));

            holder.binding.txtName.setText(String.format("%d-%s", position, item.username));

            if (item.transactionStatus == 2) {
                holder.binding.txtStatus.setText(activity.getString(R.string.pending));
            } else {
                holder.binding.txtStatus.setText(activity.getString(R.string.completed));
            }

            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Date newDate = null;
            try {
                newDate = spf.parse(item.timestamp);
                spf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                String date = null;
                if (newDate != null) {
                    date = spf.format(newDate);
                }
                holder.binding.txtDate.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public List<ReferralHistory.Data> getData() {
        return mDataset;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemReferralHistoryBinding binding;

        SimpleViewHolder(ItemReferralHistoryBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
