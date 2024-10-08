package com.nojom.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nojom.R;
import com.nojom.model.CategoryResponse;
import com.nojom.model.SocialMediaResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> implements Filterable {

    private BaseActivity context;
    private List<CategoryResponse.CategoryData> categoryList;
    private List<CategoryResponse.CategoryData> mDataset;
    private ChipListener chipListener;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    //clearSelectedDuplicate();
                    categoryList = mDataset;
                } else {
                    List<CategoryResponse.CategoryData> filteredList = new ArrayList<>();
//                    for (SocialMediaResponse.Data row : mDataset) {
//                        String rowText = row.name.toLowerCase();
//                        if (!TextUtils.isEmpty(rowText)) {
//                            if (rowText.contains(charString.toLowerCase())/* || row.name.equalsIgnoreCase("Other")*/) {
//                                filteredList.add(row);
//                            }
//                        }
//                    }


                    for (int i = 0; i < mDataset.size(); i++) {
                        String rowText = mDataset.get(i).getName(context.language).toLowerCase();
                        if (!TextUtils.isEmpty(rowText)) {
                            if (rowText.contains(charString.toLowerCase())/* || row.name.equalsIgnoreCase("Other")*/) {
                                filteredList.add(mDataset.get(i));
                                break;
                            }
                        }
                    }

                    categoryList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = categoryList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                categoryList = (List<CategoryResponse.CategoryData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ChipListener {
        void onClickChip(CategoryResponse.CategoryData data, CategoryResponse.CategoryService tag);
    }

    public CategoryAdapter(BaseActivity context, List<CategoryResponse.CategoryData> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.mDataset = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item_new, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryResponse.CategoryData category = categoryList.get(position);
        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.chip_item, holder.chipGroup, false);
        chip.setText(category.getName(context.language));
        chip.setChecked(category.isChecked);
        chip.setOnCheckedChangeListener((compoundButton, b) -> {
            category.isChecked = b;
        });
        holder.chipGroup.addView(chip);
    }

    public List<CategoryResponse.CategoryData> getSelectedChip() {
        return categoryList;
    }

    public List<CategoryResponse.CategoryData> getSelectedChipCopy() {
        return mDataset;
    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ChipGroup chipGroup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chipGroup = itemView.findViewById(R.id.chipGroup);

        }
    }
}
