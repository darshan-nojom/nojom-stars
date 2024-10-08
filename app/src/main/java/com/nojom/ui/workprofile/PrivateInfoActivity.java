package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.GoogleApiClient;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.databinding.ActivityPrivateInfoBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.UpdatePasswordActivity;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class PrivateInfoActivity extends BaseActivity implements PermissionListener {
    private PrivateInfoActivityVM privateInfoActivityVM;
    private ActivityPrivateInfoBinding binding;
    private ProfileResponse profileData;
    private File profileFile = null;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_private_info);
        binding.setActivity(this);

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();

        privateInfoActivityVM = ViewModelProviders.of(this).get(PrivateInfoActivityVM.class);
        privateInfoActivityVM.init(this, binding);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvSave.setOnClickListener(v -> {

            if (TextUtils.isEmpty(getFirstName()) && TextUtils.isEmpty(getLastName()) && TextUtils.isEmpty(getUsername()) && TextUtils.isEmpty(getEmail())
                    && TextUtils.isEmpty(getMobile())) {
                toastMessage(getString(R.string.all_fields_are_required));
                return;
            }

            if (TextUtils.isEmpty(getFirstName())) {
                toastMessage(getString(R.string.please_enter_first_name));
                return;
            }
            /*if (TextUtils.isEmpty(getLastName())) {
                toastMessage(getString(R.string.please_enter_last_name));
                return;
            }*/
//            if (TextUtils.isEmpty(getUsername())) {
//                toastMessage(getString(R.string.enter_username));
//                return;
//            }
            if (TextUtils.isEmpty(getEmail())) {
                toastMessage(getString(R.string.enter_valid_email));
                return;
            }
            if (!isValidEmail(getEmail())) {
                toastMessage(getString(R.string.enter_valid_email));
                return;
            }
            if (TextUtils.isEmpty(getMobile())) {
                toastMessage(getString(R.string.please_enter_mobile));
                return;
            }
            if (!binding.ccp.isValidFullNumber()) {
                toastMessage(getString(R.string.enter_valid_number));
                return;
            }
            privateInfoActivityVM.updateProfile(getFirstName(), getLastName(), getEmail(), getMobile(), getMobilePrefix(), getUsername(), profileFile, binding.segmentLoginGroupGender.getPosition() == 0 ? 2 : 1);
        });

        profileData = Preferences.getProfileData(this);
        binding.setProfileData(profileData);

        binding.toolbar.tvSave.setVisibility(View.GONE);

        boolean isSocialLoggedIn = Preferences.readBoolean(this, Constants.IS_SOCIAL_LOGIN, false);
        if (isSocialLoggedIn) {
            binding.txtPassTitle.setVisibility(View.GONE);
            binding.relPass.setVisibility(View.GONE);
            binding.lineView.setVisibility(View.GONE);
        }

        if (profileData != null) {
//            if (profileData.profilePic != null) {

            Glide.with(this).load(getImageUrl() + profileData.profilePic)
                    .placeholder(R.drawable.dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            binding.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(binding.imgProfile);
//            }
            if (profileData.contactNo != null) {
                try {
                    String[] split = profileData.contactNo.split("\\.");
                    if (split.length == 2) {
                        binding.etMobile.setText(split[1]);
                        binding.tvPhonePrefix.setText(split[0]);

                        String nameCode = Preferences.readString(this, COUNTRY_CODE, "");
                        if (!TextUtils.isEmpty(nameCode)) {
                            binding.ccp.setDetectCountryWithAreaCode(true);
                            binding.ccp.setCountryForNameCode(nameCode);
                        } else {
                            String code = split[0].replace("+", "").replace(" ", "");//added by DPP on 12th Feb[replace " " with ""]
                            binding.ccp.setCountryForPhoneCode(Integer.parseInt(code));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (profileData.gender != null) {
                binding.segmentLoginGroupGender.setPosition(profileData.gender == 2 ? 0 : 1, false);
            }
        }

        binding.ccp.registerCarrierNumberEditText(binding.etMobile);
        addTextChangeEvent(binding.etFirstname, binding.etLastname, binding.etEmail, binding.etMobile);

        binding.ccp.setOnCountryChangeListener(() -> {
            binding.toolbar.tvSave.setVisibility(View.VISIBLE);
            binding.tvPhonePrefix.setText(binding.ccp.getSelectedCountryCodeWithPlus());
        });

        setOnProfileLoadListener(this::onProfileLoad);

        Utils.trackFirebaseEvent(this, "Profile_Screen");

        privateInfoActivityVM.getShowProgress().observe(this, showProgress -> {
            disableEnableTouch(showProgress);
            if (showProgress) {
                binding.toolbar.tvSave.setVisibility(View.INVISIBLE);
                binding.toolbar.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.toolbar.tvSave.setVisibility(View.VISIBLE);
                binding.toolbar.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        if (profileData.profilePic != null) {
            binding.imgProfile.setOnClickListener(v -> viewFile(getImageUrl() + profileData.profilePic));
        }

    }

   /* // Construct a request for phone numbers and show the picker
    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        try {
            if (mGoogleApiClient != null) {
                PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                        mGoogleApiClient, hintRequest);
                startIntentSenderForResult(intent.getIntentSender(),
                        1212, null, 0, 0, 0);
            }
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
                        binding.toolbar.tvSave.setVisibility(View.VISIBLE);
                        profileFile = new File(imgPath.get(0).getPath());

                        Glide.with(this).load(profileFile)
                                .placeholder(R.drawable.dp)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(binding.imgProfile);


//                        CropImage.activity(Uri.fromFile(new File(imgPath.get(0).getPath())))
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setAspectRatio(1, 1)
//                                .setGuidelines(CropImageView.Guidelines.OFF)
////                            .setCropMenuCropButtonIcon(R.drawable.check_done)
//                                .setAllowFlipping(false)
//                                .setMinCropResultSize((int) getResources().getDimension(R.dimen._100sdp), (int) getResources().getDimension(R.dimen._100sdp))
//                                .setMaxCropResultSize((int) getResources().getDimension(R.dimen._599sdp), (int) getResources().getDimension(R.dimen._599sdp))
//                                .setFixAspectRatio(true)
//                                .setAllowRotation(false)
//                                .start(this);
                    } else {
                        toastMessage(getString(R.string.image_not_selected));
                    }
                }
            } else if (requestCode == 1212) {
                if (resultCode == RESULT_OK) {
//                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId()-; <-- E.164 format phone number on 10.2.+ devices
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishToRight();
    }

    public void onClickEditProfile() {
        if (checkAndRequestPermissions()) {
            Intent intent = new Intent(PrivateInfoActivity.this, ImagePickActivity.class);
            intent.putExtra(IS_NEED_CAMERA, true);
            intent.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
        } else {
            checkPermission();
        }
    }

    public void onClickChangePass() {
        redirectActivity(UpdatePasswordActivity.class);
    }

    private void addTextChangeEvent(EditText... editTexts) {
        for (EditText edittext : editTexts) {
            edittext.addTextChangedListener(textWatcher);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            binding.toolbar.tvSave.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public String getFirstName() {
        return Objects.requireNonNull(binding.etFirstname.getText()).toString().trim();
    }

    public String getLastName() {
        return Objects.requireNonNull(binding.etLastname.getText()).toString().trim();
    }

    public String getEmail() {
        return Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
    }

    public String getUsername() {
        return Objects.requireNonNull(binding.etUsername.getText()).toString().trim();
    }

    private String getMobile() {
        return Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
    }

    private String getMobilePrefix() {
        return binding.ccp.getSelectedCountryCodeWithPlus();
    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    public void onProfileLoad(ProfileResponse data) {
        privateInfoActivityVM.getShowProgress().setValue(false);

        toastMessage(getString(R.string.privateinfo_updated_succeefully));
        onBackPressed();
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(PrivateInfoActivity.this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }


   /* @Override
    public void onConnected(@Nullable @org.jetbrains.annotations.Nullable Bundle bundle) {
        requestHint();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/
}
