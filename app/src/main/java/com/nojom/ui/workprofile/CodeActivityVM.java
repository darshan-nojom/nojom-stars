package com.nojom.ui.workprofile;

import static com.nojom.ui.workprofile.CodeActivity.jwtToken;
import static com.nojom.util.Constants.API_ADD_PROFILE_VERIF;
import static com.nojom.util.Constants.API_GET_PROFILE;
import static com.nojom.util.Constants.API_SIGNUP;
import static com.nojom.util.Constants.API_VERIFY_EMAIL_OTP;
import static com.nojom.util.Constants.API_VERIFY_OTP_PHONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.APIResponse;
import com.nojom.model.UserModel;
import com.nojom.model.requestmodel.AuthenticationRequest;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.ResetPasswordActivity;
import com.nojom.util.AESHelper;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;

    private MutableLiveData<Boolean> verifyOtpSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> verifyEmailOtpSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> verifyPhoneOtpSuccess = new MutableLiveData<>();
    private MutableLiveData<Integer> stateVisibility = new MutableLiveData<>();
    private MutableLiveData<Boolean> verificationInProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    public MutableLiveData<Boolean> codeWrong = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Integer> getStateVisibility() {
        return stateVisibility;
    }

    public MutableLiveData<Boolean> getVerificationInProgress() {
        return verificationInProgress;
    }

    public MutableLiveData<Boolean> getVerifyOtpSuccess() {
        return verifyOtpSuccess;
    }

