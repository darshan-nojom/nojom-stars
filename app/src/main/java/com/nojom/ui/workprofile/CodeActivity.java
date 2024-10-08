package com.nojom.ui.workprofile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nojom.R;
import com.nojom.databinding.ActivityCodeBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.LoginSignUpVM;
import com.nojom.ui.auth.ResetPasswordActivity;
import com.nojom.util.Constants;

import java.util.Locale;

public class CodeActivity extends BaseActivity {

    private CodeActivityVM verifyPhoneNumberActivityVM;
    private ActivityCodeBinding binding;
    static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";


    //    private static final int STATE_INITIALIZED = 1;
//    private static final int STATE_VERIFY_SUCCESS = 4;
//    private static final int STATE_CODE_SENT = 2;
//    private static final int STATE_VERIFY_FAILED = 3;
//
//
//    private FirebaseAuth mAuth;
//
    boolean mVerificationInProgress = false;
    private String mVerificationId;
//    private PhoneAuthProvider.ForceResendingToken mResendToken;
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String phone, prefix, pass, email;
    public boolean isFromForget;
    private CountDownTimer countDownTimer;
    private LoginSignUpVM activityViewModel;
    public static String jwtToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_code);
        verifyPhoneNumberActivityVM = ViewModelProviders.of(this).get(CodeActivityVM.class);
        verifyPhoneNumberActivityVM.init(this);
        activityViewModel = new LoginSignUpVM(this);

        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.txtNum, Constants.FONT_AR_REGULAR);
            setArFont(binding.pinview, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtResend, Constants.FONT_AR_MEDIUM);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
        }

        phone = getIntent().getStringExtra("mobile");
        prefix = getIntent().getStringExtra("prefix");
        pass = getIntent().getStringExtra("pass");
        email = getIntent().getStringExtra("email");
        isFromForget = getIntent().getBooleanExtra("isFromForget", false);
        verifyPhoneNumberActivityVM.setFromForget(isFromForget);
        initData();
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        if (!TextUtils.isEmpty(phone)) {
            binding.txtNum.setText(getString(R.string.your_code_was_sent_to) + " " + prefix + " " + phone);
        } else if (!TextUtils.isEmpty(email)) {
            binding.txtNum.setText(getString(R.string.your_code_was_sent_to) + " " + email);
        }
        binding.pinview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (i == 5 && !TextUtils.isEmpty(charSequence) && charSequence.length() == 6) {
                    DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(CodeActivity.this, R.color.black));
                    binding.btnLogin.setTextColor(getResources().getColor(R.color.white));
                } else {
                    DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(CodeActivity.this, R.color.C_E5E5EA));
                    binding.btnLogin.setTextColor(getResources().getColor(R.color.c_AAAAAC));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        verifyPhoneNumberActivityVM.verifyEmailOtpSuccess.observe(this, aBoolean -> {
            if (aBoolean) {
                Intent intent = new Intent(this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("phone", prefix + phone);
                String otp = binding.pinview.getText().toString();
                intent.putExtra("otp", otp);
                intent.putExtra("token", jwtToken);
                startActivity(intent);
                setProgress(false);
                countDownTimer.cancel();
                jwtToken = "";
                finish();
            }
        });
        verifyPhoneNumberActivityVM.verifyPhoneOtpSuccess.observe(this, aBoolean -> {
            if (aBoolean) {
                verifyPhoneNumberActivityVM.onCreateAccount(pass, email, getToken(), prefix, phone);
            }
        });
        verifyPhoneNumberActivityVM.codeWrong.observe(this, aBoolean -> {
            if (aBoolean) {//wrong OTP
                binding.txtErr.setVisibility(View.VISIBLE);
                binding.pinview.setText("");
            } else {//correct OTP
                binding.txtErr.setVisibility(View.GONE);
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.pinview, 0);
        binding.pinview.requestFocus();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        //binding.txtPhone.setText(prefix + " " + phone);

        binding.relLogin.setOnClickListener(v -> {
            try {
                String otp = binding.pinview.getText().toString();
                if (isEmpty(otp)) {
                    toastMessage(getString(R.string.please_enter_otp));
                    return;
                }
                if (otp.length() != 6) {
                    //toastMessage(getString(R.string.please_enter_otp));
                    return;
                }
                setProgress(true);
                binding.txtErr.setVisibility(View.GONE);
                if (isFromForget) {
                    //redirectActivity(ResetPasswordActivity.class);
                    /*if (!TextUtils.isEmpty(phone)) {//phone case
                        verifyPhoneNumberActivityVM.verifyPhoneNumberWithCode(mVerificationId, otp, mAuth, pass, email, getToken(), phone, prefix);
                    } else {//email case
                        verifyPhoneNumberActivityVM.verifyEmailOtp(email, otp);
                    }*/
                    if (!TextUtils.isEmpty(email)) {
                        verifyPhoneNumberActivityVM.verifyEmailOtp(email, otp, "");
                    } else if (!TextUtils.isEmpty(phone)) {
                        verifyPhoneNumberActivityVM.verifyEmailOtp("", otp, prefix + "." + phone);
                    }

                } else {//
                    verifyPhoneNumberActivityVM.verifyPhoneOtp(prefix + "." + phone, otp);
                }
            } catch (Exception e) {
                setProgress(false);
                e.printStackTrace();
            }
        });
        binding.txtResend.setOnClickListener(v -> {
            if (binding.txtResend.getText().toString().equals(getString(R.string.resend_code))) {
                setProgressResendCode(true);
                if (!isFromForget) {
                    if (!TextUtils.isEmpty(phone)) {
//                        verifyPhoneNumberActivityVM.resendVerificationCode(mResendToken, prefix + phone, mCallbacks);
                        activityViewModel.sendOtpPhone(prefix + "." + phone);
                    } else {
                        if (isFromForget) {//resend code API for mail
                            activityViewModel.forgotPassword(email);
                        }
                    }
                } else {//from forget pass screen
                    //send code API
                    if (!TextUtils.isEmpty(email)) {
                        activityViewModel.sendOTP(email, "");
                    } else if (!TextUtils.isEmpty(phone)) {
                        activityViewModel.sendOTP("", prefix + "." + phone);
                    }
                }
                countDownTimer.start();
            }
        });

//        mAuth = FirebaseAuth.getInstance();

        /*mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                mVerificationInProgress = false;
                *//*verifyPhoneNumberActivityVM.updateUI(STATE_VERIFY_SUCCESS);
                if (isFromForget) {
                    if (!TextUtils.isEmpty(phone)) {
                        setProgress(false);
                        countDownTimer.cancel();
//                        redirectActivity(ResetPasswordActivity.class);
                        Intent intent = new Intent(CodeActivity.this, ResetPasswordActivity.class);
                        intent.putExtra("phone", prefix + "." + phone);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    verifyPhoneNumberActivityVM.onCreateAccount(pass, email, getToken(), prefix, phone);
                }*//*
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
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                hideProgress();
                mVerificationId = verificationId;
                mResendToken = token;

                verifyPhoneNumberActivityVM.updateUI(STATE_CODE_SENT);
            }
        };*/

        verifyPhoneNumberActivityVM.getVerificationInProgress().observe(this, isProgress -> mVerificationInProgress = isProgress);

//        verifyPhoneNumberActivityVM.getStateVisibility().observe(this, state -> {
//            switch (state) {
//                case STATE_INITIALIZED:
//                    break;
//                case STATE_CODE_SENT:
////                    binding.tvSubmit.setText(getString(R.string.verify_otp));
////                    binding.llOtp.setVisibility(View.VISIBLE);
////                    disableViews(binding.etMobile, binding.ccp);
//                    setProgress(false);
//                    setProgressResendCode(false);
//                    break;
//                case STATE_VERIFY_FAILED:
////                    binding.tvSubmit.setText(getString(R.string.send_verification_code));
////                    binding.llOtp.setVisibility(View.GONE);
////                    disableViews(binding.etMobile, binding.ccp);
//                    toastMessage(getString(R.string.verification_failed));
//                    setProgress(false);
//                    break;
//            }
//        });

        verifyPhoneNumberActivityVM.getIsShowProgress().observe(this, this::setProgress);

//        if (!TextUtils.isEmpty(phone) && !isFromForget) {
//            verifyPhoneNumberActivityVM.startPhoneNumberVerification(prefix + phone, mCallbacks, mAuth);
//        }


        verifyPhoneNumberActivityVM.getVerifyOtpSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
//                verifyPhoneNumberActivityVM.register(uname, pass, email, phone, prefix);
                verifyPhoneNumberActivityVM.verifyPhoneNumber();
            }
        });

        countDownTimer = new CountDownTimer(50000, 1000) { // 50,000 milliseconds = 50 seconds

            public void onTick(long millisUntilFinished) {
                // Update the timer text view every second
                binding.txtResend.setText(String.format("%s", getString(R.string.re_send_again_in)));
                binding.txtResend.setTextColor(getResources().getColor(R.color.C_3C3C43));
                binding.txtTime.setText(String.format("%d%s", millisUntilFinished / 1000, getString(R.string.s)));
            }

            public void onFinish() {
                // When the timer finishes
                binding.txtResend.setText(getString(R.string.resend_code));
                binding.txtResend.setTextColor(getResources().getColor(R.color.C_020814));
                binding.txtTime.setText("");
            }
        };

        // Start the timer
        countDownTimer.start();
    }

    private void setProgress(Boolean isShow) {
        disableEnableTouch(isShow);
        if (isShow) {
            binding.btnLogin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnLogin.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
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
//        if (mVerificationInProgress && verifyPhoneNumberActivityVM != null) {
//            if (!TextUtils.isEmpty(phone) && !isFromForget) {
//                verifyPhoneNumberActivityVM.startPhoneNumberVerification(prefix + phone, mCallbacks, mAuth);
//            }
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //gotoMainActivity(Constants.TAB_HOME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer if the activity is destroyed
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
