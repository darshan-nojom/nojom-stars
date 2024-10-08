package com.nojom.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityResetPassBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

public class ResetPasswordActivity extends BaseActivity {

    private ActivityResetPassBinding binding;
    private boolean isNeedToFinish = false;
    private LoginSignUpVM activityViewModel;
    private String email, otp, phone, jwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_pass);
        activityViewModel = new LoginSignUpVM(this);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.etPass, Constants.FONT_AR_REGULAR);
            setArFont(binding.etPassConf, Constants.FONT_AR_REGULAR);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);

        }
        initData();
    }

    private boolean isPasswordVisible = false;
    private boolean isPasswordVisibleConf = false;

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.passwordToggle.setImageResource(R.drawable.eye_closed);
        } else {
            binding.etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.passwordToggle.setImageResource(R.drawable.eye_open);
        }
        isPasswordVisible = !isPasswordVisible;
        binding.etPass.setSelection(binding.etPass.length());
    }

    private void togglePasswordConfVisibility() {
        if (isPasswordVisibleConf) {
            binding.etPassConf.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.passwordToggle2.setImageResource(R.drawable.eye_closed);
        } else {
            binding.etPassConf.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.passwordToggle2.setImageResource(R.drawable.eye_open);
        }
        isPasswordVisibleConf = !isPasswordVisibleConf;
        binding.etPassConf.setSelection(binding.etPassConf.length());
    }

    private void initData() {
        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");
        phone = getIntent().getStringExtra("phone");
        jwtToken = getIntent().getStringExtra("token");
        binding.relLogin.setOnClickListener(view -> sendCode());
        binding.imgBack.setOnClickListener(view -> onBackPressed());
        binding.passwordToggle.setOnClickListener(view -> togglePasswordVisibility());
        binding.passwordToggle2.setOnClickListener(view -> togglePasswordConfVisibility());

        binding.etPass.addTextChangedListener(watcher);
        binding.etPassConf.addTextChangedListener(watcher);

        activityViewModel.isShowProgressForget.observe(this, aBoolean -> {
            if (aBoolean) {
                binding.btnLogin.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.btnLogin.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (isValid()) {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(ResetPasswordActivity.this, R.color.black));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.white));
            } else {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(ResetPasswordActivity.this, R.color.C_E5E5EA));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.c_AAAAAC));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean isValid() {
        if (TextUtils.isEmpty(binding.etPass.getText().toString().trim())) {
            return false;
        }
        if (TextUtils.isEmpty(binding.etPassConf.getText().toString().trim())) {
            return false;
        }
        return binding.etPass.getText().toString().equals(binding.etPassConf.getText().toString());
    }

    private void sendCode() {
        if (TextUtils.isEmpty(binding.etPass.getText().toString().trim())) {
            validationError(getString(R.string.please_enter_password));
            return;
        }
        if (TextUtils.isEmpty(binding.etPassConf.getText().toString().trim())) {
            validationError(getString(R.string.enter_confirm_password));
            return;
        }
        if (binding.etPass.getText().toString().length() < 8) {
            validationError(getString(R.string.you_have_to_enter_at_least_8_digits));
            return;
        }
        if (!binding.etPass.getText().toString().equals(binding.etPassConf.getText().toString())) {
            validationError(getString(R.string.password_not_matched_with_confirm_password));
            return;
        }

//        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(otp)) {//reset pass with email
//            activityViewModel.resetPassword(email, otp, binding.etPass.getText().toString());
//        } else if (!TextUtils.isEmpty(phone)) {//reset pass with phone
//            activityViewModel.resetPasswordByPhone(phone, binding.etPass.getText().toString());
//        }
        activityViewModel.resetPasswordByToken(jwtToken, binding.etPass.getText().toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

}
