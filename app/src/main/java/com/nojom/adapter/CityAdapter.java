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
import com.nojom.model.CityResponse;
import com.nojom.model.CountryResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.SimpleViewHolder>
        implements Filterable {

    private List<CityResponse.CityData> mDataset;
    private List<CityResponse.CityData> mDatasetFiltered;
    private BaseActivity context;
    private CityListener cityListener;

    public interface CityListener {
        void onClickCity(int pos, CityResponse.CityData data);
    }

    public void setCityListener(CityListener cityListener) {
        this.cityListener = cityListener;
    }

    public CityAdapter(BaseActivity context, List<CityResponse.CityData> objects) {
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
            CityResponse.CityData item = mDatasetFiltered.get(position);

            holder.binding.tvTitle.setText(item.getCityName(context.language));

            if (item.isSelected) {
                holder.binding.imgChk.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgChk.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSelected() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (CityResponse.CityData data : mDatasetFiltered) {
                    data.isSelected = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CityResponse.CityData getSelectedItem() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (CityResponse.CityData data : mDatasetFiltered) {
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

    public List<CityResponse.CityData> getData() {
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
                    if (cityListener != null) {
                        cityListener.onClickCity(getAdapterPosition(), mDatasetFiltered.get(getAdapterPosition()));
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
                    List<CityResponse.CityData> filteredList = new ArrayList<>();
                    for (CityResponse.CityData row : mDataset) {
                        String rowText = row.getCityName(context.language).toLowerCase();
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
                mDatasetFiltered = (List<CityResponse.CityData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
