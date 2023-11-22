package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityEmailVerifyBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

public class VerifyEmailActivity extends BaseActivity {

    private VerifyEmailActivityVM verifyEmailActivityVM;
    private ActivityEmailVerifyBinding binding;
    private boolean isVerifyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_verify);
        verifyEmailActivityVM = ViewModelProviders.of(this).get(VerifyEmailActivityVM.class);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvSubmit.setOnClickListener(v -> {
//            if (binding.tvSubmit.getText().toString().equals(getString(R.string.send_verification_email))) {
            if (verifyEmailActivityVM.isValid(this, getEmail())) {
                verifyEmailActivityVM.verifyEmail(this);
            }
        });

        ProfileResponse profileData = Preferences.getProfileData(this);
        binding.etEmail.setText(profileData.email);
        binding.etEmail.setEnabled(false);

        verifyEmailActivityVM.getIsVerifyEmail().observe(this, isVerify -> isVerifyEmail = isVerify);

        verifyEmailActivityVM.getIsShowLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.tvSubmit.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSubmit.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
            disableEnableTouch(isLoading);
        });
    }


    public String getEmail() {
        return binding.etEmail.getText().toString().trim();
    }

    private String getOtp() {
        return binding.etOtp.getText().toString().trim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isVerifyEmail) {
            isVerifyEmail = false;
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
