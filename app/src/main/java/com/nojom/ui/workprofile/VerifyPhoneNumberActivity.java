package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nojom.R;
import com.nojom.databinding.ActivityPhoneVerifyBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

public class VerifyPhoneNumberActivity extends BaseActivity {

    private VerifyPhoneNumberActivityVM verifyPhoneNumberActivityVM;
    private ActivityPhoneVerifyBinding binding;
    static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";


    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;


    private FirebaseAuth mAuth;

    boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_verify);
        verifyPhoneNumberActivityVM = ViewModelProviders.of(this).get(VerifyPhoneNumberActivityVM.class);
        verifyPhoneNumberActivityVM.init(this);
        initData();
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        ProfileResponse profileData = getProfileData();
        binding.etMobile.setEnabled(false);
        binding.ccp.setEnabled(false);
        binding.ccp.setCcpClickable(false);
        if (profileData != null && profileData.contactNo != null) {
            try {
                String[] split = profileData.contactNo.split("\\.");
                if (split.length == 2) {
                    binding.etMobile.setText(split[1]);
                    binding.tvPhonePrefix.setText(split[0]);
                    String nameCode = Preferences.readString(this, COUNTRY_CODE, "");
                    if (!TextUtils.isEmpty(nameCode)) {
                        binding.ccp.setDetectCountryWithAreaCode(false);
                        binding.ccp.setCountryForNameCode(nameCode);
                    } else {
                        String code = split[0].replace("+", "").replace(" ", "");
                        binding.ccp.setCountryForPhoneCode(Integer.parseInt(code));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        binding.tvSubmit.setOnClickListener(v -> {
            try {
                if (binding.tvSubmit.getText().toString().equals(getString(R.string.send_verification_code))) {
                    if (!verifyPhoneNumberActivityVM.isValid(binding.ccp.isValidFullNumber())) {
                        return;
                    }

                    verifyPhoneNumberActivityVM.startPhoneNumberVerification(getMobilePrefix() + getMobile(), mCallbacks, mAuth);
                } else {
                    String code = binding.etOtp.getText().toString();
                    if (isEmpty(code)) {
                        toastMessage(getString(R.string.please_enter_otp));
                        return;
                    }

                    verifyPhoneNumberActivityVM.verifyPhoneNumberWithCode(mVerificationId, code, mAuth);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.tvResendCode.setOnClickListener(v -> {
            setProgressResendCode(true);
            verifyPhoneNumberActivityVM.resendVerificationCode(mResendToken, getMobilePrefix() + getMobile(), mCallbacks);
        });

        binding.ccp.registerCarrierNumberEditText(binding.etMobile);

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                mVerificationInProgress = false;
                verifyPhoneNumberActivityVM.updateUI(STATE_VERIFY_SUCCESS);
                verifyPhoneNumberActivityVM.verifyPhoneNumber();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
//                hideProgress();
                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    binding.etMobile.setError(getString(R.string.invalid_phone_number));
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    toastMessage(getString(R.string.quota_exceeded));
                }

                verifyPhoneNumberActivityVM.updateUI(STATE_VERIFY_FAILED);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                hideProgress();
                mVerificationId = verificationId;
                mResendToken = token;

                verifyPhoneNumberActivityVM.updateUI(STATE_CODE_SENT);
            }
        };

        binding.ccp.setOnCountryChangeListener(() -> binding.tvPhonePrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus()));

        verifyPhoneNumberActivityVM.getVerificationInProgress().observe(this, isProgress -> mVerificationInProgress = isProgress);

        verifyPhoneNumberActivityVM.getStateVisibility().observe(this, state -> {
            switch (state) {
                case STATE_INITIALIZED:
                    binding.llOtp.setVisibility(View.GONE);
                    break;
                case STATE_CODE_SENT:
                    binding.tvSubmit.setText(getString(R.string.verify_otp));
                    binding.llOtp.setVisibility(View.VISIBLE);
                    disableViews(binding.etMobile, binding.ccp);
                    setProgress(false);
                    setProgressResendCode(false);
                    break;
                case STATE_VERIFY_FAILED:
                    binding.tvSubmit.setText(getString(R.string.send_verification_code));
                    binding.llOtp.setVisibility(View.GONE);
                    disableViews(binding.etMobile, binding.ccp);
                    toastMessage(getString(R.string.verification_failed));
                    setProgress(false);
                    break;
            }
        });

        verifyPhoneNumberActivityVM.getIsShowProgress().observe(this, this::setProgress);

        if (TextUtils.isEmpty(binding.etMobile.getText().toString())) {
            toastMessage(getString(R.string.add_mobile_to_profile));
        }
    }

    private void setProgress(Boolean isShow) {
        disableEnableTouch(isShow);
        if (isShow) {
            binding.tvSubmit.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.tvSubmit.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void setProgressResendCode(Boolean isShow) {
        disableEnableTouch(isShow);
        if (isShow) {
            binding.tvResendCode.setVisibility(View.GONE);
            binding.progressBarResend.setVisibility(View.VISIBLE);
        } else {
            binding.tvResendCode.setVisibility(View.VISIBLE);
            binding.progressBarResend.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVerificationInProgress && verifyPhoneNumberActivityVM.isValid(binding.ccp.isValidFullNumber())) {
            verifyPhoneNumberActivityVM.startPhoneNumberVerification(getMobilePrefix() + getMobile(), mCallbacks, mAuth);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    private String getMobile() {
        String mobile = binding.etMobile.getText().toString().trim();
        return mobile.replace(" ", "");
    }

    private String getMobilePrefix() {
        return binding.ccp.getSelectedCountryCodeWithPlus();
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }
}
