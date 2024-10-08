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
import com.nojom.databinding.ActivityOtpBinding;
import com.nojom.databinding.ActivityPhoneVerifyBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

public class OtpActivity extends BaseActivity {

    private OtpActivityVM verifyPhoneNumberActivityVM;
    private ActivityOtpBinding binding;
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

    String phone, prefix, pass, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp);
        verifyPhoneNumberActivityVM = ViewModelProviders.of(this).get(OtpActivityVM.class);
        verifyPhoneNumberActivityVM.init(this, mAuth);

        phone = getIntent().getStringExtra("mobile");
        prefix = getIntent().getStringExtra("prefix");
        pass = getIntent().getStringExtra("pass");
        email = getIntent().getStringExtra("email");

        initData();
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtPhone.setText(prefix + " " + phone);

        binding.btnSubmit.setOnClickListener(v -> {
            try {

                String otp = binding.pinview.getText().toString();
                if (isEmpty(otp)) {
                    toastMessage(getString(R.string.please_enter_otp));
                    return;
                }
                setProgress(true);
                verifyPhoneNumberActivityVM.verifyPhoneNumberWithCode(mVerificationId, otp, mAuth, pass, email, getToken(), phone, prefix);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.tvReesendCode.setOnClickListener(v -> {
            setProgressResendCode(true);
            verifyPhoneNumberActivityVM.resendVerificationCode(mResendToken, prefix + phone, mCallbacks);
        });

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                mVerificationInProgress = false;
                verifyPhoneNumberActivityVM.updateUI(STATE_VERIFY_SUCCESS);
                verifyPhoneNumberActivityVM.onCreateAccount(pass, email, getToken(), prefix, phone);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
//                hideProgress();
                mVerificationInProgress = false;

                if (e instanceof FirebaseTooManyRequestsException) {
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

        verifyPhoneNumberActivityVM.getVerificationInProgress().observe(this, isProgress -> mVerificationInProgress = isProgress);

        verifyPhoneNumberActivityVM.getStateVisibility().observe(this, state -> {
            switch (state) {
                case STATE_INITIALIZED:
                    break;
                case STATE_CODE_SENT:
//                    binding.tvSubmit.setText(getString(R.string.verify_otp));
//                    binding.llOtp.setVisibility(View.VISIBLE);
//                    disableViews(binding.etMobile, binding.ccp);
                    setProgress(false);
                    setProgressResendCode(false);
                    break;
                case STATE_VERIFY_FAILED:
//                    binding.tvSubmit.setText(getString(R.string.send_verification_code));
//                    binding.llOtp.setVisibility(View.GONE);
//                    disableViews(binding.etMobile, binding.ccp);
                    toastMessage(getString(R.string.verification_failed));
                    setProgress(false);
                    break;
            }
        });

        verifyPhoneNumberActivityVM.getIsShowProgress().observe(this, this::setProgress);

        verifyPhoneNumberActivityVM.startPhoneNumberVerification(prefix + phone, mCallbacks, mAuth);

        verifyPhoneNumberActivityVM.getVerifyOtpSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
//                verifyPhoneNumberActivityVM.register(uname, pass, email, phone, prefix);
                verifyPhoneNumberActivityVM.verifyPhoneNumber();
            }
        });
    }

    private void setProgress(Boolean isShow) {
        disableEnableTouch(isShow);
        if (isShow) {
            binding.btnSubmit.setVisibility(View.INVISIBLE);
            binding.progressBarSignup.setVisibility(View.VISIBLE);
        } else {
            binding.btnSubmit.setVisibility(View.VISIBLE);
            binding.progressBarSignup.setVisibility(View.GONE);
        }
    }

    private void setProgressResendCode(Boolean isShow) {
//        disableEnableTouch(isShow);
//        if (isShow) {
//            binding.tvResendCode.setVisibility(View.GONE);
//            binding.progressBarResend.setVisibility(View.VISIBLE);
//        } else {
//            binding.tvResendCode.setVisibility(View.VISIBLE);
//            binding.progressBarResend.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVerificationInProgress && verifyPhoneNumberActivityVM != null) {
            verifyPhoneNumberActivityVM.startPhoneNumberVerification(prefix + phone, mCallbacks, mAuth);
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
        gotoMainActivity(Constants.TAB_HOME);
    }

//    private String getMobile() {
//        String mobile = binding.etMobile.getText().toString().trim();
//        return mobile.replace(" ", "");
//    }

//    private String getMobilePrefix() {
//        return binding.ccp.getSelectedCountryCodeWithPlus();
//    }

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
