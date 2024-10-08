package com.nojom.ui.workprofile;

import static android.app.Activity.RESULT_OK;
import static com.nojom.util.Constants.API_ADD_PROFILE_VERIF;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumberActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;

    private MutableLiveData<Integer> stateVisibility = new MutableLiveData<>();
    private MutableLiveData<Boolean> verificationInProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Integer> getStateVisibility() {
        return stateVisibility;
    }

    public MutableLiveData<Boolean> getVerificationInProgress() {
        return verificationInProgress;
    }

    private FirebaseAuth mAuth;

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    void startPhoneNumberVerification(String code, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks, FirebaseAuth mAuth) {
        getIsShowProgress().postValue(true);
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                code,
//                60,
//                TimeUnit.SECONDS,
//                activity,
//                mCallbacks);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(code)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


        getVerificationInProgress().postValue(true);
    }

    void verifyPhoneNumberWithCode(String verificationId, String code, FirebaseAuth mAuth) {
        getIsShowProgress().postValue(true);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        verifyPhoneNumber();
                    } else {
                        // Sign in failed, display a message and update the UI
                        task.getException();// The verification code entered was invalid
                        getIsShowProgress().postValue(false);
                    }
                });
    }

    void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token, String s, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks) {
//        activity.showProgress();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                s,
                60,
                TimeUnit.SECONDS,
                activity,
                mCallbacks,
                token);
    }


    void verifyPhoneNumber() {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        CommonRequest.ProfileVerification profileVerification = new CommonRequest.ProfileVerification();
        profileVerification.setType("2");
//        profileVerification.setMawthooq_number("");

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_PROFILE_VERIF, profileVerification.toString(), true, this);

    }

    boolean isValid(boolean validFullNumber) {
        if (!validFullNumber) {
            activity.validationError(activity.getString(R.string.valid_mobile_no));
            return false;
        }
        return true;
    }

    void updateUI(int uiState) {
        switch (uiState) {
            case STATE_INITIALIZED:
                getStateVisibility().postValue(STATE_INITIALIZED);
                break;
            case STATE_CODE_SENT:
                getStateVisibility().postValue(STATE_CODE_SENT);
                break;
            case STATE_VERIFY_FAILED:
                getStateVisibility().postValue(STATE_VERIFY_FAILED);
                break;
        }
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
//        activity.hideProgress();
        activity.setResult(RESULT_OK);
        activity.finish();
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
//        activity.hideProgress();
    }
}
