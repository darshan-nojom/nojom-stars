package com.nojom.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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
import com.nojom.databinding.ActivitySignupBinding;
import com.nojom.model.requestmodel.AuthenticationRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.CodeActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.SegmentedButtonGroupNew;
import com.nojom.util.Utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends BaseActivity implements LoginInteractor {

    private ActivitySignupBinding binding;
    private LoginSignUpVM activityViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private int RC_SIGN_IN = 9001;
    private boolean isNeedToFinish = false;
    private LoginSignUpActivity.LayoutBinderHelper layoutBinderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        activityViewModel = new LoginSignUpVM(this);
        layoutBinderHelper = new LoginSignUpActivity.LayoutBinderHelper();
        activityViewModel.setLoginInteractor(this);
        activityViewModel.setLayoutBinderHelper(layoutBinderHelper);
        binding.setLayoutBinder(layoutBinderHelper);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv3, Constants.FONT_AR_REGULAR);
            setArFont(binding.etEmail, Constants.FONT_AR_REGULAR);
            setArFont(binding.etPassword, Constants.FONT_AR_REGULAR);
            setArFont(binding.etMobile, Constants.FONT_AR_REGULAR);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
            setArFont(binding.tvSignup, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv4, Constants.FONT_AR_REGULAR);
        }
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
            isNeedToFinish = getIntent().getBooleanExtra(Constants.LOGIN_FINISH, false);
        }

        binding.ccp.registerCarrierNumberEditText(binding.etMobile);
        binding.ccp.setOnCountryChangeListener(() -> {
            binding.etMobile.setText("");
            //binding.txtPrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());
            binding.ccp.setTag(binding.ccp.getSelectedCountryCodeWithPlus());
        });
        //binding.txtPrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());

        binding.relLogin.setOnClickListener(view -> onClickLogin());
        binding.imgFb.setOnClickListener(view -> onClickFacebook());
        binding.imgGoogle.setOnClickListener(view -> onClickGoogle());
        binding.imgBack.setOnClickListener(view -> onBackPressed());
        binding.tvSignup.setOnClickListener(view -> {
            redirectActivity(LoginActivity.class);
            finish();
        });

        binding.etMobile.addTextChangedListener(watcher);
        binding.etEmail.addTextChangedListener(watcher);
        binding.etPassword.addTextChangedListener(watcher);


        String s = getString(R.string.already_have_an_account_log_in);
        int[] colorList = {R.color.black};
        String[] fonts = {Constants.SFTEXT_SEMIBOLD};
        String[] words = {getString(R.string.login)};
        binding.tvSignup.setText(Utils.getBoldString(this, s, fonts, colorList, words));

        binding.passwordToggle2.setOnClickListener(view -> togglePasswordConfVisibility());

        activityViewModel.isContactUnique.observe(this, aBoolean -> {
            if (aBoolean) {//contact is unique, can redirect to next OTP screen
                activityViewModel.sendOtpPhone(binding.ccp.getSelectedCountryCodeWithPlus() + "." + getMobile());
            }
        });
        activityViewModel.isOtpSent.observe(this, aBoolean -> {
            if (aBoolean) {//contact is unique, can redirect to next OTP screen
                Intent intent = new Intent(this, CodeActivity.class);
                intent.putExtra("mobile", getMobile());
                intent.putExtra("prefix", binding.ccp.getSelectedCountryCodeWithPlus());
                intent.putExtra("pass", binding.etPassword.getText().toString());
                intent.putExtra("email", getEmail());
                startActivity(intent);
            }
        });
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

    private boolean isPasswordVisibleConf = false;

    private void togglePasswordConfVisibility() {
        if (isPasswordVisibleConf) {
            binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.passwordToggle2.setImageResource(R.drawable.eye_closed);
        } else {
            binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.passwordToggle2.setImageResource(R.drawable.eye_open);
        }
        isPasswordVisibleConf = !isPasswordVisibleConf;
        binding.etPassword.setSelection(binding.etPassword.length());
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (isValidData(false)) {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(SignupActivity.this, R.color.black));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.white));
            } else {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(SignupActivity.this, R.color.C_E5E5EA));
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
                //layoutBinderHelper.setIsLoadingFb(false);
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
                //layoutBinderHelper.setIsLoadingFb(false);
                Utils.trackFirebaseEvent(SignupActivity.this, "Login_With_Facebook_Error");
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
            if (position == 0) {//phone
                binding.linPhone.setVisibility(View.VISIBLE);
                binding.etEmail.setVisibility(View.GONE);
            } else if (position == 1) {//email
                binding.linPhone.setVisibility(View.GONE);
                binding.etEmail.setVisibility(View.VISIBLE);
            }
        }
    };

    public void onClickBack() {
        onBackPressed();
    }

    public void forgotPassword() {
        // activityViewModel.showForgotPasswordDialog();
        redirectActivity(ForgotPasswordActivity.class);
    }

    public void onClickLogin() {
        if (isValidData(true)) {
            Utils.hideSoftKeyboard(this);
            activityViewModel.checkContactExist(getEmail(), binding.ccp.getSelectedCountryCodeWithPlus() + "." + getMobile());
        }
    }


    private boolean isValidData(boolean isCheck) {

        if (TextUtils.isEmpty((Objects.requireNonNull(binding.etMobile.getText()).toString()))) {
            if (isCheck) {
                validationError(getString(R.string.enter_valid_number));
            }
            return false;
        }
        if (TextUtils.isEmpty((Objects.requireNonNull(binding.etEmail.getText()).toString()))) {
            if (isCheck) {
                validationError(getString(R.string.enter_valid_email));
            }
            return false;
        }
        if (!isValidEmailData1((Objects.requireNonNull(binding.etEmail.getText()).toString()))) {
            if (isCheck) {
                validationError(getString(R.string.enter_valid_email));
            }
            return false;
        }

        if (TextUtils.isEmpty((Objects.requireNonNull(binding.etPassword.getText()).toString()))) {
            if (isCheck) {
                validationError(getString(R.string.please_enter_password));
            }
            return false;
        }
        if (binding.etPassword.getText().toString().length() < 8) {
            // Show error message if the password is too short
            if (isCheck) {
                validationError(getString(R.string.password_must_be_at_least_8_characters_long));
            }
            return false;
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

    public void onClickGoogle() {
//        if (layoutBinderHelper.getShow_login()) {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.login_with_google_button_click));
//        } else {
//            Utils.trackAppsFlayerEvent(this, getString(R.string.sign_up_with_google_button_click));
//        }
        Utils.hideSoftKeyboard(this);
        //layoutBinderHelper.setIsLoadingGoogle(true);

        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

                    Utils.trackFirebaseEvent(SignupActivity.this, "Login_With_Gmail_Success");
                }
            } catch (ApiException e) {
                Log.e("Google fails", Objects.requireNonNull(e.getMessage()));
                //layoutBinderHelper.setIsLoadingGoogle(false);
                Utils.trackFirebaseEvent(SignupActivity.this, "Login_With_Google_Error");
            }
        }
    }

    @Override
    public void redirect(boolean isSignup) {
        Preferences.writeBoolean(this, Constants.IS_SHOW_FIRST_TIME, true);
        if (isSignup) {
            Intent intent = new Intent(this, CodeActivity.class);
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
                        Intent intent = new Intent(this, CodeActivity.class);
                        String[] split = getProfileData().contactNo.split("\\.");
                        if (split.length == 2) {
                            intent.putExtra("mobile", split[1]);
                            intent.putExtra("prefix", split[0]);
                        }
                        startActivity(intent);
                    } else {
                        if (getProfileData().is_verified == 0) {
//                            redirectActivity(ProfileVerificationActivity.class);
                            openSteps(getProfileData().registration_step);
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

    public class LayoutBinderHelper extends BaseObservable {
        private Boolean show_login, isLoading, isLoadingGoogle, isLoadingFb;

        public LayoutBinderHelper() {
            this.show_login = true;
            this.isLoading = false;
            this.isLoadingFb = false;
            this.isLoadingGoogle = false;
        }

        public void setIsLoading(boolean isLoading) {
            this.isLoading = isLoading;
            disableEnableTouch(isLoading);
            notifyPropertyChanged(BR.isLoading);
        }

        public void setIsLoadingGoogle(boolean isLoadingGoogle) {
            this.isLoadingGoogle = isLoadingGoogle;
            disableEnableTouch(isLoading);
            notifyPropertyChanged(BR.isLoadingGoogle);
        }

        public void setIsLoadingFb(boolean isLoadingFb) {
            this.isLoadingFb = isLoadingFb;
            disableEnableTouch(isLoading);
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

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    public static boolean isValidEmailData1(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
