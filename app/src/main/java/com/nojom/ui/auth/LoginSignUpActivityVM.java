package com.nojom.ui.auth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.edittext.EditTextSFTextRegular;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityLoginSignUpBinding;
import com.nojom.model.APIResponse;
import com.nojom.model.ProfileResponse;
import com.nojom.model.UserModel;
import com.nojom.model.requestmodel.AuthenticationRequest;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.AESHelper;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

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

import static android.text.TextUtils.isEmpty;
import static com.nojom.util.Constants.API_FORGET_PASSWORD;
import static com.nojom.util.Constants.API_GET_PROFILE;
import static com.nojom.util.Constants.API_LOGIN;
import static com.nojom.util.Constants.API_RESET_PASSWORD;

public class LoginSignUpActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private LoginSignUpActivity.LayoutBinderHelper layoutBinderHelper;
    private Dialog progressDialog;
    private boolean isResend;
    private Button btn;
    private TextView textView;
    private ProgressBar progressBar;
    private String email;
    private boolean isSignUp;

    public void setLayoutBinderHelper(LoginSignUpActivity.LayoutBinderHelper layoutBinderHelper) {
        this.layoutBinderHelper = layoutBinderHelper;
    }

    private LoginInteractor loginInteractor;

    public void setLoginInteractor(LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
    }

    LoginSignUpActivityVM(BaseActivity loginSignUpActivity) {
        this.activity = loginSignUpActivity;
    }


    void loginSignup(String url, AuthenticationRequest authenticationRequest, boolean isSignUp, boolean isSocialLogin) {
        if (!activity.isNetworkConnected())
            return;

        if (!isSocialLogin) {
            layoutBinderHelper.setIsLoading(true);
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
                        } else {
                            activity.failureError(apiResponse.msg);
//                                activity.hideProgress();
                            if (!isSocialLogin) {
                                layoutBinderHelper.setIsLoading(false);
                            } else {
                                layoutBinderHelper.setIsLoadingGoogle(false);
                                layoutBinderHelper.setIsLoadingFb(false);
                            }
                        }
                        if (isSignUp) {
                            Utils.trackFirebaseEvent(activity, "User_Signup_Success");
                        } else {
                            Utils.trackFirebaseEvent(activity, "User_Login_Success");
                        }

                    } else {
                        activity.toastMessage(TextUtils.isEmpty(apiResponse.getMessage(activity.language)) ? "Something went wrong" : apiResponse.getMessage(activity.language));
//                        activity.hideProgress();
                        if (!isSocialLogin) {
                            layoutBinderHelper.setIsLoading(false);
                        } else {
                            layoutBinderHelper.setIsLoadingGoogle(false);
                            layoutBinderHelper.setIsLoadingFb(false);
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
                    layoutBinderHelper.setIsLoading(false);
                } else {
                    layoutBinderHelper.setIsLoadingGoogle(false);
                    layoutBinderHelper.setIsLoadingFb(false);
                }
            }
        });
    }

    private void saveData(String jwtToken, String data, boolean isSignUp) {
        this.isSignUp = isSignUp;
        UserModel userModel = null;//get logged in user data from JWT token
        try {
            userModel = new Gson().fromJson(AESHelper.decrypt(data), UserModel.class);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
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
                if (!accounts.containsKey(userModel.username)) {
                    Preferences.addMultipleAccounts(activity, jwtToken, userModel.username);
                }
            } else {
                Preferences.addMultipleAccounts(activity, jwtToken, userModel.username);
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

    boolean validLoginData(LoginSignUpActivity.LayoutBinderHelper layoutBinderHelper, ActivityLoginSignUpBinding binding) {
        if (activity.isEmpty(layoutBinderHelper.getShow_login() ? Objects.requireNonNull(binding.etUsername.getText()).toString() : Objects.requireNonNull(binding.etSUsername.getText()).toString())) {
            activity.validationError(activity.getString(R.string.enter_valid_email));
            return false;
        }

        if (isEmpty(layoutBinderHelper.getShow_login() ? Objects.requireNonNull(binding.etPassword.getText()).toString() : Objects.requireNonNull(binding.etSPassword.getText()).toString())) {
            activity.validationError(activity.getString(R.string.enter_password));
            return false;
        }

//        if (binding.etPassword.getText().length() < 8) {
//            activity.validationError(activity.getString(R.string.minimum_length_is_eight_characters));
//            return false;
//        }

        return true;
    }

    boolean validSignUpData(LoginSignUpActivity.LayoutBinderHelper layoutBinderHelper, ActivityLoginSignUpBinding binding, String email) {

        if (!activity.isValidEmail(email)) {
            activity.validationError(activity.getString(R.string.enter_valid_email));
            return false;
        }

//        if (activity.isEmpty(layoutBinderHelper.getShow_login() ? Objects.requireNonNull(binding.etUsername.getText()).toString() : Objects.requireNonNull(binding.etSUsername.getText()).toString())) {
//            activity.validationError(activity.getString(R.string.enter_username));
//            return false;
//        }

        if (activity.isEmpty(layoutBinderHelper.getShow_login() ? Objects.requireNonNull(binding.etPassword.getText()).toString() : Objects.requireNonNull(binding.etSPassword.getText()).toString())) {
            activity.validationError(activity.getString(R.string.enter_password));
            return false;
        }

//        if (binding.etSPassword.getText().length() < 8) {
//            activity.validationError(activity.getString(R.string.minimum_length_is_eight_characters));
//            return false;
//        }

        return true;
    }

    void showForgotPasswordDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.setCancelable(true);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnReset = dialog.findViewById(R.id.btn_reset);
        final EditText etEmail = dialog.findViewById(R.id.et_email);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnReset.setOnClickListener(v -> {
            if (activity.isValidEmail(etEmail.getText().toString())) {
                Utils.hideSoftKeyboard(activity);
                btnReset.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                forgotPassword(etEmail.getText().toString(), false, btnReset, progressBar, dialog, null);

            } else {
                activity.toastMessage(activity.getString(R.string.enter_valid_email));
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void forgotPassword(final String email, final boolean isResend, Button btn, ProgressBar progressBar, Dialog dialog, TextView textView) {
        if (!activity.isNetworkConnected())
            return;
        progressDialog = dialog;

        this.progressBar = progressBar;
        this.textView = textView;
        this.btn = btn;
        this.email = email;

        activity.disableEnableTouch(true);

        CommonRequest.ForgetPassword forgetPassword = new CommonRequest.ForgetPassword();
        forgetPassword.setEmail(email);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_FORGET_PASSWORD, forgetPassword.toString(), true, this);
    }

    private void showSecurityCodeDialog(final String email) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_security_code);
        dialog.setCancelable(false);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnReset = dialog.findViewById(R.id.btn_reset);
        final EditText etSecurityCode = dialog.findViewById(R.id.et_security_code);
        final EditText etNewPassword = dialog.findViewById(R.id.et_new_password);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        final ProgressBar progressBarResend = dialog.findViewById(R.id.progress_bar_resend);
        TextView tvResendCode = dialog.findViewById(R.id.tv_resend_code);
        tvResendCode.setPaintFlags(tvResendCode.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvResendCode.setOnClickListener(v -> {
            progressBarResend.setVisibility(View.VISIBLE);
            tvResendCode.setVisibility(View.GONE);
            forgotPassword(email, true, btnReset, progressBarResend, dialog, tvResendCode);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnReset.setOnClickListener(v -> {
            if (isEmpty(etSecurityCode.getText().toString())) {
                activity.validationError(activity.getString(R.string.enter_code));
                return;
            }

            if (isEmpty(etNewPassword.getText().toString())) {
                activity.validationError(activity.getString(R.string.please_enter_password));
                return;
            }

            Utils.hideSoftKeyboard(activity);
            progressBar.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.GONE);
            resetPassword(email, etSecurityCode.getText().toString(), etNewPassword.getText().toString(), dialog, progressBar, btnReset);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void resetPassword(String email, String otp, String password, final Dialog dialog, ProgressBar progressBar, Button btnReset) {
        if (!activity.isNetworkConnected())
            return;
        this.progressDialog = dialog;
        this.btn = btnReset;

        activity.disableEnableTouch(true);

        CommonRequest.ResetPassword resetPassword = new CommonRequest.ResetPassword();
        resetPassword.setEmail(email);
        resetPassword.setOtp(otp);
        resetPassword.setPassword(password);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_RESET_PASSWORD, resetPassword.toString(), true, this);
    }

    void getGraphRequest(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                (object, response) -> {
                    try {
                        if (object != null) {
                            String id = object.getString("id");
                            String name = "", first_name = "", last_name = "", email = "";
                            if (object.has("name")) {
                                name = object.getString("name");
                            }
                            if (object.has("first_name")) {
                                first_name = object.getString("first_name");
                            }
                            if (object.has("last_name")) {
                                last_name = object.getString("last_name");
                            }
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
                            Log.e("Fb Response", object.toString());

//                            socialLogin(id, "", name, first_name, last_name, email);
//                            String username = "agent_" + new Random().nextInt(10000);
                            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
                            authenticationRequest.setUsername("");
                            authenticationRequest.setEmail(email);
                            authenticationRequest.setDevice_token(activity.getToken());
                            authenticationRequest.setFacebook_id(id);
                            authenticationRequest.setFirst_name(first_name);
                            authenticationRequest.setLast_name(last_name);
                            authenticationRequest.setDevice_type(1);

                            loginSignup(API_LOGIN, authenticationRequest, false, true);

                            Utils.trackFirebaseEvent(activity, "Login_With_Facebook_Success");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    void showHidePassword(EditTextSFTextRegular etPassword, ImageView imgPassword, LoginSignUpActivity.LayoutBinderHelper layoutBinderHelper) {
        if (imgPassword.getTag() != null && imgPassword.getTag().equals("1")) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imgPassword.setImageResource(R.drawable.show_password);
            imgPassword.setTag("0");
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imgPassword.setImageResource(R.drawable.hide_password);
            imgPassword.setTag("1");
        }
        etPassword.setSelection(layoutBinderHelper.getShow_login() ? Objects.requireNonNull(etPassword.getText()).toString().length() : Objects.requireNonNull(etPassword.getText()).toString().length());
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_FORGET_PASSWORD)) {
            activity.toastMessage(msg);
            if (!isResend) {
                progressDialog.dismiss();
                showSecurityCodeDialog(email);
            } else {
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        } else if (urlEndPoint.equalsIgnoreCase(API_RESET_PASSWORD)) {
            activity.toastMessage(msg);
            progressDialog.dismiss();
        } else if (urlEndPoint.equalsIgnoreCase(API_GET_PROFILE)) {
            ProfileResponse profileObject = ProfileResponse.getProfileObject(decryptedData);
            if (profileObject != null) {
                Preferences.setProfileData(activity, profileObject);
            }
            activity.connectSocket(activity);
//            Task24Application.getActivity().clientNew=null;
//            activity.start();

            if (loginInteractor != null) {
                loginInteractor.redirect(isSignUp);
            }

        }
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_FORGET_PASSWORD)) {
            btn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();
        } else if (urlEndPoint.equalsIgnoreCase(API_RESET_PASSWORD)) {
            activity.failureError(activity.getString(R.string.reset_password_failed));
            btn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else if (urlEndPoint.equalsIgnoreCase(API_GET_PROFILE)) {
            if (loginInteractor != null) {
                loginInteractor.redirect(isSignUp);
            }
        }
        activity.disableEnableTouch(false);
    }
}