//    private FirebaseAuth mAuth;
    private boolean isFromForget;

    public void setFromForget(boolean fromForget) {
        isFromForget = fromForget;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
//        this.mAuth = mAuth;
    }

    void startPhoneNumberVerification(String code, PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks, FirebaseAuth mAuth) {
//        getIsShowProgress().postValue(true);
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

    void verifyPhoneNumberWithCode(String verificationId, String code, FirebaseAuth mAuth, String pass, String email, String tok, String pho, String pref) {
        getIsShowProgress().postValue(true);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        codeWrong.postValue(false);
                        //verifyPhoneNumber();
                        if (isFromForget) {
                            if (!TextUtils.isEmpty(pho)) {
                                activity.redirectActivity(ResetPasswordActivity.class);
                            }
                        } else {
                            onCreateAccount(pass, email, tok, pref, pho);
                        }
                    } else {
                        codeWrong.postValue(true);
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

    void verifyEmailOtp(String email, String otp, String phone) {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        CommonRequest.EmailOtp profileVerification = new CommonRequest.EmailOtp();
        if (!TextUtils.isEmpty(email)) {
            profileVerification.setEmail(email);
        }
        if (!TextUtils.isEmpty(phone)) {
            profileVerification.setPhone(phone);
        }
        profileVerification.setOtp(otp);
//        profileVerification.setMawthooq_number("");

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest("", activity, API_VERIFY_EMAIL_OTP, profileVerification.toString(), true, this);

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

    public void onCreateAccount(String pass, String email, String token, String pref, String mobile) {
        getIsShowProgress().postValue(true);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
//            authenticationRequest.setUsername(binding.etSUsername.getText().toString());
        authenticationRequest.setPassword(pass);
        authenticationRequest.setEmail(email);
        authenticationRequest.setDevice_token(token);
        authenticationRequest.setDevice_type(1);
//        String cCode = binding.ccp.getSelectedCountryCodeWithPlus();
        authenticationRequest.setPhone(mobile);
        authenticationRequest.setPreFix(pref);
        loginSignup(API_SIGNUP, authenticationRequest, true, false);
    }

    public void verifyPhoneOtp(String mobile, String otp) {
        getIsShowProgress().postValue(true);
        CommonRequest.PhoneOtp profileVerification = new CommonRequest.PhoneOtp();
        profileVerification.setOtp(otp);
        profileVerification.setPhone(mobile);
        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest("", activity, API_VERIFY_OTP_PHONE, profileVerification.toString(), true, this);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
//        activity.hideProgress();
        if (urlEndPoint.equals(API_ADD_PROFILE_VERIF)) {
            if (isFromForget) {
                activity.redirectActivity(ResetPasswordActivity.class);
            } else {
                activity.redirectActivity(NewNameActivity.class);
            }
//            activity.redirectActivity(ProfileVerificationActivity.class);
            activity.finish();
        } else if (urlEndPoint.equals(API_VERIFY_EMAIL_OTP)) {

            jwtToken = decryptedData;
            verifyEmailOtpSuccess.postValue(true);
            codeWrong.postValue(false);
        } else if (urlEndPoint.equals(API_VERIFY_OTP_PHONE)) {

            verifyPhoneOtpSuccess.postValue(true);
            codeWrong.postValue(false);
        }
        activity.disableEnableTouch(false);
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
//        activity.hideProgress();
        getIsShowProgress().postValue(false);
        activity.disableEnableTouch(false);
        getVerifyOtpSuccess().postValue(false);
        if (urlEndPoint.equals(API_VERIFY_EMAIL_OTP) || urlEndPoint.equals(API_VERIFY_OTP_PHONE)) {
            codeWrong.postValue(true);
        } else {
            activity.toastMessage(message);
        }
    }

    void loginSignup(String url, AuthenticationRequest authenticationRequest, boolean isSignUp, boolean isSocialLogin) {
        if (!activity.isNetworkConnected())
            return;

        if (!isSocialLogin) {
//            layoutBinderHelper.setIsLoading(true);
            getIsShowProgress().postValue(true);
        }

        Log.e("Request ", "Encrypted Data----- " + authenticationRequest.toString());
        RequestBody body = RequestBody.create(authenticationRequest.toString(), MultipartBody.FORM);

        Call<APIResponse> call = activity.getService().requestAPI(url, body);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                APIResponse apiResponse = response.body();
                if (apiResponse != null) {
                    if (apiResponse.status) {
                        //                            String decryptedText = AESHelper.decrypt(apiResponse.data);
//                            Log.e("Response ", "Plain Data----- " + decryptedText);

                        Preferences.writeBoolean(activity, Constants.IS_SOCIAL_LOGIN, isSocialLogin);

                        if (apiResponse.jwt != null && !activity.isEmpty(apiResponse.jwt)) {
//                                firebaseSync(mAuth, apiResponse.jwt, apiResponse.data, isSignUp);
                            saveData(apiResponse.jwt, apiResponse.data, isSignUp);
                            getVerifyOtpSuccess().postValue(true);
                        } else {
                            activity.failureError(apiResponse.msg);
//                                activity.hideProgress();
                            if (!isSocialLogin) {
//                                layoutBinderHelper.setIsLoading(false);
                                getIsShowProgress().postValue(false);
                            }
                        }
                        if (isSignUp) {
                            Utils.trackFirebaseEvent(activity, "User_Signup_Success");
                        } else {
                            Utils.trackFirebaseEvent(activity, "User_Login_Success");
                        }

                    } else {
                        activity.toastMessage(TextUtils.isEmpty(apiResponse.getMessage(activity.language)) ? activity.getString(R.string.something_went_wrong) : apiResponse.getMessage(activity.language));
//                        activity.hideProgress();
                        if (!isSocialLogin) {
//                            layoutBinderHelper.setIsLoading(false);
                            getIsShowProgress().postValue(false);
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                if (isSignUp) {
                    Utils.trackFirebaseEvent(activity, "Signup_Failed");
                    activity.failureError(activity.getString(R.string.sigup_failed));
                } else {
                    Utils.trackFirebaseEvent(activity, "Login_Failed");
                    activity.failureError(activity.getString(R.string.login_failed));
                }
                if (!isSocialLogin) {
//                    layoutBinderHelper.setIsLoading(false);
                    getIsShowProgress().postValue(false);
                }
            }
        });
    }

    private void saveData(String jwtToken, String data, boolean isSignUp) {
//        this.isSignUp = isSignUp;
        UserModel userModel = null;//get logged in user data from JWT token
        try {
            userModel = new Gson().fromJson(AESHelper.decrypt(data), UserModel.class);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        if (userModel != null) {
            if (userModel.profileType != null && userModel.profileType.id != activity.getProfileTypeId()) {
                if (userModel.profileType.id == Constants.CLIENT_PROFILE) {
                    activity.failureError(activity.getString(R.string.you_are_trying_to_login_with_client_profile));
                } else {
                    activity.failureError(activity.getString(R.string.you_are_trying_to_login_with_agent_profile));
                }
                return;
            }
            Preferences.writeBoolean(activity, Constants.IS_LOGIN, true);
            userModel.jwt = jwtToken;
            Preferences.saveUserData(activity, userModel);
            Preferences.writeString(activity, Constants.JWT, jwtToken);
            Preferences.writeInteger(activity, Constants.FILTER_ID, 0);

            HashMap<String, String> accounts = Preferences.getMultipleAccounts(activity);
            if (accounts != null && accounts.size() > 0) {
//                if (!accounts.containsKey(userModel.username)) {
                Preferences.addMultipleAccounts(activity, jwtToken, userModel.email);
//                }
            } else {
                Preferences.addMultipleAccounts(activity, jwtToken, userModel.email);
            }

            try {
                Intercom.client().setUserHash(generateHMACKey(String.valueOf(userModel.id)));
                Intercom.client().registerIdentifiedUser(Registration.create().withUserId(String.valueOf(userModel.id)));

                //Update Device Token to firebase
                if (!activity.isEmpty(activity.getToken())) {
                    APIRequest apiRequest = new APIRequest();//after login call profile API and onSuccess redirection to home screen
                    apiRequest.makeAPIRequest(activity, API_GET_PROFILE, null, false, this);

                } else {
                    getFirebaseToken(isSignUp);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getFirebaseToken(boolean isSignUp) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("getInstanceId failed", task.getException() + "");
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    if (token != null) {
                        Preferences.writeString(activity, Constants.FCM_TOKEN, token);
                        Log.d("TTT", "token..... " + token);
                    }

                    APIRequest apiRequest = new APIRequest();//after login call profile API and onSuccess redirection to home screen
                    apiRequest.makeAPIRequest(activity, API_GET_PROFILE, null, false, this);
                });
    }

    private String generateHMACKey(String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec("sh_B0K7q5gnhvzj46rrYIZ_abGkTrP1cYjXydg09".getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = (sha256_HMAC.doFinal(message.getBytes()));
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            Log.e("HMAC Key", result.toString());
            return result.toString();
        } catch (Exception e) {
            System.out.println("Error");
        }
        return "";
    }
}
