package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityEditRateBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

public class EditRateActivity extends BaseActivity {
    private ActivityEditRateBinding binding;
    private EditRateActivityVM editRateActivityVM;
    private ProfileResponse profileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_rate);
        binding.setRateAct(this);
        editRateActivityVM = ViewModelProviders.of(this).get(EditRateActivityVM.class);
        initData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        profileData = Preferences.getProfileData(this);
        if (profileData != null && profileData.payRate != null)
            binding.etRate.setText(Utils.numberFormat(String.valueOf(profileData.payRate), 2));

        binding.etRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.toString().startsWith("0")) {
                        binding.etRate.setText(getString(R.string.three));
                        toastMessage(getString(R.string.enter_rate_only_one));
                        return;
                    }
                    String key = s.toString().replaceAll(",", "");
                    if (!isEmpty(key) && !key.equals(".00")) {
                        if (Double.parseDouble(key) >= 100) {
                            binding.etRate.setText(getString(R.string.nine_nine));
                            toastMessage(getString(R.string.enter_rate_only_upto_));
                        } else if (Double.parseDouble(key) < 3) {
                            binding.etRate.setText(getString(R.string.three));
                            toastMessage(getString(R.string.enter_rate_only_one));
                        }
                        binding.btnSave.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etRate.setOnTouchListener((v, event) -> {
            binding.etRate.setFocusable(true);
            binding.etRate.setCursorVisible(true);
            binding.etRate.setSelection(getRate().length());
            return false;
        });

        binding.etRate.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateUi();
                return true;
            }
            return false;
        });

        editRateActivityVM.getAddRate().observe(this, aBoolean -> {
            if (profileData != null) {
                toastMessage(getString(R.string.pay_rate_updated));
                profileData.payRate = Double.parseDouble(getRate());
                Preferences.setProfileData(EditRateActivity.this, profileData);
            }
            onBackPressed();
        });

        editRateActivityVM.getIsShowProgress().observe(this, isShow -> {
            isClickableView = isShow;
            if (isShow) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSave.setVisibility(View.INVISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSave.setVisibility(View.VISIBLE);
            }
        });
    }

    public void onClickAdd() {
        updateUi();
        if (!isEmpty(getRate())) {
            try {
                binding.btnSave.setVisibility(View.VISIBLE);
                double rate = Double.parseDouble(getRate());
                binding.etRate.setText(Utils.numberFormat(String.valueOf(rate + 1), 2));
            } catch (NumberFormatException exception) {
                exception.printStackTrace();
            }
        } else {
            binding.etRate.setText(getString(R.string.three));
        }
    }

    public void onClickMinus() {
        updateUi();
        if (!isEmpty(getRate())) {
            try {
                double rate = Double.parseDouble(getRate());
                if (rate > 3) {
                    binding.btnSave.setVisibility(View.VISIBLE);
                    binding.etRate.setText(Utils.numberFormat(String.valueOf(rate - 1), 2));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            binding.etRate.setText(getString(R.string.three));
        }
    }

    public void onClickSave() {
        updateUi();
        editRateActivityVM.addPayRate(this, getRate());
    }

    private void updateUi() {
        Utils.hideSoftKeyboard(this);
        binding.etRate.setCursorVisible(false);
    }

    public String getRate() {
        return binding.etRate.getText().toString().trim().replaceAll(",", "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
