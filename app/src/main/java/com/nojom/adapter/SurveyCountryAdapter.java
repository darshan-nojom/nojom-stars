package com.nojom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.nojom.R;
import com.nojom.ccp.CCPCountry;
import com.nojom.databinding.ItemSelectFullBinding;
import com.nojom.util.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SurveyCountryAdapter extends RecyclerView.Adapter<SurveyCountryAdapter.SimpleViewHolder>
        implements Filterable {

    private final List<CCPCountry> mDataset;
    private List<CCPCountry> mDatasetFiltered;
    private final Context context;

    public SurveyCountryAdapter(Context context, List<CCPCountry> objects) {
        this.mDataset = objects;
        this.mDatasetFiltered = objects;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NotNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemSelectFullBinding fullBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_select_full, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NotNull final SimpleViewHolder holder, final int position) {
        try {
            CCPCountry item = mDatasetFiltered.get(position);

            holder.binding.tvTitle.setText(item.getName());

            if (item.isSelected) {
                holder.binding.tvTitle.setBackground(ContextCompat.getDrawable(context, R.drawable.black_button_bg));
                Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_BOLD);
                holder.binding.tvTitle.setTypeface(tf);
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                holder.binding.tvTitle.setBackgroundColor(Color.TRANSPARENT);
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
                Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_REGULAR);
                holder.binding.tvTitle.setTypeface(tf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSelected() {
        if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
//            for (int i = 0; i < mDatasetFiltered.size(); i++) {
//                mDatasetFiltered.get(i).isSelected = false;
//            }
            for (CCPCountry country : mDatasetFiltered) {
                country.isSelected = false;
            }
        }
    }

    public CCPCountry getSelectedItem() {
        if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
//            for (int i = 0; i < mDatasetFiltered.size(); i++) {
//                if (mDatasetFiltered.get(i).isSelected) {
//                    return mDatasetFiltered.get(i);
//                }
//            }
            for (CCPCountry country : mDatasetFiltered) {
                if (country.isSelected) {
                    return country;
                }
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDatasetFiltered != null ? mDatasetFiltered.size() : 0;
    }

    public List<CCPCountry> getData() {
        return mDatasetFiltered;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDatasetFiltered = mDataset;
                    //clearSelected();
                    //selectFirst();
                } else {
                    List<CCPCountry> filteredList = new ArrayList<>();
                    for (CCPCountry row : mDataset) {
                        String rowText = row.getName().toLowerCase();
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
                mDatasetFiltered = (List<CCPCountry>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSelectFullBinding binding;

        public SimpleViewHolder(ItemSelectFullBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.rlView.setOnClickListener(v -> {
                clearSelected();
                mDatasetFiltered.get(getAdapterPosition()).isSelected = true;
                notifyDataSetChanged();
            });
        }
    }
}
