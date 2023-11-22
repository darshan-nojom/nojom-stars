package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemCustomPriceBinding;
import com.nojom.model.GigCategoryModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.ArrayList;

public class RequirementDeadlineAdapter extends RecyclerView.Adapter<RequirementDeadlineAdapter.SimpleViewHolder> {
    LayoutInflater layoutInflater;
    private BaseActivity activity;
    private ArrayList<GigCategoryModel.Deadline> customDataList;

    public RequirementDeadlineAdapter(BaseActivity context, ArrayList<GigCategoryModel.Deadline> customDataList) {
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
        GigCategoryModel.Deadline item = customDataList.get(position);

        holder.binding.txName.setText(item.value + " " + (item.type == 2 ? "" + activity.getString(R.string.days)
                : "" + activity.getString(R.string.hours)
        ));
        holder.binding.txtPrice.setText("$" + Utils.getDecimalValue(String.valueOf(item.price)));
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
