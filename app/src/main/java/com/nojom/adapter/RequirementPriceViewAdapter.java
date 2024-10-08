package com.nojom.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemCustomPriceBinding;
import com.nojom.model.RequiremetList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.ArrayList;

public class RequirementPriceViewAdapter extends RecyclerView.Adapter<RequirementPriceViewAdapter.SimpleViewHolder> {
    LayoutInflater layoutInflater;
    private BaseActivity activity;
    private ArrayList<RequiremetList.CustomData> customDataList;

    public RequirementPriceViewAdapter(BaseActivity context, ArrayList<RequiremetList.CustomData> customDataList) {
        this.activity = context;
        this.customDataList = customDataList;
        layoutInflater = activity.getLayoutInflater();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomPriceBinding popularBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_custom_price, parent, false);
        return new SimpleViewHolder(popularBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        RequiremetList.CustomData item = customDataList.get(position);

        holder.binding.txName.setText(item.dataReq);
        if (!TextUtils.isEmpty(item.dataValue)) {
            if (activity.getCurrency().equals("SAR")) {
                if (!item.dataValue.endsWith(activity.getString(R.string.sar))) {
                    holder.binding.txtPrice.setText(Utils.getDecimalValue(item.dataValue) + " "+activity.getString(R.string.sar));
                } else {
                    holder.binding.txtPrice.setText(Utils.getDecimalValue(item.dataValue.replace(activity.getString(R.string.sar), "")) + " "+activity.getString(R.string.sar));
                }
            } else {
                if (!item.dataValue.startsWith(activity.getString(R.string.dollar))) {
                    holder.binding.txtPrice.setText(activity.getString(R.string.dollar) + Utils.getDecimalValue(item.dataValue));
                } else {
                    holder.binding.txtPrice.setText(activity.getString(R.string.dollar) + Utils.getDecimalValue(item.dataValue.replace(activity.getString(R.string.dollar), "")));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return customDataList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        ItemCustomPriceBinding binding;

        public SimpleViewHolder(ItemCustomPriceBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
