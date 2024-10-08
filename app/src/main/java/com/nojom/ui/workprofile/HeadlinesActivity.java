package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityHeadlinesBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.util.Objects;

public class HeadlinesActivity extends BaseActivity implements Constants {
    private ActivityHeadlinesBinding binding;
    private HeadlinesActivityVM headlinesActivityVM;
    private ProfileResponse profileData;
    private int screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_headlines);
        headlinesActivityVM = ViewModelProviders.of(this).get(HeadlinesActivityVM.class);
        headlinesActivityVM.init(this);
        initData();
    }

    private void initData() {
        profileData = Preferences.getProfileData(this);

        if (getIntent() != null) {
            screen = getIntent().getIntExtra("screen", 0);
        }

        if (screen == HEADLINE) {
//            binding.etHeadline.setKeyListener(DigitsKeyListener.getInstance("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz. "));
            binding.etHeadline.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
            binding.tvTitle.setText(getString(R.string.professional_headline));
            binding.etHeadline.setHint(String.format(getString(R.string.enter_s), getString(R.string.professional_headline)));
            if (profileData != null && profileData.headlines != null)
                binding.etHeadline.setText(profileData.headlines);
        } else if (screen == ADDRESS) {
            binding.tvTitle.setText(getString(R.string.professional_address));
            binding.etHeadline.setHint(String.format(getString(R.string.enter_s), getString(R.string.professional_address)));
            binding.etHeadline.setText(String.format("%s, %s, %s", profileData.getCityName(language), profileData.getStateName(language), profileData.getCountryName(language)));
        } else if (screen == OFFICE_ADD) {
            binding.tvTitle.setText(getString(R.string.professional_address));
            binding.etHeadline.setHint(String.format(getString(R.string.enter_s), getString(R.string.professional_address)));
            binding.etHeadline.setText(TextUtils.isEmpty(profileData.addProAddress) ? "" : profileData.addProAddress);
        } else if (screen == PHONE) {
            binding.tvTitle.setText(getString(R.string.professional_phone));
            binding.etHeadline.setHint(String.format(getString(R.string.enter_s), getString(R.string.professional_phone)));
            if (profileData.contactNo != null)
                binding.etHeadline.setText(profileData.contactNo);
        } else if (screen == EMAIL) {
            binding.tvTitle.setText(getString(R.string.professional_email));
            binding.etHeadline.setHint(String.format(getString(R.string.enter_s), getString(R.string.professional_email)));
            if (profileData.email != null)
                binding.etHeadline.setText(profileData.email);
        } else if (screen == WEBSITE) {
            binding.tvTitle.setText(getString(R.string.professional_website));
            binding.etHeadline.setHint(String.format(getString(R.string.enter_s), getString(R.string.professional_website)));
            if (profileData.website != null)
                binding.etHeadline.setText(profileData.website);
        }

        binding.etHeadline.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.etHeadline.setRawInputType(InputType.TYPE_CLASS_TEXT);

        binding.tvSave.setOnClickListener(v -> {
            if (headlinesActivityVM.isValid(getHeadline(), screen)) {
                if (screen == HEADLINE) {
                    headlinesActivityVM.updateHeadlines(getHeadline());
                } else if (screen == WEBSITE) {
                    headlinesActivityVM.updateWebsite(getHeadline());
                } else if (screen == OFFICE_ADD) {
                    if (getHeadline().length() < 10) {
                        toastMessage(getString(R.string.enter_min_1o_char));
                        return;
                    }
                    headlinesActivityVM.addProfAddress(getHeadline());
                }
            }
        });

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        headlinesActivityVM.getUpdateHeadlineRes().observe(this, generalModelResponse -> {
            if (profileData != null) {
                profileData.headlines = getHeadline();
                Preferences.setProfileData(HeadlinesActivity.this, profileData);
            }
            setResult(RESULT_OK);
            finish();
        });

        headlinesActivityVM.getIsShowProgressDialog().observe(this, isShow -> {
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

    private String getHeadline() {
        return Objects.requireNonNull(binding.etHeadline.getText()).toString().trim();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
