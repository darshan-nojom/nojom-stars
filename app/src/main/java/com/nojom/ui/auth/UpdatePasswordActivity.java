package com.nojom.ui.auth;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityUpdatePasswordBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.Objects;

import io.intercom.android.sdk.Intercom;

public class UpdatePasswordActivity extends BaseActivity {
    private ActivityUpdatePasswordBinding binding;
    private UpdatePasswordActivityVM updatePasswordActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_password);
        updatePasswordActivityVM = ViewModelProviders.of(this).get(UpdatePasswordActivityVM.class);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvSave.setOnClickListener(v -> {
            if (updatePasswordActivityVM.isValid(UpdatePasswordActivity.this, getOldPassword(), getNewPassword(), getConfirmPassword())) {
                updatePasswordActivityVM.updatePassword(this, getOldPassword(), getNewPassword());
            }
        });
        binding.rlSupportChat.setOnClickListener(v ->Intercom.client().displayMessageComposer());

        binding.toolbar.tvTitle.setText(getString(R.string.update_password));

        Utils.trackFirebaseEvent(this, "Update_Password_Screen");

        updatePasswordActivityVM.getIsProgress().observe(this, isShow -> {
            if (isShow) {
                binding.tvSave.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSave.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
            disableEnableTouch(isShow);
        });
    }


    private String getOldPassword() {
        return Objects.requireNonNull(binding.etOldPassword.getText()).toString().trim();
    }

    private String getNewPassword() {
        return Objects.requireNonNull(binding.etNewPassword.getText()).toString().trim();
    }

    private String getConfirmPassword() {
        return Objects.requireNonNull(binding.etConfirmPassword.getText()).toString().trim();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
