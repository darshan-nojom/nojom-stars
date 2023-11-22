package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.SimpleViewHolder> {

    private ArrayList<?> mDataset;
    private int layoutId;
    private OnViewBindListner onViewBindListner;

    public RecyclerviewAdapter(ArrayList<?> objects, int layoutId, OnViewBindListner onViewBindListner) {
        this.mDataset = objects;
        this.layoutId = layoutId;
        this.onViewBindListner = onViewBindListner;
    }

    public void doRefresh(ArrayList<?> objects) {
        this.mDataset = objects;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    public interface OnViewBindListner {
        void bindView(View view, int position);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder viewHolder, final int position) {
        if (onViewBindListner != null) {
            onViewBindListner.bindView(viewHolder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
