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
import com.nojom.databinding.ActivityPayRateBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.policy.NewPolicyActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.Objects;

import retrofit2.Response;

public class PayRateActivity extends BaseActivity implements ResponseListener {
    private ActivityPayRateBinding binding;
    private PayRateActivityVM payRateActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_rate);
        binding.setPayRateAct(this);
        payRateActivityVM = ViewModelProviders.of(this).get(PayRateActivityVM.class);
        initData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        payRateActivityVM.setResponseListener(this);

        binding.toolbar.progress.setProgress(100);

        binding.etRate.addTextChangedListener(textWatcherListener);

        binding.etRate.setOnTouchListener((v, event) -> {
            binding.etRate.setFocusable(true);
            binding.etRate.setCursorVisible(true);
            binding.etRate.setSelection(getRate().length());
            return false;
        });

        binding.etRate.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Utils.hideSoftKeyboard(this);
                binding.etRate.setCursorVisible(false);
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickTermsCond() {
        updateUi();
        redirectActivity(NewPolicyActivity.class);
    }

    public void increase() {
        updateUi();
        if (!isEmpty(Objects.requireNonNull(getRate()))) {
            double rate = Double.parseDouble(getRate());
            binding.etRate.setText(Utils.numberFormat(String.valueOf(rate + 1), 2));
        } else {
            binding.etRate.setText(getString(R.string.three));
        }
    }

    public void decrease() {
        updateUi();
        if (!isEmpty(Objects.requireNonNull(getRate()))) {
            double rate = Double.parseDouble(getRate());
            if (rate > 3) {
                binding.etRate.setText(Utils.numberFormat(String.valueOf(rate - 1), 2));
            }
        } else {
            binding.etRate.setText(getString(R.string.three));
        }
    }

    public void onClickSubmit() {
        updateUi();
        if (binding.chkTnc.isChecked()) {
            binding.btnSubmit.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            payRateActivityVM.addPayRate(this, getRate());
        } else {
            validationError(getString(R.string.please_check_terms_conditions));
        }
    }

    private void updateUi() {
        Utils.hideSoftKeyboard(this);
        binding.etRate.setCursorVisible(false);
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        gotoMainActivity(Constants.TAB_HOME);
        binding.btnSubmit.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        binding.btnSubmit.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    public String getRate() {
        return binding.etRate.getText().toString().trim().replaceAll(",", "");
    }

    TextWatcher textWatcherListener = new TextWatcher() {
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
                }
            } catch (NumberFormatException e) {
                binding.etRate.setText(getString(R.string.three));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
