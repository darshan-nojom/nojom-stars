package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivitySummaryBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.Objects;

public class SummaryActivity extends BaseActivity {
    private ActivitySummaryBinding binding;
    private SummaryActivityVM summaryActivityVM;
    private ProfileResponse profileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_summary);
        summaryActivityVM = ViewModelProviders.of(this).get(SummaryActivityVM.class);
        summaryActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvSave.setOnClickListener(v -> {
            if (summaryActivityVM.isValid(getSummary())) {
                summaryActivityVM.updateSummary(getSummary());
            }
        });

        profileData = Preferences.getProfileData(this);

        if (profileData != null && profileData.summaries != null)
            binding.etSummary.setText(profileData.summaries);

        binding.etSummary.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.etSummary.setRawInputType(InputType.TYPE_CLASS_TEXT);

        summaryActivityVM.getResponseMutableLiveData().observe(this, generalModelResponse -> {
            if (profileData != null) {
                profileData.summaries = getSummary();
                Preferences.setProfileData(SummaryActivity.this, profileData);
            }
            setResult(RESULT_OK);
            finish();
        });

        summaryActivityVM.getIsShowProgress().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.tvSave.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSave.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String getSummary() {
        return Objects.requireNonNull(binding.etSummary.getText()).toString().trim();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
