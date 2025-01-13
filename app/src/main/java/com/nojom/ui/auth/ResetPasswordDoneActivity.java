package com.nojom.ui.auth;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityResetPassSuccessBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

public class ResetPasswordDoneActivity extends BaseActivity {

    private ActivityResetPassSuccessBinding binding;
    private boolean isFromMawthooq, isFromOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_pass_success);
        isFromMawthooq = getIntent().getBooleanExtra("isFrom", false);
        isFromOverview = getIntent().getBooleanExtra("from", false);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_BOLD);
            setArFont(binding.tv2, Constants.FONT_AR_BOLD);
            setArFont(binding.tv3, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv4, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv4, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv5, Constants.FONT_AR_REGULAR);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
        }
        initData();
    }

    private void initData() {

        if (isFromMawthooq) {
            binding.tv1.setText(getString(R.string.we_will_respond_within_24_hours));
            binding.tv2.setText(getString(R.string.we_will_respond_within_24_hours_24));
            binding.tv3.setText(getString(R.string.the_account_is_currently_under_review));
            binding.tv2.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.tv3.setVisibility(View.GONE);
            binding.tv4.setVisibility(View.GONE);
            binding.btnLogin.setText(isFromOverview ? getString(R.string.go_to_overview) : getString(R.string.go_to_portfolio));
        }

        binding.relLogin.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFromMawthooq) {
            if (isFromOverview) {
                finish();
            } else {
                gotoMainActivity(0);
            }
        } else {
            goToLoginSignup(true);
        }
    }
}
