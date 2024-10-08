package com.nojom.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;

import com.facebook.login.LoginManager;
import com.nojom.R;
import com.nojom.databinding.ActivityForgotPasswordBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.CodeActivity;
import com.nojom.ui.workprofile.OtpActivity;
import com.nojom.util.Constants;
import com.nojom.util.SegmentedButtonGroupNew;
import com.nojom.util.Utils;

import java.util.Arrays;
import java.util.Objects;

public class ForgotPasswordActivity extends BaseActivity implements LoginInteractor {

    private ActivityForgotPasswordBinding binding;
    private boolean isNeedToFinish = false;
    private LoginSignUpVM activityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        activityViewModel = new LoginSignUpVM(this);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.etMobile, Constants.FONT_AR_REGULAR);
            setArFont(binding.etEmail, Constants.FONT_AR_REGULAR);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
        }
        initData();
    }

    private void initData() {

        binding.ccp.registerCarrierNumberEditText(binding.etMobile);
        binding.ccp.setOnCountryChangeListener(() -> {
            binding.etMobile.setText("");
            //binding.txtPrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());
            binding.ccp.setTag(binding.ccp.getSelectedCountryCodeWithPlus());
        });
        //binding.txtPrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());

        binding.relLogin.setOnClickListener(view -> sendCode());
        binding.imgBack.setOnClickListener(view -> onBackPressed());

        binding.etMobile.addTextChangedListener(watcher);
        binding.etEmail.addTextChangedListener(watcher);

        activityViewModel.isShowProgressForget.observe(this, aBoolean -> {
            if (aBoolean) {
                binding.btnLogin.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.btnLogin.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        activityViewModel.sendCodeSuccess.observe(this, aBoolean -> {
            if (aBoolean) {
                //open next screen for OTP
                Intent intent = new Intent(this, CodeActivity.class);
                intent.putExtra("mobile", getMobile());
                intent.putExtra("prefix", binding.ccp.getSelectedCountryCodeWithPlus());
                intent.putExtra("email", getEmail());
                intent.putExtra("isFromForget", true);
                startActivity(intent);
            }
        });
        phoneView();
        binding.linPhoneTab.setOnClickListener(view -> phoneView());
        binding.linEmailTab.setOnClickListener(view -> EmailView());
    }

    private void sendCode() {
        if (isValidData(true)) {
            if (!TextUtils.isEmpty(getEmail())) {
                activityViewModel.sendOTP(getEmail(), "");
            } else {
                activityViewModel.sendOTP("", binding.ccp.getSelectedCountryCodeWithPlus() + "." + getMobile());
            }
        }
//        if (binding.segmentLoginGroup.getPosition() == 0) {
//            if (!TextUtils.isEmpty(getMobile())) {
//                activityViewModel.checkContactExistOrNot(binding.ccp.getSelectedCountryCodeWithPlus() + "." + getMobile());
//            }
//        } else {
//            if (!TextUtils.isEmpty(getEmail())) {
//                activityViewModel.forgotPassword(getEmail());
//            }
//        }


    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (isValidData(false)) {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(ForgotPasswordActivity.this, R.color.black));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.white));
            } else {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(ForgotPasswordActivity.this, R.color.C_E5E5EA));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.c_AAAAAC));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public String getEmail() {
        return Objects.requireNonNull(binding.etEmail.getText()).toString();
    }

    public String getMobile() {
        return Objects.requireNonNull(binding.etMobile.getText()).toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public SegmentedButtonGroupNew.OnPositionChangedListener onPositionChangedListener = new SegmentedButtonGroupNew.OnPositionChangedListener() {
        @Override
        public void onPositionChanged(int position) {
            if (position == 0) {//phone
                binding.etEmail.setText("");
                binding.linPhone.setVisibility(View.VISIBLE);
                binding.etEmail.setVisibility(View.GONE);
            } else if (position == 1) {//email
                binding.etMobile.setText("");
                binding.linPhone.setVisibility(View.GONE);
                binding.etEmail.setVisibility(View.VISIBLE);
            }
        }
    };

    public void onClickBack() {
        onBackPressed();
    }


    private boolean isValidData(boolean isCheck) {
        if (binding.linPhone.isShown()) {
            if (TextUtils.isEmpty((Objects.requireNonNull(binding.etMobile.getText()).toString()))) {
                if (isCheck) {
                    validationError(getString(R.string.enter_valid_number));
                }
                return false;
            }

        } else {
            if (TextUtils.isEmpty((Objects.requireNonNull(binding.etEmail.getText()).toString()))) {
                if (isCheck) {
                    validationError(getString(R.string.enter_valid_email));
                }
                return false;
            }
            if (!isValidEmailData((Objects.requireNonNull(binding.etEmail.getText()).toString()))) {
                if (isCheck) {
                    validationError(getString(R.string.enter_valid_email));
                }
                return false;
            }

        }
        return true;
    }

    public void onClickFacebook() {
//        if (layoutBinderHelper.getShow_login()) {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.login_with_facebook_button_click));
//        } else {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.sign_up_with_facebook_button_click));
//        }
        Utils.hideSoftKeyboard(this);
        //layoutBinderHelper.setIsLoadingFb(true);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }


    @Override
    public void redirect(boolean isSignup) {
        if (isSignup) {
            Intent intent = new Intent(this, OtpActivity.class);
            intent.putExtra("mobile", getMobile());
            intent.putExtra("prefix", binding.ccp.getSelectedCountryCodeWithPlus());
            startActivity(intent);
//            redirectActivity(NameActivity.class);
        } else {
            if (isNeedToFinish) {
                finish();
            } else {
                if (getProfileData() != null && getProfileData().trustRate != null && getProfileData().trustRate.phoneNumber == 0) {//if number is not verified then again open while login case

                    if (getProfileData().contactNo != null) {
                        Intent intent = new Intent(this, OtpActivity.class);
                        String[] split = getProfileData().contactNo.split("\\.");
                        if (split.length == 2) {
                            intent.putExtra("mobile", split[1]);
                            intent.putExtra("prefix", split[0]);
                        }
                        startActivity(intent);
                    } else {
                        if (getProfileData().is_verified == 0) {
                            openSteps(getProfileData().registration_step);
//                            redirectActivity(ProfileVerificationActivity.class);
                        } else {
                            gotoMainActivity(Constants.TAB_HOME);
                        }
                    }
                } else {
                    if (getProfileData().is_verified == 0) {
//                        redirectActivity(ProfileVerificationActivity.class);
                        openSteps(getProfileData().registration_step);
                    } else {
                        gotoMainActivity(Constants.TAB_HOME);
                    }
                }
            }
        }
    }

    private void EmailView() {
        binding.linPhone.setVisibility(View.GONE);
        binding.etEmail.setVisibility(View.VISIBLE);
        binding.tvEmail.setTextColor(getResources().getColor(R.color.C_020814));
        binding.tvPhone.setTextColor(getResources().getColor(R.color.C_3C3C43));
        binding.v2.setVisibility(View.VISIBLE);
        binding.v1.setVisibility(View.GONE);
        binding.etEmail.setText("");
    }

    private void phoneView() {
        binding.linPhone.setVisibility(View.VISIBLE);
        binding.etEmail.setVisibility(View.GONE);
        binding.tvPhone.setTextColor(getResources().getColor(R.color.C_020814));
        binding.tvEmail.setTextColor(getResources().getColor(R.color.C_3C3C43));
        binding.v1.setVisibility(View.VISIBLE);
        binding.v2.setVisibility(View.GONE);
        binding.etMobile.setText("");
    }
}
