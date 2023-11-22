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
import com.nojom.model.GigRequirementsModel;

import java.util.ArrayList;
import java.util.List;

public class GigRequirementAdapter extends RecyclerView.Adapter<GigRequirementAdapter.SimpleViewHolder>
        implements Filterable {

    private List<GigRequirementsModel.Data> mDataset;
    private List<GigRequirementsModel.Data> mDatasetFiltered;
    private Context context;
    private OnClickRequirementListener onClickLanguageListener;

    public interface OnClickRequirementListener {
        void onClickLanguage(boolean isAdded, GigRequirementsModel.Data language);
    }

    public GigRequirementAdapter(Context context, ArrayList<GigRequirementsModel.Data> objects, OnClickRequirementListener listener) {
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
            GigRequirementsModel.Data item = mDatasetFiltered.get(position);

            holder.binding.tvCategory.setText(item.name);

            if (item.isSelected) {
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.check_done);
                holder.binding.imgCheckUncheck.clearColorFilter();
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
                for (GigRequirementsModel.Data data : mDatasetFiltered) {
                    data.isSelected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GigRequirementsModel.Data getSelectedItem() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (GigRequirementsModel.Data data : mDatasetFiltered) {
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

    public List<GigRequirementsModel.Data> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemCategoryListBinding binding;

        public SimpleViewHolder(ItemCategoryListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.rlView.setOnClickListener(v -> {
                try {

                    mDatasetFiltered.get(getAdapterPosition()).isSelected = !mDatasetFiltered.get(getAdapterPosition()).isSelected;

                    if (mDatasetFiltered.get(getAdapterPosition()).isSelected) {
                        if (onClickLanguageListener != null) {
                            onClickLanguageListener.onClickLanguage(true, mDatasetFiltered.get(getAdapterPosition()));
                        }
                    } else {
                        if (onClickLanguageListener != null) {
                            onClickLanguageListener.onClickLanguage(false, mDatasetFiltered.get(getAdapterPosition()));
                        }
                    }
                    notifyItemChanged(getAdapterPosition());

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
                    mDatasetFiltered = mDataset;
                } else {
                    List<GigRequirementsModel.Data> filteredList = new ArrayList<>();
                    for (GigRequirementsModel.Data row : mDataset) {
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
                mDatasetFiltered = (List<GigRequirementsModel.Data>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
