package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.databinding.ItemProjectTypeBinding;
import com.nojom.model.ProjectType;

import java.util.ArrayList;

public class ProjectTypeAdapter extends RecyclerView.Adapter<ProjectTypeAdapter.SimpleViewHolder> {

    private ArrayList<ProjectType> mDataset;

    public ProjectTypeAdapter(ArrayList<ProjectType> objects) {
        this.mDataset = objects;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemProjectTypeBinding projectTypeBinding =
                ItemProjectTypeBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(projectTypeBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ProjectType item = mDataset.get(position);
        holder.binding.setType(item);
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public ArrayList<ProjectType> getData() {
        return mDataset;
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemProjectTypeBinding binding;

        SimpleViewHolder(ItemProjectTypeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
