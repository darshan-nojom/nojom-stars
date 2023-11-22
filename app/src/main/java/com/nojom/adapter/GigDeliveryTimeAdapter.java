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
import com.nojom.databinding.ItemTimeListBinding;
import com.nojom.model.GigDeliveryTimeModel;

import java.util.ArrayList;
import java.util.List;

public class GigDeliveryTimeAdapter extends RecyclerView.Adapter<GigDeliveryTimeAdapter.SimpleViewHolder>
        implements Filterable {

    private List<GigDeliveryTimeModel.Data> mDataset;
    private List<GigDeliveryTimeModel.Data> mDatasetFiltered;
    private Context context;
    private OnClickDeliveryTimeListener onClickLanguageListener;
    private GigDeliveryTimeModel.Data selectedPosData;

    public interface OnClickDeliveryTimeListener {
        void onClickDeliveryTime(GigDeliveryTimeModel.Data language);
    }

    public GigDeliveryTimeAdapter(Context context, ArrayList<GigDeliveryTimeModel.Data> objects, OnClickDeliveryTimeListener listener) {
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
        ItemTimeListBinding fullBinding =
                ItemTimeListBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(fullBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleViewHolder holder, final int position) {
        try {
            GigDeliveryTimeModel.Data item = mDatasetFiltered.get(position);

            holder.binding.tvCategory.setText(item.deliveryTitle);

            if (item.isSelected) {
                holder.binding.rlView.setBackgroundResource(R.drawable.black_border_5);
                holder.binding.tvCategory.setTextColor(context.getResources().getColor(R.color.black));
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.vw_ic_checked);
                holder.binding.imgCheckUncheck.setColorFilter(ContextCompat.getColor(context,
                        R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                selectedPosData = item;
            } else {
                holder.binding.rlView.setBackgroundResource(R.drawable.gray_border_5);
                holder.binding.tvCategory.setTextColor(context.getResources().getColor(R.color.black_80));
                holder.binding.imgCheckUncheck.setImageResource(R.drawable.vw_ic_uncheck);
                holder.binding.imgCheckUncheck.setColorFilter(ContextCompat.getColor(context,
                        R.color.black_80), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearSelected() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (int i = 0; i < mDatasetFiltered.size(); i++) {
                    mDatasetFiltered.get(i).isSelected = false;
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

    public GigDeliveryTimeModel.Data getSelectedItem() {
        try {
            if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                for (GigDeliveryTimeModel.Data data : mDatasetFiltered) {
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

    public List<GigDeliveryTimeModel.Data> getData() {
        return mDatasetFiltered;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemTimeListBinding binding;

        public SimpleViewHolder(ItemTimeListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.rlView.setOnClickListener(v -> {
                try {
                    clearSelected();
                    mDatasetFiltered.get(getAdapterPosition()).isSelected = true;
                    selectedPosData = mDatasetFiltered.get(getAdapterPosition());
                    if (onClickLanguageListener != null) {
                        onClickLanguageListener.onClickDeliveryTime(mDatasetFiltered.get(getAdapterPosition()));
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
                    List<GigDeliveryTimeModel.Data> filteredList = new ArrayList<>();
                    for (GigDeliveryTimeModel.Data row : mDataset) {
                        String rowText = row.deliveryTitle.toLowerCase();
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
                mDatasetFiltered = (List<GigDeliveryTimeModel.Data>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
