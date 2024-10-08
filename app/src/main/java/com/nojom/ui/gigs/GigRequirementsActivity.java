package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.GigRequirementAdapter;
import com.nojom.databinding.ActivityCategoryBinding;
import com.nojom.model.GigRequirementsModel;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class GigRequirementsActivity extends BaseActivity implements GigRequirementAdapter.OnClickRequirementListener {
    private ActivityCategoryBinding binding;
    private ArrayList<GigRequirementsModel.Data> selectedLanguageList;
    private GigRequirementAdapter mAdapter;
    private ArrayList<GigRequirementsModel.Data> requirementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category);
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.shimmerLayout.stopShimmer();
        binding.tvTitle.setText(getString(R.string.requirements));
        binding.etSearch.setHint(getString(R.string.search_for_requirement));

        selectedLanguageList = (ArrayList<GigRequirementsModel.Data>) getIntent().getSerializableExtra("data");
        requirementList = (ArrayList<GigRequirementsModel.Data>) getIntent().getSerializableExtra("list");

        if (selectedLanguageList == null) {
            selectedLanguageList = new ArrayList<>();
        }

        initData();

    }

    private void initData() {
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.progress.tvSave.setText(getString(R.string.apply));

        if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
            for (GigRequirementsModel.Data mainLang : requirementList) {
                for (GigRequirementsModel.Data selLang : selectedLanguageList) {
                    if (mainLang.id == selLang.id) {
                        mainLang.isSelected = true;
                        break;
                    }
                }
            }
        }

        mAdapter = new GigRequirementAdapter(GigRequirementsActivity.this, requirementList, GigRequirementsActivity.this);
        binding.rvCategory.setAdapter(mAdapter);

        binding.progress.tvSave.setOnClickListener(v -> {
            if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
                Intent intent = new Intent();
                intent.putExtra("data", selectedLanguageList);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                toastMessage(getString(R.string.select_your_req));
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public void addRemoveItem(boolean isAdded, GigRequirementsModel.Data model) {
        if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
            for (int i = 0; i < selectedLanguageList.size(); i++) {
                if (isAdded) {
                    selectedLanguageList.add(model);
                    break;
                } else {
                    if (selectedLanguageList.get(i).id == model.id) {
                        selectedLanguageList.remove(i);
                        break;
                    }
                }
            }
        } else {
            selectedLanguageList.add(model);
        }
    }

    @Override
    public void onClickLanguage(boolean isAdded, GigRequirementsModel.Data language) {
        addRemoveItem(isAdded, language);
    }
}
