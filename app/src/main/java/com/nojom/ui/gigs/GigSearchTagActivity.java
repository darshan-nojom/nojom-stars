package com.nojom.ui.gigs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.chip.Chip;
import com.nojom.R;
import com.nojom.adapter.SearchTagAdapter;
import com.nojom.databinding.ActivitySearchTagsBinding;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.Objects;


public class GigSearchTagActivity extends BaseActivity implements Constants {
    private ActivitySearchTagsBinding binding;
    private ArrayList<String> selectedTags;
    private ArrayList<GigSubCategoryModel.Data> subCatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_tags);
        binding.toolbar.tvTitle.setText(getString(R.string.search_tag));
        initData();
    }

    private void initData() {
        if (getIntent() != null) {
            selectedTags = getIntent().getStringArrayListExtra("data");
            subCatList = (ArrayList<GigSubCategoryModel.Data>) getIntent().getSerializableExtra("list");
        }

        if (selectedTags == null) {
            selectedTags = new ArrayList<>();
        } else {
            addDefaultTag();
        }

//        binding.etSearchTags.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        binding.etSearchTags.setRawInputType(InputType.TYPE_CLASS_TEXT);

        binding.tvAdd.setOnClickListener(v -> {
            if (selectedTags != null && selectedTags.size() > 4) {
                toastMessage(getString(R.string.you_can_add_max_five_tags));
                return;
            }
            addTag(getSearchTag());
        });

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        binding.tvSave.setOnClickListener(v -> {
            if (selectedTags == null || selectedTags.size() == 0) {
                toastMessage(getString(R.string.please_add_min_one_tag));
                return;
            }
            if (selectedTags.size() < 6) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("tags", selectedTags);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                toastMessage(getString(R.string.you_can_add_max_five_tags));
            }
        });

        try {
            SearchTagAdapter adapter = new SearchTagAdapter(this, R.layout.layout_textview, R.id.text, subCatList);
            binding.etSearchTags.setThreshold(2);
            binding.etSearchTags.setAdapter(adapter);
            binding.etSearchTags.setOnItemClickListener((adapterView, view, pos, id) -> {
                GigSubCategoryModel.Data selectedSkill = (GigSubCategoryModel.Data) adapterView.getItemAtPosition(pos);
                if (selectedTags != null && selectedTags.size() > 4) {
                    toastMessage(getString(R.string.you_can_add_max_five_tags));
                    return;
                }
                if (selectedTags != null && selectedTags.contains(selectedSkill.getName(language))) {
                    toastMessage(selectedSkill.getName(language) + " " + getString(R.string.already_selected));
                    binding.etSearchTags.setText("");
                    return;
                }
                addTag(selectedSkill.getName(language));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSearchTag() {
        return Objects.requireNonNull(binding.etSearchTags.getText()).toString().trim();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    private void addTag(String tagName) {
        if (TextUtils.isEmpty(tagName)) {
            return;
        }
        binding.etSearchTags.setText("");
        Chip chip = makeChipView(tagName);
        selectedTags.add(tagName);
        binding.tagGroup.addView(chip);
    }

    private Chip makeChipView(String tagName) {
        Chip chip = new Chip(this);
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(tagName);
        chip.setCloseIconResource(R.drawable.close_gray);
        chip.setCloseIconEnabled(true);
        chip.setOnCloseIconClickListener(v -> {
            selectedTags.remove(tagName);
            binding.tagGroup.removeView(chip);
        });
        return chip;
    }

    private void addDefaultTag() {
        for (int i = 0; i < selectedTags.size(); i++) {
            Chip chip = makeChipView(selectedTags.get(i));
            binding.tagGroup.addView(chip);
        }
    }
}
