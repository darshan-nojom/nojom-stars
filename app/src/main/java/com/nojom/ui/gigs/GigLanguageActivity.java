package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.GigLanguageAdapter;
import com.nojom.databinding.ActivityCategoryBinding;
import com.nojom.model.Language;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class GigLanguageActivity extends BaseActivity implements GigLanguageAdapter.OnClickLanguageListener {
    private ActivityCategoryBinding binding;
    private GigLanguageActivityVM gigLanguageActivityVM;
    private ArrayList<Language.Data> selectedLanguageList;
    private GigLanguageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category);
        binding.tvTitle.setText(getString(R.string.language));
        binding.etSearch.setHint(getString(R.string.search_for_language));

        selectedLanguageList = (ArrayList<Language.Data>) getIntent().getSerializableExtra("data");

        if (selectedLanguageList == null) {
            selectedLanguageList = new ArrayList<>();
        }

        gigLanguageActivityVM = ViewModelProviders.of(this).get(GigLanguageActivityVM.class);
        setUI();
        gigLanguageActivityVM.init(this);

    }

    private void setUI() {
        binding.progress.imgBack.setOnClickListener(v -> finish());
        binding.progress.tvSave.setText(getString(R.string.apply));

//        runOnUiThread(() -> {
//            ArrayList<Language.Data> gigLang = Preferences.getGigLanguage(GigLanguageActivity.this);
//            if (gigLang != null && gigLang.size() > 0) {
//                if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
//                    for (Language.Data mainLang : gigLang) {
//                        for (Language.Data selLang : selectedLanguageList) {
//                            if (mainLang.id == selLang.id) {
//                                mainLang.isSelected = true;
//                                break;
//                            }
//                        }
//                    }
//                }
//
//                mAdapter = new GigLanguageAdapter(GigLanguageActivity.this, gigLang, GigLanguageActivity.this);
//                binding.rvCategory.setAdapter(mAdapter);
//            }
//        });

        binding.progress.tvSave.setOnClickListener(v -> {
//            selectedLanguageList = mAdapter != null ? mAdapter.getSelectedItem() : null;
            if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
                Intent intent = new Intent();
                intent.putExtra("data", selectedLanguageList);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                toastMessage(getString(R.string.please_select_language));
            }
        });
        binding.rvCategory.setLayoutManager(new LinearLayoutManager(this));

        gigLanguageActivityVM.getIsShowProgress().observe(this, integer -> {
            disableEnableTouch(true);
            binding.shimmerLayout.setVisibility(View.VISIBLE);
            binding.shimmerLayout.startShimmer();
        });

        gigLanguageActivityVM.getIsHideProgress().observe(this, integer -> {
            disableEnableTouch(false);
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.shimmerLayout.stopShimmer();
        });

        gigLanguageActivityVM.getGigLanguageList().observe(this, data -> {
            if (data != null && data.size() > 0) {

                if (selectedLanguageList != null && selectedLanguageList.size() > 0) {
                    for (Language.Data mainLang : data) {
                        for (Language.Data selLang : selectedLanguageList) {
                            if (mainLang.id == selLang.id) {
                                mainLang.isSelected = true;
                                break;
                            }
                        }
                    }
                }

                mAdapter = new GigLanguageAdapter(this, data, this);
                binding.rvCategory.setAdapter(mAdapter);
            }
        });

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
    public void onClickLanguage(boolean isAdded, Language.Data language, int adapPos) {
        addRemoveItem(isAdded, language);
    }

    public void addRemoveItem(boolean isAdded, Language.Data model) {
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
}
