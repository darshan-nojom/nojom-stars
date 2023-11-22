package com.nojom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemSkillsEditBinding;
import com.nojom.model.YearsModel;
import com.nojom.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SelectYearsAdapter extends RecyclerView.Adapter<SelectYearsAdapter.SimpleViewHolder> {

    private List<YearsModel> mDatasetFiltered;
    private Context context;

    public SelectYearsAdapter(Context context, List<YearsModel> objects) {
        this.mDatasetFiltered = objects;
        this.context = context;
    }

    @NotNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemSkillsEditBinding itemSkillsEditBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_skills_edit, parent, false);
        return new SimpleViewHolder(itemSkillsEditBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        YearsModel item = mDatasetFiltered.get(position);

        holder.binding.tvSkill.setText(String.format("%d", item.year));

        if (item.isSelected) {
            holder.binding.tvSkill.setBackground(ContextCompat.getDrawable(context, R.drawable.black_button_bg));
            Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_BOLD);
            holder.binding.tvSkill.setTypeface(tf);
            holder.binding.tvSkill.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.binding.tvSkill.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.tvSkill.setTextColor(ContextCompat.getColor(context, R.color.black));
            Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_REGULAR);
            holder.binding.tvSkill.setTypeface(tf);
        }
    }

    private void clearSelected() {
        if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
//            for (int i = 0; i < mDatasetFiltered.size(); i++) {
//                mDatasetFiltered.get(i).isSelected = false;
//            }
            for (YearsModel year : mDatasetFiltered) {
                year.isSelected = false;
            }
        }
    }

    public YearsModel getSelectedItem() {
        if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
//            for (int i = 0; i < mDatasetFiltered.size(); i++) {
//                if (mDatasetFiltered.get(i).isSelected) {
//                    return mDatasetFiltered.get(i);
//                }
//            }
            for (YearsModel model : mDatasetFiltered) {
                if (model.isSelected) {
                    return model;
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<YearsModel> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSkillsEditBinding binding;

        public SimpleViewHolder(ItemSkillsEditBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(v -> {
                try {
                    clearSelected();
                    mDatasetFiltered.get(getAdapterPosition()).isSelected = true;
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
