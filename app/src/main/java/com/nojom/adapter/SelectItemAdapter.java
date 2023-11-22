package com.nojom.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemSelectFullBinding;
import com.nojom.model.Language;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class SelectItemAdapter extends RecyclerView.Adapter<SelectItemAdapter.SimpleViewHolder>
        implements Filterable {

    private List<Language.Data> mDataset;
    private List<Language.Data> mDatasetFiltered;
    private BaseActivity context;
    private boolean isLanguage;

    public SelectItemAdapter(BaseActivity context, ArrayList<Language.Data> objects, boolean isLanguage) {
        this.mDataset = objects;
        this.mDatasetFiltered = objects;
        this.context = context;
        this.isLanguage = isLanguage;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemSelectFullBinding fullBinding =
                ItemSelectFullBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            Language.Data item = mDatasetFiltered.get(position);

            if (!isLanguage) {
                holder.binding.tvTitle.setText(item.level);
                holder.binding.tvTitle.setGravity(Gravity.CENTER);
            } else {
                holder.binding.tvTitle.setText(item.getName(context.language));
            }

            if (item.isSelected) {
                holder.binding.tvTitle.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_button_bg));
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
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (Language.Data data : mDatasetFiltered) {
                    data.isSelected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Language.Data getSelectedItem() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (Language.Data data : mDatasetFiltered) {
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

    public List<Language.Data> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemSelectFullBinding binding;

        public SimpleViewHolder(ItemSelectFullBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.rlView.setOnClickListener(v -> {
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDatasetFiltered = mDataset;
                } else {
                    List<Language.Data> filteredList = new ArrayList<>();
                    for (Language.Data row : mDataset) {
                        String rowText = row.getName(context.language).toLowerCase();
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
                mDatasetFiltered = (List<Language.Data>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
