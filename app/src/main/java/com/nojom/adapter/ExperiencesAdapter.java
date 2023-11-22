package com.nojom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemExperiencesBinding;
import com.nojom.databinding.ItemSkillsBinding;
import com.nojom.model.Skill;

import java.util.ArrayList;

public class ExperiencesAdapter extends RecyclerView.Adapter<ExperiencesAdapter.SimpleViewHolder> {

    private ArrayList<Skill> mDataset;
    private Context context;
    private boolean isGrayColor = false;
    private OnItemClickListener onItemClickListener;
    private boolean isEmployment;
    private String SELECTED_TAG = null;

    private String getSELECTED_TAG() {
        return SELECTED_TAG;
    }

    public void setSELECTED_TAG(String SELECTED_TAG) {
        this.SELECTED_TAG = SELECTED_TAG;
    }

    public interface OnItemClickListener {
        void onItemExperienceClick(boolean isEmlpoyment, String tag);
    }

    public ExperiencesAdapter(Context context, ArrayList<Skill> objects, OnItemClickListener onItemClickListener, boolean isEmployement) {
        this.mDataset = objects;
        this.onItemClickListener = onItemClickListener;
        this.isEmployment = isEmployement;
        this.context = context;
    }

    public void setGrayColor(boolean isGrayColor) {
        this.isGrayColor = isGrayColor;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemExperiencesBinding itemBinding =
                ItemExperiencesBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Skill item = mDataset.get(position);
        holder.binding.setSkills(item);

        if (isGrayColor) {
            holder.binding.tvLevel.setTextColor(ContextCompat.getColor(context, R.color.textgrayAccent));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public ArrayList<Skill> getData() {
        return mDataset;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemExperiencesBinding binding;

        SimpleViewHolder(ItemExperiencesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            itemView.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemExperienceClick(isEmployment, getSELECTED_TAG());
                }
            });
        }
    }
}
