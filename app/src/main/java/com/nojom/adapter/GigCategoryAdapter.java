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
import com.nojom.model.GigCategoryModel;

import java.util.ArrayList;
import java.util.List;

public class GigCategoryAdapter extends RecyclerView.Adapter<GigCategoryAdapter.SimpleViewHolder>
        implements Filterable {

    private List<GigCategoryModel.Data> mDataset;
    private List<GigCategoryModel.Data> mDatasetFiltered;
    private Context context;
    private OnClickLanguageListener onClickLanguageListener;
    private GigCategoryModel.Data selectedPosData;

    public interface OnClickLanguageListener {
        void onClickLanguage(GigCategoryModel.Data language);
    }

    public GigCategoryAdapter(Context context, ArrayList<GigCategoryModel.Data> objects, OnClickLanguageListener listener) {
        this.mDataset = objects;
        this.mDatasetFiltered = objects;
        this.context = context;
        this.onClickLanguageListener = listener;
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
            GigCategoryModel.Data item = mDatasetFiltered.get(position);

            holder.binding.tvCategory.setText(item.nameApp);

            if (item.isSelected) {
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.check_done);
                holder.binding.imgCheckUncheck.clearColorFilter();
                selectedPosData = item;
            } else {
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.circle_uncheck);
                holder.binding.imgCheckUncheck.setColorFilter(ContextCompat.getColor(context,
                        R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSelected() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (GigCategoryModel.Data data : mDatasetFiltered) {
                    data.isSelected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSelectedDuplicate() {
        try {
            if (mDataset != null && mDataset.size() > 0) {
                for (int i = 0; i < mDataset.size(); i++) {
                    mDataset.get(i).isSelected = selectedPosData != null && selectedPosData.id == mDataset.get(i).id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GigCategoryModel.Data getSelectedItem() {
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
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<GigCategoryModel.Data> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryListBinding binding;

        public SimpleViewHolder(ItemCategoryListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.rlView.setOnClickListener(v -> {
                try {
                    clearSelected();
                    mDatasetFiltered.get(getAdapterPosition()).isSelected = true;
                    selectedPosData = mDatasetFiltered.get(getAdapterPosition());
                    if (onClickLanguageListener != null) {
                        onClickLanguageListener.onClickLanguage(mDatasetFiltered.get(getAdapterPosition()));
                    }
                    notifyDataSetChanged();

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
                    clearSelectedDuplicate();
                    mDatasetFiltered = mDataset;
                } else {
                    List<GigCategoryModel.Data> filteredList = new ArrayList<>();
                    for (GigCategoryModel.Data row : mDataset) {
                        String rowText = row.name.toLowerCase();
                        if (!TextUtils.isEmpty(rowText)) {
                            if (rowText.contains(charString.toLowerCase())) {
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
                mDatasetFiltered = (List<GigCategoryModel.Data>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
