package com.nojom.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.databinding.HireGridItemBinding;
import com.nojom.ui.home.HomePagerModel;

import java.util.List;

public class HireItemsAdapter extends RecyclerView.Adapter<HireItemsAdapter.SimpleViewHolder> {

    private List<HomePagerModel> mDataSet;
    private OnItemClick onItemClick;

    public HireItemsAdapter(List<HomePagerModel> objects, OnItemClick onItemClick) {
        this.mDataSet = objects;
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onClickItem(HomePagerModel model);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        HireGridItemBinding hireGridItemBinding =
                HireGridItemBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(hireGridItemBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        HomePagerModel item = mDataSet.get(position);
        holder.binding.setHomeItem(item);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public List<HomePagerModel> getData() {
        return mDataSet;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private HireGridItemBinding binding;

        SimpleViewHolder(HireGridItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            itemView.getRoot().setOnClickListener(v -> {
                if (onItemClick != null) {
                    onItemClick.onClickItem(mDataSet.get(getAdapterPosition()));
                }
            });
        }
    }
}
