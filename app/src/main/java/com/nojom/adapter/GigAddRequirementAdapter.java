package com.nojom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemCategoryListBinding;
import com.nojom.model.RequiremetList;

import java.util.ArrayList;
import java.util.List;

public class GigAddRequirementAdapter extends RecyclerView.Adapter<GigAddRequirementAdapter.SimpleViewHolder>
        implements Filterable {

    private List<RequiremetList.Data> mDataset;
    private List<RequiremetList.Data> mDatasetFiltered;
    private Context context;
    private OnClickReqListener onClickLanguageListener;
    private int selectedPos = -1;

    public interface OnClickReqListener {
        void onClickReq(RequiremetList.Data language);
    }

    public GigAddRequirementAdapter(Context context, ArrayList<RequiremetList.Data> objects, OnClickReqListener listener) {
        this.mDataset = objects;
        this.mDatasetFiltered = objects;
        this.context = context;
        this.onClickLanguageListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        if (mDataset.get(position).id != null) {
            return mDataset.get(position).id;
        } else {
            return position;
        }

    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemCategoryListBinding fullBinding =
                ItemCategoryListBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            RequiremetList.Data item = mDatasetFiltered.get(position);

            holder.binding.tvCategory.setText(item.name);

            /*if (item.isSelected) {
                holder.itemView.setEnabled(false);
            } else {
                holder.itemView.setEnabled(true);
            }*/

            if (selectedPos == position/* || item.isSelected*/) {
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.check_done);
                holder.binding.imgCheckUncheck.clearColorFilter();
                selectedPos = position;
            } else {
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.circle_uncheck);
                holder.binding.imgCheckUncheck.setColorFilter(ContextCompat.getColor(context,
                        R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
/*
            if (selectedPos == position) {

            } else {

            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearSelected() {
        try {
            selectedPos = -1;
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (RequiremetList.Data data : mDatasetFiltered) {
                    data.isSelected = false;
                }
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public GigCategoryModel.Data getSelectedItem() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (GigCategoryModel.Data data : mDatasetFiltered) {
                    if (data.isSelected) {
                        return data;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

//    public List<GigCategoryModel.Data> getData() {
//        return mDatasetFiltered;
//    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryListBinding binding;

        public SimpleViewHolder(ItemCategoryListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.rlView.setOnClickListener(v -> {
                try {
                    // clearSelected();
//                    mDatasetFiltered.get(getAdapterPosition()).isSelected = true;
                    selectedPos = getAdapterPosition();
                    if (onClickLanguageListener != null) {
                        onClickLanguageListener.onClickReq(mDatasetFiltered.get(getAdapterPosition()));
                    }
                    notifyDataSetChanged();
//                    notifyItemChanged(selectedPos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //clearSelectedDuplicate();
                    mDatasetFiltered = mDataset;
                } else {
                    List<RequiremetList.Data> filteredList = new ArrayList<>();
                    for (RequiremetList.Data row : mDataset) {
                        String rowText = row.name.toLowerCase();
                        if (!TextUtils.isEmpty(rowText)) {
                            if (rowText.contains(charString.toLowerCase())/* || row.name.equalsIgnoreCase("Other")*/) {
                                filteredList.add(row);
                            }
                        }
                    }

                    mDatasetFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDatasetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDatasetFiltered = (List<RequiremetList.Data>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void setData(ArrayList<RequiremetList.Data> newData) {

//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallBack(newData, mDataset));
//        diffResult.dispatchUpdatesTo(this);
//        mDataset.clear();
//        this.mDataset.addAll(newData);
    }
}
