package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.textview.TextViewSFTextPro;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.databinding.ActivitySelectSubcatBinding;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class GigSubCategoryActivity extends BaseActivity {
    private ActivitySelectSubcatBinding binding;
    private RecyclerViewAdapter adapter;
    private GigSubCategoryModel.Data selectedSubCat = null;
    private ArrayList<GigSubCategoryModel.Data> subCatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_subcat);
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.shimmerLayout.stopShimmer();
        binding.toolbarSave.tvToolbarTitle.setText(getString(R.string.what_your_skills));

        subCatList = (ArrayList<GigSubCategoryModel.Data>) getIntent().getSerializableExtra("list");
        selectedSubCat = (GigSubCategoryModel.Data) getIntent().getSerializableExtra("data");

        setUI();


    }

    private void setUI() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvSelectedSkills.setLayoutManager(linearLayoutManager);
        binding.toolbarSave.tvSave.setText(getString(R.string.apply));

        binding.toolbarSave.imgBack.setOnClickListener(v -> onBackPressed());

        binding.toolbarSave.tvSave.setOnClickListener(v -> {
            if (selectedSubCat != null) {
                Intent intent = new Intent();
                intent.putExtra("data", selectedSubCat);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                toastMessage(getString(R.string.select_your_skills));
            }
        });

        if (subCatList != null && subCatList.size() > 0) {
            if (selectedSubCat != null) {
                for (GigSubCategoryModel.Data mainCat : subCatList) {
                    if (mainCat.id == selectedSubCat.id) {
                        mainCat.isSelected = true;
                        break;
                    }
                }
            }

            adapter = new RecyclerViewAdapter(subCatList);
            binding.rvSelectedSkills.setAdapter(adapter);
        }

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter("" + s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> implements Filterable {

        private ArrayList<GigSubCategoryModel.Data> subCatList;
        private ArrayList<GigSubCategoryModel.Data> mDatasetFiltered;
        private GigSubCategoryModel.Data selectedPosData;

        public RecyclerViewAdapter(ArrayList<GigSubCategoryModel.Data> modelList) {
            subCatList = modelList;
            mDatasetFiltered = modelList;
        }

        private void clearSelected() {
            try {
                if (subCatList != null && subCatList.size() > 0) {
                    for (GigSubCategoryModel.Data data : subCatList) {
                        data.isSelected = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void clearSelectedDuplicate() {
            try {
                if (mDatasetFiltered != null && mDatasetFiltered.size() > 0) {
                    for (int i = 0; i < mDatasetFiltered.size(); i++) {
                        mDatasetFiltered.get(i).isSelected = selectedPosData != null && selectedPosData.id == mDatasetFiltered.get(i).id;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final GigSubCategoryModel.Data model = subCatList.get(position);
            holder.tvSkillName.setText(model.getName(language));

            if (model.isSelected) {
                holder.imgRemove.setImageResource(R.drawable.check_done);
                holder.imgRemove.clearColorFilter();
                selectedPosData = model;
            } else {
                holder.imgRemove.setImageResource(R.drawable.circle_uncheck);
                holder.imgRemove.setColorFilter(ContextCompat.getColor(GigSubCategoryActivity.this,
                        R.color.full_dark_green), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            holder.itemView.setOnClickListener(view -> {
                clearSelected();
                model.isSelected = true;
                holder.imgRemove.setImageResource(R.drawable.check_done);
                holder.imgRemove.clearColorFilter();
                notifyDataSetChanged();
                selectedSubCat = model;
                selectedPosData = model;
            });
        }

        @Override
        public int getItemCount() {
            return subCatList == null ? 0 : subCatList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextViewSFTextPro tvSkillName;
            private ImageView imgRemove;

            private MyViewHolder(View itemView) {
                super(itemView);

                tvSkillName = itemView.findViewById(R.id.tv_category);
                imgRemove = itemView.findViewById(R.id.img_check_uncheck);
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
                        subCatList = mDatasetFiltered;
                    } else {
                        ArrayList<GigSubCategoryModel.Data> filteredList = new ArrayList<>();
                        for (GigSubCategoryModel.Data row : mDatasetFiltered) {
                            String rowText = row.getName(language).toLowerCase();
                            if (!TextUtils.isEmpty(rowText)) {
                                if (rowText.contains(charString.toLowerCase())) {
                                    filteredList.add(row);
                                }
                            }
                        }

                        subCatList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = subCatList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    subCatList = (ArrayList<GigSubCategoryModel.Data>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}
