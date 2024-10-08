package com.nojom.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.nojom.BR;
import com.nojom.R;
import com.nojom.api.ApiClient;
import com.nojom.databinding.ActivityLoginSignUpBinding;
import com.nojom.model.requestmodel.AuthenticationRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.OtpActivity;
import com.nojom.util.Constants;
import com.nojom.util.SegmentedButtonGroupNew;
import com.nojom.util.Utils;

import java.util.Arrays;
import java.util.Objects;

public class LoginSignUpActivity extends BaseActivity implements LoginInteractor {

    private ActivityLoginSignUpBinding binding;
    private LoginSignUpActivityVM activityViewModel;
    private LayoutBinderHelper layoutBinderHelper;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private int RC_SIGN_IN = 9001;
    private boolean isLoginForm = false;
    private boolean isNeedToFinish = false;
    private static final int LOGIN = 1;
    private static final int SIGNUP = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_sign_up);

        layoutBinderHelper = new LayoutBinderHelper();
        activityViewModel = new LoginSignUpActivityVM(this);
        activityViewModel.setLayoutBinderHelper(layoutBinderHelper);

        binding.setLoginModel(activityViewModel);
        binding.setActivity(this);
        binding.setLayoutBinder(layoutBinderHelper);
        activityViewModel.setLoginInteractor(this);

        initData();
    }

    private void initData() {
        initFacebook();
        // For Google Login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Configure Google Sign-In
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("780634167419-8fg49f4n4b9i5rgdjoo9t5k44e1aqdh4.apps.googleusercontent.com")
//                .requestEmail()
//                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if (getIntent() != null) {
            isLoginForm = getIntent().getBooleanExtra(Constants.FROM_LOGIN, false);
            isNeedToFinish = getIntent().getBooleanExtra(Constants.LOGIN_FINISH, false);
        }

        if (isLoginForm) {
            layoutBinderHelper.setLoginView(true);
            Utils.trackFirebaseEvent(this, "Login_Screen");
        } else {
            layoutBinderHelper.setLoginView(false);
            Utils.trackFirebaseEvent(this, "Signup_Screen");
        }

        binding.segmentLoginGroup.setOnPositionChangedListener(onPositionChangedListener);
        binding.segmentLoginGroup.setPosition(1, false);

        //this observer used when server return 502 error code or any wrong with response
        ApiClient.getMutableServerError().observe(LoginSignUpActivity.this, integer -> {
            if (!binding.progressBarGoogle.isShown() || !binding.progressBarFb.isShown()) {
                layoutBinderHelper.setIsLoading(false);
            } else {
                layoutBinderHelper.setIsLoadingGoogle(false);
                layoutBinderHelper.setIsLoadingFb(false);
            }
        });

        binding.ccp.registerCarrierNumberEditText(binding.etMobile);
        binding.ccp.setOnCountryChangeListener(() -> {
            binding.etMobile.setText("");
            binding.txtPrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());
            binding.ccp.setTag(binding.ccp.getSelectedCountryCodeWithPlus());
        });
        binding.txtPrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());
    }

    public String getEmail() {
        return Objects.requireNonNull(binding.etEmail.getText()).toString();
    }

    public String getMobile() {
        return Objects.requireNonNull(binding.etMobile.getText()).toString();
    }

    private void initFacebook() {
        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().logInWithReadPermissions(LoginSignUpActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                activityViewModel.getGraphRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                layoutBinderHelper.setIsLoadingFb(false);
            }

            @Override
            public void onError(FacebookException e) {
                if (e instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
                if (!isEmpty(e.getMessage()))
                    Log.e("LoginActivity1", Objects.requireNonNull(e.getMessage()));
                layoutBinderHelper.setIsLoadingFb(false);
                Utils.trackFirebaseEvent(LoginSignUpActivity.this, "Login_With_Facebook_Error");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public SegmentedButtonGroupNew.OnPositionChangedListener onPositionChangedListener = new SegmentedButtonGroupNew.OnPositionChangedListener() {
        @Override
        public void onPositionChanged(int position) {
            if (position == LOGIN) {
                layoutBinderHelper.setLoginView(true);
                binding.txtFb.setText(getString(R.string.login_with_facebook));
                binding.txtGoogle.setText(getString(R.string.login_with_google));
            } else if (position == SIGNUP) {
                layoutBinderHelper.setLoginView(false);
                binding.txtFb.setText(getString(R.string.signup_with_facebook));
                binding.txtGoogle.setText(getString(R.string.signup_with_google));
            }
        }
    };

    public void onClickBack() {
        onBackPressed();
    }

    public void forgotPassword() {
        activityViewModel.showForgotPasswordDialog();
    }

    public void onClickLogin() {
        if (activityViewModel.validLoginData(layoutBinderHelper, binding)) {
            Utils.hideSoftKeyboard(this);
            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
            authenticationRequest.setUsername(binding.etUsername.getText().toString());
            authenticationRequest.setPassword(binding.etPassword.getText().toString());
            authenticationRequest.setDevice_token(getToken());
            authenticationRequest.setDevice_type(1);
            activityViewModel.loginSignup(API_LOGIN, authenticationRequest, false, false);
        }
    }

    public void onClickCreateAccount() {
        if (activityViewModel.validSignUpData(layoutBinderHelper, binding, getEmail(), getMobile())) {
            Utils.hideSoftKeyboard(this);
//            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
////            authenticationRequest.setUsername(binding.etSUsername.getText().toString());
//            authenticationRequest.setPassword(binding.etSPassword.getText().toString());
//            authenticationRequest.setEmail(getEmail());
//            authenticationRequest.setDevice_token(getToken());
//            authenticationRequest.setDevice_type(1);
//            String cCode = binding.ccp.getSelectedCountryCodeWithPlus();
//            authenticationRequest.setPhone(getMobile());
//            authenticationRequest.setPreFix(cCode);
//            activityViewModel.loginSignup(API_SIGNUP, authenticationRequest, true, false);

            Intent intent = new Intent(this, OtpActivity.class);
            intent.putExtra("mobile", getMobile());
            intent.putExtra("prefix", binding.ccp.getSelectedCountryCodeWithPlus());
            intent.putExtra("pass", binding.etSPassword.getText().toString());
            intent.putExtra("email", getEmail());
            startActivity(intent);

        }
    }

    public void onClickFacebook() {
//        if (layoutBinderHelper.getShow_login()) {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.login_with_facebook_button_click));
//        } else {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.sign_up_with_facebook_button_click));
//        }
        Utils.hideSoftKeyboard(this);
        layoutBinderHelper.setIsLoadingFb(true);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    public void onClickGoogle() {
//        if (layoutBinderHelper.getShow_login()) {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.login_with_google_button_click));
//        } else {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.sign_up_with_google_button_click));
//        }
        Utils.hideSoftKeyboard(this);
        layoutBinderHelper.setIsLoadingGoogle(true);

        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void showHideSignupPassword() {
        activityViewModel.showHidePassword(binding.etSPassword, binding.imgPassword, layoutBinderHelper);
    }

    public void showHideLoginPassword() {
        activityViewModel.showHidePassword(binding.etPassword, binding.imgLPassword, layoutBinderHelper);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    String json = new Gson().toJson(account);
                    Log.e("Google Response", json);

//                    String username = "agent_" + new Random().nextInt(10000);
                    AuthenticationRequest authenticationRequest = new AuthenticationRequest();
                    authenticationRequest.setUsername("");
                    authenticationRequest.setEmail(account.getEmail());
                    authenticationRequest.setDevice_token(getToken());
                    authenticationRequest.setGoogle_id(account.getId());
                    authenticationRequest.setFirst_name(account.getGivenName());
                    authenticationRequest.setLast_name(account.getFamilyName());
                    authenticationRequest.setDevice_type(1);

                    activityViewModel.loginSignup(API_LOGIN, authenticationRequest, false, true);

                    Utils.trackFirebaseEvent(LoginSignUpActivity.this, "Login_With_Gmail_Success");
                }
            } catch (ApiException e) {
                Log.e("Google fails", Objects.requireNonNull(e.getMessage()));
                layoutBinderHelper.setIsLoadingGoogle(false);
                Utils.trackFirebaseEvent(LoginSignUpActivity.this, "Login_With_Google_Error");
            }
        }
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
                        //redirectActivity(ProfileVerificationActivity.class);
                        openSteps(getProfileData().registration_step);
                    } else {
                        gotoMainActivity(Constants.TAB_HOME);
                    }
                }
            }
        }
    }

    public static class LayoutBinderHelper extends BaseObservable {
        private Boolean show_login, isLoading, isLoadingGoogle, isLoadingFb;

        public LayoutBinderHelper() {
            this.show_login = true;
            this.isLoading = false;
            this.isLoadingFb = false;
            this.isLoadingGoogle = false;
        }

        public void setLoginView(boolean presentationElementsVisible) {
            if (presentationElementsVisible) {
//                binding.txtGoogle.setText(getString(R.string.login_with_google));
//                binding.txtFb.setText(getString(R.string.login_with_facebook));
            } else {
//                binding.txtGoogle.setText(getString(R.string.signup_with_google));
//                binding.txtFb.setText(getString(R.string.signup_with_facebook));
            }
            this.show_login = presentationElementsVisible;
            notifyChange();
        }

        public void setIsLoading(boolean isLoading) {
            this.isLoading = isLoading;
//            disableEnableTouch(isLoading);
            notifyPropertyChanged(BR.isLoading);
        }

        public void setIsLoadingGoogle(boolean isLoadingGoogle) {
            this.isLoadingGoogle = isLoadingGoogle;
//            disableEnableTouch(isLoading);
            notifyPropertyChanged(BR.isLoadingGoogle);
        }

        public void setIsLoadingFb(boolean isLoadingFb) {
            this.isLoadingFb = isLoadingFb;
//            disableEnableTouch(isLoading);
            notifyPropertyChanged(BR.isLoadingFb);
        }

        @Bindable
        public Boolean getShow_login() {
            return show_login;
        }

        @Bindable
        public Boolean getIsLoading() {
            return isLoading;
        }

        @Bindable
        public Boolean getIsLoadingFb() {
            return isLoadingFb;
        }

        @Bindable
        public Boolean getIsLoadingGoogle() {
            return isLoadingGoogle;
        }
    }
}
