package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.nojom.R;
import com.nojom.databinding.ItemGigDetailsBinding;
import com.nojom.model.GigRequirementsModel;
import com.nojom.ui.BaseActivity;

import java.util.List;

public class GigAdapter extends RecyclerView.Adapter<GigAdapter.SimpleViewHolder> {
    LayoutInflater layoutInflater;
    private BaseActivity activity;
    private List<GigRequirementsModel.Data> arrRequirementList;

    public GigAdapter(BaseActivity context, List<GigRequirementsModel.Data> arrRequirementList) {
        this.activity = context;
        this.arrRequirementList = arrRequirementList;
        layoutInflater = activity.getLayoutInflater();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGigDetailsBinding popularBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_gig_details, parent, false);
        return new SimpleViewHolder(popularBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        GigRequirementsModel.Data item = arrRequirementList.get(position);

        holder.binding.tvGigName.setText(item.name);
    }

    @Override
    public int getItemCount() {
        return arrRequirementList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        ItemGigDetailsBinding binding;

        public SimpleViewHolder(ItemGigDetailsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
