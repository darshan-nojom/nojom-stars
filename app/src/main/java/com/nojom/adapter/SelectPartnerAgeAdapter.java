package com.nojom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemSelectFullBinding;
import com.nojom.model.PartnerWithUsResponse;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class SelectPartnerAgeAdapter extends RecyclerView.Adapter<SelectPartnerAgeAdapter.SimpleViewHolder>
        implements Filterable {

    private ArrayList<PartnerWithUsResponse.Answers> mDataset;
    private ArrayList<PartnerWithUsResponse.Answers> mDatasetFiltered;
    private Context context;

    public SelectPartnerAgeAdapter(Context context, ArrayList<PartnerWithUsResponse.Answers> objects) {
        this.mDataset = objects;
        this.mDatasetFiltered = objects;
        this.context = context;
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
            PartnerWithUsResponse.Answers item = mDatasetFiltered.get(position);

            holder.binding.tvTitle.setText(item.answer);

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
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (PartnerWithUsResponse.Answers data : mDatasetFiltered) {
                    data.isSelected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PartnerWithUsResponse.Answers getSelectedItem() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (PartnerWithUsResponse.Answers data : mDatasetFiltered) {
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

    public List<PartnerWithUsResponse.Answers> getData() {
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
                    ArrayList<PartnerWithUsResponse.Answers> filteredList = new ArrayList<>();
                    for (PartnerWithUsResponse.Answers row : mDataset) {
                        String rowText = row.answer.toLowerCase();
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
                mDatasetFiltered = (ArrayList<PartnerWithUsResponse.Answers>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
