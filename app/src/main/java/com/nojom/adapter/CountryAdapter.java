package com.nojom.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ItemSelectFullBinding;
import com.nojom.model.CountryResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.SimpleViewHolder>
        implements Filterable {

    private List<CountryResponse.CountryData> mDataset;
    private List<CountryResponse.CountryData> mDatasetFiltered;
    private BaseActivity context;
    public boolean isBlackColor;
    private CountryListener countryListener;

    public interface CountryListener {
        void onClickCountry(int pos, CountryResponse.CountryData data);
    }

    public void setCountryListener(CountryListener countryListener) {
        this.countryListener = countryListener;
    }

    public void setBlackColor(boolean blackColor) {
        isBlackColor = blackColor;
    }

    public CountryAdapter(BaseActivity context, List<CountryResponse.CountryData> objects) {
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
            CountryResponse.CountryData item = mDatasetFiltered.get(position);

            holder.binding.tvTitle.setText(item.getCountryName(context.language));

            if (item.isSelected) {
                holder.binding.imgChk.setVisibility(View.VISIBLE);
                /*holder.binding.tvTitle.setBackground(isBlackColor ? ContextCompat.getDrawable(context, R.drawable.black_button_bg) : ContextCompat.getDrawable(context, R.drawable.blue_button_bg));
                Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_BOLD);
                holder.binding.tvTitle.setTypeface(tf);
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.white));*/
            } else {
                holder.binding.imgChk.setVisibility(View.GONE);
                /*holder.binding.tvTitle.setBackgroundColor(Color.TRANSPARENT);
                holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
                Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.SFTEXT_REGULAR);
                holder.binding.tvTitle.setTypeface(tf);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSelected() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (CountryResponse.CountryData data : mDatasetFiltered) {
                    data.isSelected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CountryResponse.CountryData getSelectedItem() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (CountryResponse.CountryData data : mDatasetFiltered) {
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

    public List<CountryResponse.CountryData> getData() {
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
                    //notifyDataSetChanged();
                    if (countryListener != null) {
                        countryListener.onClickCountry(getAdapterPosition(), mDatasetFiltered.get(getAdapterPosition()));
                    }
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
                    List<CountryResponse.CountryData> filteredList = new ArrayList<>();
                    for (CountryResponse.CountryData row : mDataset) {
                        String rowText = row.getCountryName(context.language).toLowerCase();
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
                mDatasetFiltered = (List<CountryResponse.CountryData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
