package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.GigCategoryAdapter;
import com.nojom.databinding.ActivityCategoryBinding;
import com.nojom.model.GigCategoryModel;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class GigCategoryActivity extends BaseActivity implements GigCategoryAdapter.OnClickLanguageListener {
    private ActivityCategoryBinding binding;
    private GigCategoryModel.Data selectedCategory = null;
    private GigCategoryAdapter mAdapter;
    private ArrayList<GigCategoryModel.Data> gigCatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category);
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
        if (getIntent() != null) {
            selectedCategory = (GigCategoryModel.Data) getIntent().getSerializableExtra("data");
            gigCatList = (ArrayList<GigCategoryModel.Data>) getIntent().getSerializableExtra("list");
        }

        setUI();
    }

    private void setUI() {
        binding.tvTitle.setText(getString(R.string.what_is_your_cat));
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.progress.tvSave.setText(getString(R.string.apply));

        if (gigCatList != null && gigCatList.size() > 0) {
            setAdapter(gigCatList);
        }

        binding.progress.tvSave.setOnClickListener(v -> {
            if (selectedCategory == null) {
                toastMessage(getString(R.string.select_your_category));
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("category", selectedCategory);
            setResult(RESULT_OK, intent);
            finish();
        });
        binding.rvCategory.setLayoutManager(new LinearLayoutManager(this));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter("" + s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setAdapter(ArrayList<GigCategoryModel.Data> data) {
        if (data != null && data.size() > 0) {

            for (GigCategoryModel.Data cat : data) {
                if (selectedCategory != null && cat.id == selectedCategory.id) {
                    cat.isSelected = true;
                    break;
                }
            }

            mAdapter = new GigCategoryAdapter(this, data, this);
            binding.rvCategory.setAdapter(mAdapter);

        }
    }

    @Override
    public void onClickLanguage(GigCategoryModel.Data category) {
        selectedCategory = category;
    }
}
