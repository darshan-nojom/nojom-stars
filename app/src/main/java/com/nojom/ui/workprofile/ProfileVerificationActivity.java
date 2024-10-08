package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.adapter.SelectCityAdapter;
import com.nojom.adapter.SelectCountryAdapter;
import com.nojom.adapter.SelectStateAdapter;
import com.nojom.adapter.SkillsAdapter;
import com.nojom.apis.GetCountriesAPI;
import com.nojom.apis.GetServiceCategoryAPI;
import com.nojom.apis.ViewSkillAPI;
import com.nojom.databinding.ActivityProfileVerificationBinding;
import com.nojom.databinding.ActivitySelectSkillsBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.CityResponse;
import com.nojom.model.CountryResponse;
import com.nojom.model.GeneralModel;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.model.StateResponse;
import com.nojom.model.UserSkillsModel;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.UpdatePasswordActivity;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.LocationAddress;
import com.nojom.util.PermissionRequest;
import com.nojom.util.Preferences;
import com.nojom.util.SegmentedButtonGroupNew;
import com.nojom.util.Utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Response;

public class ProfileVerificationActivity extends BaseActivity implements PermissionListener, RecyclerviewAdapter.OnViewBindListner, ResponseListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ActivityProfileVerificationBinding binding;
    private ProfileResponse profileData;

    private File profileFile = null;
    private ProfileVerificationActivityVM myProfileActivityVM;
    private UpdateLocationActivityVM updateLocationActivityVM;
    private GetCountriesAPI getCountriesAPI;
    private ViewSkillAPI viewSkillAPI;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double latitude;
    private double longitude;
    private MyPlatformActivityVM nameActivityVM;
    private boolean isSubmitForVerification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_verification);
        myProfileActivityVM = ViewModelProviders.of(this).get(ProfileVerificationActivityVM.class);
        myProfileActivityVM.init(this, null);
        updateLocationActivityVM = ViewModelProviders.of(this).get(UpdateLocationActivityVM.class);
        updateLocationActivityVM.init(this);
        getCountriesAPI = new GetCountriesAPI();
        getCountriesAPI.init(this);
        getCountriesAPI.getCountries();
        nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
        nameActivityVM.getConnectedPlatform(this);
    }

    private void initData() {
        setOnProfileLoadListener(this::onProfileLoad);

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        binding.tvName.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.NAME));

        binding.tvUsername.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.USERNAME));
        binding.tvAbout.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.ABOUT_ME));

        binding.linSocialMedia.setOnClickListener(v -> redirectActivity(SocialMediaActivity.class));
        binding.linVerif.setOnClickListener(v -> redirectActivity(VerificationActivity.class));
        binding.relGender.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.GENDER));
        binding.relBdate.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.DOB));
        binding.relCountry.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.COUNTRY));
        binding.relCategory.setOnClickListener(v -> showCategoryDialog(false));
        binding.relAds.setOnClickListener(v -> showAdsPriceDialog());

        binding.relSubmit.setOnClickListener(v -> {
            if (profileData == null) {
                return;
            }
            if (TextUtils.isEmpty(profileData.profilePic)) {
                toastMessage(getString(R.string.upload_profile_photo_now));
                return;
            }
            if (TextUtils.isEmpty(profileData.firstName) && TextUtils.isEmpty(profileData.lastName)) {
                toastMessage(getString(R.string.please_enter_first_name));
                return;
            }
            if (TextUtils.isEmpty(profileData.username)) {
                toastMessage(getString(R.string.enter_username));
                return;
            }
            if (TextUtils.isEmpty(profileData.about_me)) {
                toastMessage(getString(R.string.about_me));
                return;
            }
            if (TextUtils.isEmpty(profileData.getCountryName(language))) {
                toastMessage(getString(R.string.select_your_location));
                return;
            }
            if (TextUtils.isEmpty(profileData.birth_date)) {
                toastMessage(getString(R.string.enter_your_date_of_birth));
                return;
            }

            if (profileData.expertise == null || TextUtils.isEmpty(profileData.expertise.getName(language))) {
                toastMessage(getString(R.string.select_your_expertise));
                return;
            }

            if (profileData.minPrice == null && profileData.maxPrice == null) {
                toastMessage(getString(R.string.ads_price_range));
                return;
            }

            if (profileData.minPrice != null && profileData.minPrice <= 0 && profileData.maxPrice <= 0) {
                toastMessage(getString(R.string.ads_price_range));
                return;
            }

            if (TextUtils.isEmpty(binding.tvSocialMedia.getTag().toString())) {
                toastMessage(getString(R.string.add_social_media));
                return;
            }


            binding.progressBarLive.setVisibility(View.VISIBLE);
            binding.tvEditProfile.setVisibility(View.INVISIBLE);
            isSubmitForVerification = true;
            myProfileActivityVM.updateProfile();
        });

        binding.txtEditProfile.setOnClickListener(v -> checkPermission());

        profileData = Preferences.getProfileData(this);

        updateUI();

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        nameActivityVM.getConnectedMediaDataList().observe(this, data -> {

            if (data != null && data.size() > 0) {
                binding.tvSocialMedia.setText(data.get(0).getName(language));
                binding.tvSocialMedia.setTag("done");
                if (data.size() > 1) {
                    binding.tvSocialMedia.setText(data.get(0).getName(language) + ", " + data.get(1).getName(language));
                }
            } else {
                binding.tvSocialMedia.setTag("");
                binding.tvSocialMedia.setText("");
            }
        });
    }

    private void updateUI() {
        profileData = Preferences.getProfileData(this);

        if (profileData != null) {

            if (profileData.firstName != null && profileData.lastName != null) {
                binding.tvName.setTextColor(getColor(R.color.black));
                binding.tvName.setText(profileData.firstName + " " + profileData.lastName);
            }
            if (profileData.username != null) {
                binding.tvUsername.setTextColor(getColor(R.color.black));
                binding.tvUsername.setText("@" + profileData.username);
            }
            //TODO: set about
            if (profileData.about_me != null) {
                binding.tvAbout.setTextColor(getColor(R.color.black));
                binding.tvAbout.setText(profileData.about_me);
            } else {
                binding.tvAbout.setTextColor(getColor(R.color.textgrayAccent));
                binding.tvAbout.setText(getString(R.string.about_me));
            }
            if (profileData.gender != null) {
                binding.tvGender.setText(profileData.gender == 2 ? getString(R.string.male) : getString(R.string.female));
            }
            if (profileData.minPrice != null && profileData.maxPrice != null) {
                if (getCurrency().equals("SAR")) {
                    binding.tvAdsPrice.setText(String.format("%s - %s %s", Utils.getDecimalValue(String.valueOf(profileData.minPrice)), Utils.getDecimalValue(String.valueOf(profileData.maxPrice)), getString(R.string.sar)));
                } else {
                    binding.tvAdsPrice.setText(String.format("%s%s - %s%s", getString(R.string.dollar), Utils.getDecimalValue(String.valueOf(profileData.minPrice)), getString(R.string.dollar), Utils.getDecimalValue(String.valueOf(profileData.maxPrice))));
                }
            }
            if (profileData.birth_date != null) {

                String monthLang;
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date dateObj = null;
                try {
                    dateObj = curFormater.parse(profileData.birth_date.split("T")[0]);
                    SimpleDateFormat postFormater = new SimpleDateFormat("MMM", Locale.getDefault());
                    monthLang = postFormater.format(dateObj);
                    if (language.equals("ar")) {
                        binding.tvBDate.setText(profileData.birth_date.split("T")[0].split("-")[2] + "-" + monthLang + "-" + profileData.birth_date.split("T")[0].split("-")[0]);
                    } else {
                        String d = profileData.birth_date.split("T")[0];
                        binding.tvBDate.setText(String.format("%s-%s-%s", d.split("-")[2], d.split("-")[1], d.split("-")[0]));
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
            if (profileData.getCountryName(language) != null) {
                binding.tvCountry.setText(profileData.getCountryName(language));
                if (!TextUtils.isEmpty(profileData.getCityName(language))) {
                    binding.tvCountry.setText(profileData.getCountryName(language) + ", " + profileData.getCityName(language));
                }
            }
            if (profileData.expertise != null) {
                binding.tvCat.setText(profileData.expertise.getName(language));
            }

            Glide.with(this).load(getImageUrl() + profileData.profilePic).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
            }).into(binding.imgProfile);


            if (profileData.profilePic != null) {
                binding.imgProfile.setOnClickListener(v -> viewFile(getImageUrl() + profileData.profilePic));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
                        profileFile = new File(imgPath.get(0).getPath());
                        Glide.with(this).load(profileFile).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
                        }).into(binding.imgProfile);

                        myProfileActivityVM.updateProfile(0, null, null, null, profileFile, null, -1, -1, null, 0, 0, null, 0);

//                        CropImage.activity(Uri.fromFile(new File(imgPath.get(0).getPath()))).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.OFF)
////                            .setCropMenuCropButtonIcon(R.drawable.check_done)
//                                .setAllowFlipping(false).setMinCropResultSize((int) getResources().getDimension(R.dimen._100sdp), (int) getResources().getDimension(R.dimen._100sdp)).setMaxCropResultSize((int) getResources().getDimension(R.dimen._599sdp), (int) getResources().getDimension(R.dimen._599sdp)).setFixAspectRatio(true).setAllowRotation(false).start(this);
                    } else {
                        toastMessage(getString(R.string.image_not_selected));
                    }
                }
            } else if (requestCode == 1212) {
                if (resultCode == RESULT_OK) {
//                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    // credential.getId(); <-- E.164 format phone number on 10.2.+ devices
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (dialogCategory != null && dialogCategory.isShowing()) {
                dialogCategory.dismiss();
            } else {
                logout();
            }

            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishToRight();
    }

    public void onClickChangePass() {
        redirectActivity(UpdatePasswordActivity.class);
    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    public void onProfileLoad(ProfileResponse data) {
        myProfileActivityVM.getShowProgress().setValue(-1);
        profileData = Preferences.getProfileData(this);

        if (nameDialog != null && nameDialog.isShowing()) {
            nameDialog.dismiss();
        }

        if (adsPriceDialog != null && adsPriceDialog.isShowing()) {
            adsPriceDialog.dismiss();
        }

        updateUI();

        if (dialogCategory != null && dialogCategory.isShowing()) {
            showCategoryDialog(true);
        }

        if (isSubmitForVerification) {
            finish();
            gotoMainActivity(Constants.TAB_HOME);
        }
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(ProfileVerificationActivity.this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }


    Dialog nameDialog, dialogCategory, dialogCategorySub, dialogTag, adsPriceDialog;
    TextView txtCountry, txtCity;
    LinearLayout linCity;

    private void showNameDialog(Utils.WindowScreen screen) {
        nameDialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        nameDialog.setTitle(null);
        nameDialog.setContentView(R.layout.dialog_profile_name);
        nameDialog.setCancelable(true);
        RelativeLayout rlSave = nameDialog.findViewById(R.id.rel_save);
        RelativeLayout rlAllowLocation = nameDialog.findViewById(R.id.rel_allow_loc);
        LinearLayout linName = nameDialog.findViewById(R.id.lin_name);
        LinearLayout linUname = nameDialog.findViewById(R.id.lin_userName);
        LinearLayout linAbout = nameDialog.findViewById(R.id.lin_about);
        LinearLayout linWebsite = nameDialog.findViewById(R.id.lin_website);
        LinearLayout linGender = nameDialog.findViewById(R.id.lin_gender);
        LinearLayout linDob = nameDialog.findViewById(R.id.lin_dob);
        LinearLayout linCountry = nameDialog.findViewById(R.id.lin_country);
        linCity = nameDialog.findViewById(R.id.lin_city);
        EditText etFname = nameDialog.findViewById(R.id.et_firstname);
        EditText etLname = nameDialog.findViewById(R.id.et_lastname);
        EditText etuName = nameDialog.findViewById(R.id.et_userName);
        EditText etAbout = nameDialog.findViewById(R.id.et_aboutme);
        EditText etWebsite = nameDialog.findViewById(R.id.et_website);
        EditText etDob = nameDialog.findViewById(R.id.et_dob);
        ProgressBar progressBar = nameDialog.findViewById(R.id.progress);
        SegmentedButtonGroupNew sbActiveWebsite = nameDialog.findViewById(R.id.segmentLoginGroup);
        SegmentedButtonGroupNew sbGender = nameDialog.findViewById(R.id.segmentLoginGroupGender);
        SegmentedButtonGroupNew sgAge = nameDialog.findViewById(R.id.segmentLoginGroupAge);

        TextView txtCancel = nameDialog.findViewById(R.id.txt_cancel);
        TextView tvSave = nameDialog.findViewById(R.id.tv_save);
        TextView txtTitle = nameDialog.findViewById(R.id.txt_title);
        txtCountry = nameDialog.findViewById(R.id.txt_country);
//        txtState = nameDialog.findViewById(R.id.txt_state);
        txtCity = nameDialog.findViewById(R.id.txt_city);
        DatePicker birthDatePicker = nameDialog.findViewById(R.id.birthDatePicker);

        myProfileActivityVM.getShowProgress().observe(this, aBoolean -> {

            switch (aBoolean) {
                case 0:
                    break;
                case 1:
                case 3:
                case 2:
                    if (progressBar != null && tvSave != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        tvSave.setVisibility(View.INVISIBLE);
                    }

                    break;

                default:
                    if (progressBar != null && tvSave != null) {
                        progressBar.setVisibility(View.GONE);
                        tvSave.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        });


        switch (screen) {
            case NAME:
                txtTitle.setText(getString(R.string.name));
                if (profileData != null) {
                    if (profileData.firstName != null && profileData.lastName != null) {
                        etFname.setText(profileData.firstName);
                        etLname.setText(profileData.lastName);
                    }
                }
                linName.setVisibility(View.VISIBLE);
                break;
            case USERNAME:
                txtTitle.setText(getString(R.string.username));
                linUname.setVisibility(View.VISIBLE);
                if (profileData != null && profileData.username != null) {
                    etuName.setText("@" + profileData.username);
                }
//                etuName.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        if (TextUtils.isEmpty(s)) {
//                            etuName.setText("@");
//                        } else {
//                            etuName.setText("@" + s);
//                        }
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });

                break;
            case ABOUT_ME:
                txtTitle.setText(getString(R.string.about_me));
                linAbout.setVisibility(View.VISIBLE);
                if (profileData != null && profileData.about_me != null) {
                    etAbout.setText("" + profileData.about_me);
                }
                break;
            case WEBSITE:
                txtTitle.setText(getString(R.string.website));
                if (profileData != null && profileData.website != null) {
                    etWebsite.setText(profileData.website);
                }
                if (profileData != null && profileData.website_status != null) {
                    sbActiveWebsite.setPosition(profileData.website_status, false);
                }
                linWebsite.setVisibility(View.VISIBLE);
                break;
            case GENDER:
                txtTitle.setText(getString(R.string.gender));
                if (profileData != null && profileData.gender != null) {
                    sbGender.setPosition(profileData.gender == 2 ? 0 : 1, false);
                }
                linGender.setVisibility(View.VISIBLE);
                break;
            case DOB:
                txtTitle.setText(getString(R.string.birthdate));
                if (profileData != null && profileData.birth_date != null) {
                    if (profileData.show_age != null) {
                        sgAge.setPosition(profileData.show_age == 2 ? 0 : 1, false);
                    }
                    String monthLang;
                    String dayLang;
                    String yearLang;
                    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date dateObj = null;
                    try {
                        dateObj = curFormater.parse(profileData.birth_date.split("T")[0]);
                        SimpleDateFormat postFormater = new SimpleDateFormat("MMM", Locale.getDefault());
                        SimpleDateFormat postFormaterDD = new SimpleDateFormat("dd", Locale.getDefault());
                        SimpleDateFormat postFormaterYY = new SimpleDateFormat("yyyy", Locale.getDefault());
                        monthLang = postFormater.format(dateObj);
                        dayLang = postFormaterDD.format(dateObj);
                        yearLang = postFormaterYY.format(dateObj);
                        if (language.equals("ar")) {
                            etDob.setText(dayLang + "-" + monthLang + "-" + yearLang);
                        } else {
                            String d = profileData.birth_date.split("T")[0];
                            etDob.setText(String.format("%s-%s-%s", d.split("-")[2], d.split("-")[1], d.split("-")[0]));
                        }
                        etDob.setTag(profileData.birth_date.split("T")[0]);

                        if (profileData.birth_date.split("T")[0] != null) {
                            int m = Integer.parseInt((profileData.birth_date.split("T")[0].split("-")[1]));
                            if ((profileData.birth_date.split("T")[0].split("-")[1]).startsWith("0")) {
                                m = Integer.parseInt((profileData.birth_date.split("T")[0].split("-")[1]).replace("0", ""));
                            }
                            birthDatePicker.updateDate(Integer.parseInt(profileData.birth_date.split("T")[0].split("-")[0]), m - 1, Integer.parseInt(profileData.birth_date.split("T")[0].split("-")[2]));
                        }

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                birthDatePicker.setMaxDate(System.currentTimeMillis());


                // Set a listener to capture the selected date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    birthDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
                        // Do something with the selected date
                        String days;
                        if (dayOfMonth < 10) {
                            days = "0" + dayOfMonth;
                        } else {
                            days = "" + dayOfMonth;
                        }

                        String month;
                        if (((monthOfYear + 1)) < 10) {
                            month = "0" + (monthOfYear + 1);
                        } else {
                            month = "" + (monthOfYear + 1);
                        }
                        String monthLang = month;
                        String dayLang = "" + days;
                        String yearLang = "" + year;
                        String selectedDate1 = year + "-" + month + "-" + days;
                        String selectedDate;

                        if (language.equals("ar")) {
                            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                            Date dateObj = null;
                            try {
                                dateObj = curFormater.parse(selectedDate1);
                                SimpleDateFormat postFormater = new SimpleDateFormat("MMM", Locale.getDefault());
                                SimpleDateFormat postFormaterDD = new SimpleDateFormat("dd", Locale.getDefault());
                                SimpleDateFormat postFormaterYY = new SimpleDateFormat("yyyy", Locale.getDefault());
                                monthLang = postFormater.format(dateObj);
                                dayLang = postFormaterDD.format(dateObj);
                                yearLang = postFormaterYY.format(dateObj);
                                monthLang = postFormater.format(dateObj);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            selectedDate = dayLang + "-" + monthLang + "-" + yearLang;
                        } else {
                            selectedDate = days + "-" + monthLang + "-" + year;
                        }

                        etDob.setText(selectedDate);
                        etDob.setTag(selectedDate1);
                    });
                }
                linDob.setVisibility(View.VISIBLE);
                break;
            case COUNTRY:
                txtTitle.setText(getString(R.string.country));
                if (profileData != null && profileData.getCountryName(language) != null) {
                    txtCountry.setText(profileData.getCountryName(language));
//                    txtState.setText(profileData.getStateName(language));
                    txtCity.setText(profileData.getCityName(language));

                    txtCountry.setTag(profileData.countryID);
                    txtCity.setTag(profileData.cityID);
//                    txtState.setTag(profileData.stateID);

                    if (profileData.countryID == 194) {
                        linCity.setVisibility(View.VISIBLE);
                        updateLocationActivityVM.getCityFromCountryState(2849);
                    } else {
                        linCity.setVisibility(View.GONE);
                    }
                }

//                if (profileData != null && profileData.countryID != null) {
//                    updateLocationActivityVM.getStateFromCountry(profileData.countryID);
//                }
//                if (profileData != null && profileData.stateID != null) {
//
//                }

                txtCountry.setOnClickListener(v -> {

                    if (getCountriesAPI.getCountryLiveData() != null) {
                        if (getCountriesAPI.getCountryLiveData() != null && getCountriesAPI.getCountryLiveData().getValue() != null && getCountriesAPI.getCountryLiveData().getValue().size() > 0) {
                            showCountrySelectDialog(getCountriesAPI.getCountryLiveData().getValue(), txtCountry, txtCity);
                        } else {
                            getCountriesAPI.getCountries();
                        }

                    }
                });
                /*txtState.setOnClickListener(v -> {
                    if (updateLocationActivityVM.getStateLiveData() != null && updateLocationActivityVM.getStateLiveData().getValue() != null && updateLocationActivityVM.getStateLiveData().getValue().size() > 0) {
                        showStateSelectDialog(updateLocationActivityVM.getStateLiveData().getValue(), txtCountry, txtState, txtCity);
                    }
                });*/

                txtCity.setOnClickListener(v -> {
                    if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null && updateLocationActivityVM.getCityLiveData().getValue().size() > 0) {
                        showCitySelectDialog(updateLocationActivityVM.getCityLiveData().getValue(), txtCountry, txtCity);
                    }
                });

                rlAllowLocation.setOnClickListener(v -> {
                    getUpdateLocation();
                });

                updateLocationActivityVM.getResponseMutableLiveData().observe(this, generalModelResponse -> {
                    if (generalModelResponse != null && profileData != null) {
                        profileData.countryName = txtCountry.getText().toString();
//                        profileData.stateName = txtState.getText().toString();
                        profileData.cityName = txtCity.getText().toString();

                        profileData.countryID = (Integer) txtCountry.getTag();
//                        profileData.stateID = (Integer) txtState.getTag();
                        profileData.cityID = (Integer) txtCity.getTag();

                        Preferences.setProfileData(this, profileData);

                        toastMessage(getString(R.string.location_updated_success));
                        updateUI();
                        getProfile();
                        nameDialog.dismiss();
                    }

                });

                updateLocationActivityVM.getIsShowStateProgress().observe(this, isShow -> {
                    isClickableView = isShow;
//                    binding.progressBarState.setVisibility(isShow ? View.VISIBLE : View.GONE);
                });

                updateLocationActivityVM.getIsShowCityProgress().observe(this, isShow -> {
                    isClickableView = isShow;
//                    binding.progressBarCity.setVisibility(isShow ? View.VISIBLE : View.GONE);
                });
                updateLocationActivityVM.getIsShowSaveProgress().observe(this, isShow -> {
                    isClickableView = isShow;

                    if (isShow) {
                        if (progressBar != null && tvSave != null) {
                            progressBar.setVisibility(View.VISIBLE);
                            tvSave.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (progressBar != null && tvSave != null) {
                            progressBar.setVisibility(View.GONE);
                            tvSave.setVisibility(View.VISIBLE);
                        }
                    }
                });

                linCountry.setVisibility(View.VISIBLE);


                break;
        }


        txtCancel.setOnClickListener(v -> nameDialog.dismiss());
        rlSave.setOnClickListener(v -> {

            switch (screen) {
                case NAME:

                    if (TextUtils.isEmpty(etFname.getText().toString().trim())) {
                        toastMessage(getString(R.string.please_enter_first_name));
                        return;
                    }
                    /*if (TextUtils.isEmpty(etLname.getText().toString().trim())) {
                        toastMessage(getString(R.string.please_enter_last_name));
                        return;
                    }*/
                    //save info API and close dialog
                    myProfileActivityVM.updateProfile(1, etFname.getText().toString(), etLname.getText().toString(), null, null, null, -1, -1, null, 0, 0, null, 0);
                    break;
                case USERNAME:

                    if (TextUtils.isEmpty(etuName.getText().toString().trim())) {
                        toastMessage(getString(R.string.enter_username));
                        return;
                    }
                    if (etuName.getText().toString().trim().equals("@")) {
                        toastMessage(getString(R.string.enter_username));
                        return;
                    }

                    //save info API and close dialog
                    String un = etuName.getText().toString();
                    if (etuName.getText().toString().startsWith("@")) {
                        un = un.substring(1);
                    }
//                    if (un.length() <= 5) {
//                        toastMessage(getString(R.string.minimum_char_should_be_5));
//                        return;
//                    }
                    myProfileActivityVM.updateProfile(1, null, null, un, null, null, -1, -1, null, 0, 0, null, 0);
                    break;
                case ABOUT_ME:

                    if (TextUtils.isEmpty(etAbout.getText().toString().trim())) {
                        toastMessage(getString(R.string.about_me));
                        return;
                    }
                    myProfileActivityVM.updateHeadlines(etAbout.getText().toString());
                    break;

                case WEBSITE:
                    if (TextUtils.isEmpty(etWebsite.getText().toString().trim())) {
                        toastMessage(getString(R.string.enter_valid_website));
                        return;
                    }
                    /*if (!Utils.validateWebsite(etWebsite.getText().toString().trim())) {
                        toastMessage(getString(R.string.website_must_be_start_with_https));
                        return;
                    }*/
                    //save info API and close dialog
                    myProfileActivityVM.updateProfile(3, etWebsite.getText().toString(), sbActiveWebsite.getPosition());
                    break;
                case GENDER:
                    //save info API and close dialog
                    myProfileActivityVM.updateProfile(3, null, null, null, null, null, -1, sbGender.getPosition() == 0 ? 2 : 1, null, 0, 0, null, 0);
                    break;
                case DOB:
                    if (TextUtils.isEmpty(etDob.getTag().toString().trim())) {
                        toastMessage(getString(R.string.enter_your_date_of_birth));
                        return;
                    }
                    //save info API and close dialog
                    myProfileActivityVM.updateProfile(3, null, null, null, null, null, -1, -1, etDob.getTag().toString(), 0, 0, null, sgAge.getPosition() == 0 ? 2 : 1);
                    break;
                case COUNTRY:
                    updateLocationActivityVM.updateLocation((Integer) txtCountry.getTag(), (Integer) txtCity.getTag(),-1);
                    break;
            }

            //nameDialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(nameDialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        nameDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        nameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        nameDialog.getWindow().setAttributes(lp);

        nameDialog.setOnDismissListener(dialog -> {
            updateLocationActivityVM.getResponseMutableLiveData().setValue(null);
        });
    }

    private void showOfferDialog(Utils.WindowScreen screen) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_profile_offer);
        dialog.setCancelable(true);
        RelativeLayout rlSave = dialog.findViewById(R.id.rel_save);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        TextView txtDesc = dialog.findViewById(R.id.txt_desc);

        ImageView imgClose = dialog.findViewById(R.id.img_close);

        switch (screen) {
            case OFFER:
                break;
            case MESSAGE:
                txtTitle.setText(getString(R.string.accept_message));
                txtDesc.setText(getString(R.string.choose_who_can_send_you_message_nany_user_verified_users_or_verified_brands));
                break;

        }

        imgClose.setOnClickListener(v -> dialog.dismiss());
        rlSave.setOnClickListener(v -> {

            switch (screen) {
                case NAME:
                    break;
                case USERNAME:
                    break;
            }

            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void showAdsPriceDialog() {
        adsPriceDialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        adsPriceDialog.setTitle(null);
        adsPriceDialog.setContentView(R.layout.dialog_profile_price_range);
        adsPriceDialog.setCancelable(true);

        RelativeLayout rlSave = adsPriceDialog.findViewById(R.id.rel_save);
        TextView txtCancel = adsPriceDialog.findViewById(R.id.txt_cancel);
        TextView txtMaxSign = adsPriceDialog.findViewById(R.id.txt_maxSign);
        TextView txtMinSign = adsPriceDialog.findViewById(R.id.txt_minSign);

        EditText etMaxPrice = adsPriceDialog.findViewById(R.id.et_maxPrice);
        EditText etMinPrice = adsPriceDialog.findViewById(R.id.et_minPrice);

        TextView tvSave = adsPriceDialog.findViewById(R.id.tv_save);
        ProgressBar progressBar = adsPriceDialog.findViewById(R.id.progress);

        if (getCurrency().equals("SAR")) {
            txtMaxSign.setText(getString(R.string.sar));
            txtMinSign.setText(getString(R.string.sar));
        } else {
            txtMaxSign.setText(getString(R.string.dollar));
            txtMinSign.setText(getString(R.string.dollar));
        }

        if (profileData != null && profileData.minPrice != null && profileData.maxPrice != null) {
            etMaxPrice.setText("" + profileData.maxPrice);
            etMinPrice.setText("" + profileData.minPrice);
        }


        myProfileActivityVM.getShowProgressAds().observe(this, aBoolean -> {

            if (aBoolean == 11) {
                if (progressBar != null && tvSave != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    tvSave.setVisibility(View.INVISIBLE);
                }
            } else {
                if (progressBar != null && tvSave != null) {
                    progressBar.setVisibility(View.GONE);
                    tvSave.setVisibility(View.VISIBLE);
                }
            }
        });


        txtCancel.setOnClickListener(v -> adsPriceDialog.dismiss());

        rlSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etMaxPrice.getText().toString().trim()) || Double.parseDouble(etMaxPrice.getText().toString()) == 0.0) {
                toastMessage(getString(R.string.max_price_can_t_be_empty_or_zero));
                return;
            }
            if (TextUtils.isEmpty(etMinPrice.getText().toString().trim()) || Double.parseDouble(etMinPrice.getText().toString()) == 0.0) {
                toastMessage(getString(R.string.min_price_can_not_be_empty));
                return;
            }
            if (Double.parseDouble(etMaxPrice.getText().toString()) < Double.parseDouble(etMinPrice.getText().toString())) {
                toastMessage(getString(R.string.max_price_should_be_greater_than_min_price));
                return;
            }
            myProfileActivityVM.updateProfile(11, null, null, null, null, null, -1, -1, null, Double.parseDouble(etMinPrice.getText().toString()), Double.parseDouble(etMaxPrice.getText().toString()), null, 0);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(adsPriceDialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        adsPriceDialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        adsPriceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        adsPriceDialog.getWindow().setAttributes(lp);
    }

    private void showCategoryDialog(boolean isUpadte) {
        RecyclerView rvCat, rvTag;
        if (!isUpadte) {
            dialogCategory = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
            dialogCategory.setTitle(null);
            dialogCategory.setContentView(R.layout.dialog_profile_category);
            dialogCategory.setCancelable(true);
            RelativeLayout relSave = dialogCategory.findViewById(R.id.rel_save);
            TextView txtCancel = dialogCategory.findViewById(R.id.txt_cancel);
            LinearLayout linCat = dialogCategory.findViewById(R.id.lin_category);
            LinearLayout linTag = dialogCategory.findViewById(R.id.lin_tags);
            rvCat = dialogCategory.findViewById(R.id.rv_category);
            rvTag = dialogCategory.findViewById(R.id.rv_tags);

            viewSkillAPI = new ViewSkillAPI();
            viewSkillAPI.init(this);

            linCat.setOnClickListener(v -> selCategoryDialog());

            linTag.setOnClickListener(v -> {
                viewSkillAPI.getSkillsList(1, null);
            });

            txtCancel.setOnClickListener(v -> dialogCategory.dismiss());
            relSave.setOnClickListener(v -> dialogCategory.dismiss());

            viewSkillAPI.getUserModel().observe(this, userSkillsModel -> {
                if (dialogTag == null && userSkillsModel != null && userSkillsModel.skillLists != null) {
                    selectedSkillLists = new ArrayList<>();
                    for (UserSkillsModel.SkillLists skills : userSkillsModel.skillLists) {
                        if (skills.psId != null) {

                            if (!selectedSkillLists.contains(skills)) {
                                skills.isSelected = true;
                                selectedSkillLists.add(skills);
                            } else {
                                skills.isSelected = true;
                            }

                        } else {
                            if (selectedSkillLists.size() > 0) {
                                for (UserSkillsModel.SkillLists selectedSkill : selectedSkillLists) {
                                    if (selectedSkill.id == skills.id) {
                                        skills.isSelected = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

//                    Intent i = new Intent(this, SelectSkillsActivity.class);
//                    i.putExtra(Constants.IS_EDIT, true);
//                    i.putExtra("size", profileData.skills != null ? profileData.skills.size() : 0);
//                    i.putExtra(SKILLS, userSkillsModel.skillLists);
//                    i.putExtra(SKILL_COUNT, userSkillsModel.skillCount);
//                    i.putExtra(SELECTED_SKILL, selectedSkillLists);

                    selectedSkillCount = profileData.skills != null ? profileData.skills.size() : 0;
                    skillLists = userSkillsModel.skillLists;
                    totalSkillCount = userSkillsModel.skillCount;
                    if (dialogTag == null) {
                        selTagDialog();
                    }
                }

            });
        } else {
            rvCat = dialogCategory.findViewById(R.id.rv_category);
            rvTag = dialogCategory.findViewById(R.id.rv_tags);
        }

        ArrayList<Skill> expertiseList = new ArrayList<>();
        try {
            if (profileData != null && profileData.expertise != null && profileData.expertise.id != null) {
                expertiseList.add(new Skill(profileData.expertise.id, profileData.expertise.getName(language), Utils.getExperienceLevel(profileData.expertise.length)));
            }
            SkillsAdapter mExpertiseAdapter = new SkillsAdapter(this, expertiseList, null, false);
            mExpertiseAdapter.setSELECTED_TAG("EXPERT");
//                if (expertiseList.size() > 0) {//selected skill id
//                    selectedProfileSkillId = expertiseList.get(0).skillId;
//                }
            rvCat.setAdapter(mExpertiseAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Skill> skillList = new ArrayList<>();
        try {
            if (profileData != null) {
                if (profileData.skills != null && profileData.skills.size() > 0) {
                    for (ProfileResponse.Skill data : profileData.skills) {
                        skillList.add(new Skill(data.getName(language), Utils.getRatingLevel(data.rating)));
                    }
                }

                SkillsAdapter mSkillAdapter = new SkillsAdapter(this, skillList, null, false);
                mSkillAdapter.setSELECTED_TAG("SKILL");
                rvTag.setAdapter(mSkillAdapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isUpadte) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(dialogCategory.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.BOTTOM;
            dialogCategory.show();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            lp.height = (int) (displayMetrics.heightPixels * 0.95f);
            dialogCategory.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogCategory.getWindow().setAttributes(lp);
        }
    }

    private List<GigSubCategoryModel.Data> servicesList;
    private RecyclerviewAdapter mAdapter;

    private WorkExperienceActivityVM workExperienceActivityVM;

    private void selCategoryDialog() {
        dialogCategorySub = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogCategorySub.setTitle(null);
        dialogCategorySub.setContentView(R.layout.dialog_profile_category_sub);
        dialogCategorySub.setCancelable(true);
        RelativeLayout relSave = dialogCategorySub.findViewById(R.id.rel_save);
        TextView txtCancel = dialogCategorySub.findViewById(R.id.txt_cancel);
        RecyclerView rvSkills = dialogCategorySub.findViewById(R.id.rv_category);

        workExperienceActivityVM = ViewModelProviders.of(this).get(WorkExperienceActivityVM.class);
        workExperienceActivityVM.init(this);
        workExperienceActivityVM.setResponseListener(this);

        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rvSkills.setLayoutManager(manager);
        rvSkills.addItemDecoration(new EqualSpacingItemDecoration(16));

        servicesList = Preferences.getInfSubCatList(this);

        if (servicesList != null && servicesList.size() > 0) {
            try {
                if (profileData != null && profileData.expertise != null) {
                    for (GigSubCategoryModel.Data data : servicesList) {
                        if (profileData.expertise.id != null && profileData.expertise.id == data.id) {
                            selectedExpertiseId = data.id;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mAdapter = new RecyclerviewAdapter((ArrayList<?>) servicesList, R.layout.item_skills_edit, ProfileVerificationActivity.this);
            rvSkills.setAdapter(mAdapter);
            rvSkills.setFocusable(false);

        } else {
            GetServiceCategoryAPI getServiceCategoryAPI = new GetServiceCategoryAPI();
            getServiceCategoryAPI.init(this);
            getServiceCategoryAPI.setServiceCategoryList();
            getServiceCategoryAPI.getServiceCategoriesById(4352);

            getServiceCategoryAPI.getGigSubCatList().observe(this, data -> {
                servicesList = data;

                try {
                    if (profileData != null && profileData.expertise != null) {
                        for (GigSubCategoryModel.Data data1 : servicesList) {
                            if (profileData.expertise.id != null && profileData.expertise.id == data1.id) {
                                selectedExpertiseId = data1.id;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mAdapter = new RecyclerviewAdapter((ArrayList<?>) data, R.layout.item_skills_edit, ProfileVerificationActivity.this);
                rvSkills.setAdapter(mAdapter);
                rvSkills.setFocusable(false);
            });
        }

        txtCancel.setOnClickListener(v -> dialogCategorySub.dismiss());
        relSave.setOnClickListener(v -> {
            if (selectedExpertiseId == -1) {
                toastMessage(getString(R.string.select_your_category));
                return;
            }
            //upadte skill (selectdExpertise id) and save close dialog
            workExperienceActivityVM.updateExperience("", "" + selectedExpertiseId);
            //dialogCategorySub.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogCategorySub.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogCategorySub.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        dialogCategorySub.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCategorySub.getWindow().setAttributes(lp);

        dialogCategorySub.setOnDismissListener(dialog -> mAdapter = null);
    }

    private EndlessRecyclerViewScrollListener scrollListener;
    private int pageNo = 1;
    private boolean isSearchCall, isUpdateSkillCompulsory;
    private SelectSkillsActivityVM skillsActivityViewModel;
    private int totalSkillCount;
    private ArrayList<UserSkillsModel.SkillLists> selectedSkillLists, skillLists;
    private RecyclerviewAdapter selectedAdapter;
    private int selectedSkillCount = 0;

    ActivitySelectSkillsBinding bindingTag;

    private void selTagDialog() {
        dialogTag = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogTag.setTitle(null);
        bindingTag = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_select_skills, null, false);
        dialogTag.setContentView(bindingTag.getRoot());
        dialogTag.setCancelable(true);
        bindingTag.etSearch.setHint(getString(R.string.search_for_tags));
        skillsActivityViewModel = ViewModelProviders.of(this).get(SelectSkillsActivityVM.class);
        skillsActivityViewModel.init(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        bindingTag.rvSelectedSkills.setLayoutManager(linearLayoutManager);

        selectedAdapter = null;

        if (skillLists == null || skillLists.size() == 0 || totalSkillCount == 0) {//fresh user or redirect from home screen dialog
            viewSkillAPI.getSkillsList(1, null);
        } else {
            bindingTag.shimmerLayout.stopShimmer();
            bindingTag.shimmerLayout.setVisibility(View.GONE);
        }

//        binding.toolbar.imgBack.setOnClickListener(v -> {
//            if (isUpdateSkillCompulsory) {
////                finish();
////                System.exit(0);
//            } else {
//                onBackPressed();
//            }
//        });
//        bindingTag.toolbar.tvSave.setOnClickListener(v -> onClickSave());
//        bindingTag.toolbar.imgBack.setVisibility(View.VISIBLE);
//        bindingTag.toolbar.tvEditCancel.setVisibility(View.GONE);
//        bindingTag.toolbar.rlEdit.setVisibility(View.VISIBLE);
//        bindingTag.toolbar.tvToolbarTitle.setText(getString(R.string.select_skills));

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    pageNo = page;
                    viewSkillAPI.getSkillsList(pageNo, Objects.requireNonNull(bindingTag.etSearch.getText()).toString().trim());
                }
            }
        };

        bindingTag.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                pageNo = 1;
                selectedAdapter = null;
                if (!isSearchCall) {
                    skillsActivityViewModel.getIsSearch().postValue(true);
                    if (!TextUtils.isEmpty(Objects.requireNonNull(bindingTag.etSearch.getText()).toString().trim())) {
                        viewSkillAPI.getSkillsList(pageNo, bindingTag.etSearch.getText().toString().trim());
                    } else {
                        viewSkillAPI.getSkillsList(pageNo, null);
                    }
                }
                return true;
            }
            return false;
        });

        bindingTag.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    pageNo = 1;
                    selectedAdapter = null;
                    viewSkillAPI.getSkillsList(pageNo, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        skillsActivityViewModel.getMutableCounter().observe(this, counter -> bindingTag.tvTotalSkill.setText(String.format(" / %d", counter)));

        skillsActivityViewModel.getMutablePageNo().observe(this, integer -> {
            if (scrollListener != null) {
                scrollListener.resetState();
            }
            if (pageNo == 1) {
                bindingTag.shimmerLayout.startShimmer();
                bindingTag.shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                bindingTag.shimmerLayout.stopShimmer();
                bindingTag.shimmerLayout.setVisibility(View.GONE);
            }
        });

        skillsActivityViewModel.getIsSearch().observe(this, isSearch -> {
            isSearchCall = isSearch;
            bindingTag.shimmerLayout.stopShimmer();
            bindingTag.shimmerLayout.setVisibility(View.GONE);
            bindingTag.toolbar.tvSave.setVisibility(View.VISIBLE);
            bindingTag.toolbar.progressBar.setVisibility(View.GONE);
            disableEnableTouch(false);
        });

        //get data from previous activity and set here
        if (selectedSkillLists != null && selectedSkillLists.size() > 0) {
            skillsActivityViewModel.getSelectedListMutableLiveData().postValue(null);
            skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
        }

        if (skillLists != null) {
            bindingTag.shimmerLayout.stopShimmer();
            bindingTag.shimmerLayout.setVisibility(View.GONE);

            skillsActivityViewModel.getMutableCounter().postValue(totalSkillCount);
//            skillsActivityViewModel.getListMutableLiveData().postValue(new ArrayList<>());
//            skillsActivityViewModel.getListMutableLiveData().postValue(skillLists);
        }


        skillsActivityViewModel.getListMutableLiveData().observe(this, this::setAdapter);

        skillsActivityViewModel.getIsSkillAdded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (dialogTag != null) {
                    dialogTag.dismiss();
                }
                getProfile();

            }
        });

        viewSkillAPI.getIsShowProgress().observe(this, aBoolean -> {
            if (aBoolean) {
                if (pageNo == 1) {
                    skillLists = new ArrayList<>();
                    skillsActivityViewModel.getMutablePageNo().postValue(pageNo);
                }
            } else {
                skillsActivityViewModel.getIsSearch().postValue(false);
            }
        });

        viewSkillAPI.getUserModel().observe(this, userSkillsModel1 -> {
            skillLists = new ArrayList<>();
            if (userSkillsModel1 != null && userSkillsModel1.skillLists != null) {

                for (UserSkillsModel.SkillLists skills : userSkillsModel1.skillLists) {
//                    if (skills.psId != null) {
//
//                        if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null
//                                && !skillsActivityViewModel.getSelectedListMutableLiveData().getValue().contains(skills)) {
//                            skills.isSelected = true;
//                            skillsActivityViewModel.getSelectedListMutableLiveData().getValue().add(skills);
//                        } else {
//                            skills.isSelected = true;
//                        }
//
//                    } else {
                        if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null
                                && skillsActivityViewModel.getSelectedListMutableLiveData().getValue().size() > 0) {
                            for (UserSkillsModel.SkillLists selectedSkill : skillsActivityViewModel.getSelectedListMutableLiveData().getValue()) {
                                if (selectedSkill.id == skills.id) {
                                    skills.isSelected = true;
                                    selectedSkill.isSelected = true;
                                    break;
                                }
                            }
                        }
//                    }
                }
                skillLists.addAll(userSkillsModel1.skillLists);
                totalSkillCount = selectedSkillCount;
//                skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
                skillsActivityViewModel.getMutableCounter().postValue(userSkillsModel1.skillCount);
                skillsActivityViewModel.getListMutableLiveData().postValue(skillLists);

            }
            skillsActivityViewModel.getIsSearch().postValue(false);
        });


        bindingTag.txtCancel.setOnClickListener(v -> dialogTag.dismiss());
        bindingTag.relSave.setOnClickListener(v -> {

            try {
                ArrayList<UserSkillsModel.SkillLists> selectedSkillLists = skillsActivityViewModel.getSelectedListMutableLiveData().getValue();
                if (selectedSkillLists != null && selectedSkillLists.size() > 0) {
                    for (UserSkillsModel.SkillLists skill : selectedSkillLists) {
                        if (skill.rating != null && isEmpty(String.valueOf(skill.rating))) {
                            validationError(getString(R.string.please_select_experience_level));
                            return;
                        }
                    }

                    StringBuilder skillIds = null;
                    StringBuilder ratingIds = null;
                    for (UserSkillsModel.SkillLists skill : skillsActivityViewModel.getSelectedListMutableLiveData().getValue()) {
                        skillIds = (skillIds == null ? new StringBuilder() : skillIds.append(",")).append(skill.id);
                        ratingIds = (ratingIds == null ? new StringBuilder() : ratingIds.append(",")).append(skill.rating != null ? skill.rating : skill.selectedRating);
                    }

                    String skillsId = skillIds == null ? "" : skillIds.toString();
                    String ratingsId = ratingIds == null ? "" : ratingIds.toString();
//                    binding.toolbar.tvSave.setVisibility(View.INVISIBLE);
//                    binding.toolbar.progressBar.setVisibility(View.VISIBLE);
                    disableEnableTouch(true);
                    skillsActivityViewModel.addSkills(skillsId, ratingsId,1);

                } else {
                    toastMessage(getString(R.string.please_select_a_skill_first));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogTag.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogTag.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        lp.height = (int) (displayMetrics.heightPixels * 0.95f);
        dialogTag.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTag.getWindow().setAttributes(lp);

        dialogTag.setOnDismissListener(dialog -> {
            selectedAdapter = null;
            dialogTag = null;
        });
    }

    @SuppressLint("DefaultLocale")
    private void setAdapter(ArrayList<UserSkillsModel.SkillLists> skills) {
        selectedAdapter = new RecyclerviewAdapter(skills, R.layout.item_selected_skills, ProfileVerificationActivity.this);
        bindingTag.rvSelectedSkills.setAdapter(selectedAdapter);

        if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null) {
            bindingTag.tvSkillNo.setText(String.format("%d", skillsActivityViewModel.getSelectedListMutableLiveData().getValue().size()));
        } else {
            bindingTag.tvSkillNo.setText(String.format("%d", 0));
        }
        bindingTag.shimmerLayout.stopShimmer();
        bindingTag.shimmerLayout.setVisibility(View.GONE);
    }

    private SelectCountryAdapter selectCountryAdapter;
    private SelectStateAdapter selectStateAdapter;
    private SelectCityAdapter selectCityAdapter;

    void showCountrySelectDialog(List<CountryResponse.CountryData> arrayList, TextView txtCountry, TextView txtCity) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (arrayList != null && arrayList.size() > 0) {
                if (txtCountry != null) {
                    for (CountryResponse.CountryData data : arrayList) {
                        data.isSelected = data.getCountryName(language).equalsIgnoreCase(txtCountry.getText().toString());
                    }
                }
                selectCountryAdapter = new SelectCountryAdapter(this, arrayList);
                rvTypes.setAdapter(selectCountryAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (selectCountryAdapter != null && selectCountryAdapter.getSelectedItem() != null) {
                txtCountry.setText(selectCountryAdapter.getSelectedItem().getCountryName(language));
                txtCountry.setTag(selectCountryAdapter.getSelectedItem().id);

//                txtState.setText("");
//                txtState.setHint(getString(R.string.select_state));//clear state & city, when select other country
                txtCity.setText("");
                txtCity.setHint(getString(R.string.select_city));
//                txtState.setTag(null);//clear tag [i.e ID]
                txtCity.setTag(null);

//                if (updateLocationActivityVM.getStateLiveData() != null && updateLocationActivityVM.getStateLiveData().getValue() != null) {
//                    updateLocationActivityVM.getStateLiveData().getValue().clear();
//                }

                if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null) {
                    updateLocationActivityVM.getCityLiveData().getValue().clear();
                }

                if (selectCountryAdapter.getSelectedItem().id == 194) {
                    linCity.setVisibility(View.VISIBLE);
                    updateLocationActivityVM.getCityFromCountryState(2849);
                } else {
                    linCity.setVisibility(View.GONE);
                }

//                updateLocationActivityVM.getStateFromCountry(selectCountryAdapter.getSelectedItem().id);
            } else {
                toastMessage(getString(R.string.please_select_one_item));
            }
            dialog.dismiss();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectCountryAdapter != null)
                    selectCountryAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    void showStateSelectDialog(List<StateResponse.StateData> arrayList, TextView txtCountry, TextView txtState, TextView txtCity) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (arrayList != null && arrayList.size() > 0) {
                if (txtState != null) {
                    for (StateResponse.StateData data : arrayList) {
                        data.isSelected = data.getStateName(language).equalsIgnoreCase(txtState.getText().toString());
                    }
                }
                selectStateAdapter = new SelectStateAdapter(this, arrayList);
                rvTypes.setAdapter(selectStateAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (selectStateAdapter != null && selectStateAdapter.getSelectedItem() != null) {
                txtState.setText(selectStateAdapter.getSelectedItem().getStateName(language));
                txtState.setTag(selectStateAdapter.getSelectedItem().id);
                dialog.dismiss();
                txtCity.setText("");
                txtCity.setHint(getString(R.string.select_city));
                txtCity.setTag(null);

                if (updateLocationActivityVM.getCityLiveData() != null && updateLocationActivityVM.getCityLiveData().getValue() != null) {
                    updateLocationActivityVM.getCityLiveData().getValue().clear();
                }

                updateLocationActivityVM.getCityFromCountryState(2849/*selectStateAdapter.getSelectedItem().id*/);
            } else {
                toastMessage(getString(R.string.please_select_one_item));
            }
            dialog.dismiss();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectStateAdapter != null) selectStateAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    void showCitySelectDialog(List<CityResponse.CityData> arrayList, TextView txtCountry, TextView txtCity) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_item_select_black);
        dialog.setCancelable(true);

        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvApply = dialog.findViewById(R.id.tv_apply);
        final EditText etSearch = dialog.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialog.findViewById(R.id.rv_items);

        etSearch.setHint(String.format(getString(R.string.search_for), getString(R.string.country).toLowerCase()));

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            if (arrayList != null && arrayList.size() > 0) {
                if (txtCity != null) {
                    for (CityResponse.CityData data : arrayList) {
                        data.isSelected = data.getCityName(language).equalsIgnoreCase(txtCity.getText().toString());
                    }
                }
                selectCityAdapter = new SelectCityAdapter(this, arrayList);
                rvTypes.setAdapter(selectCityAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvApply.setOnClickListener(v -> {
            if (selectCityAdapter != null && selectCityAdapter.getSelectedItem() != null) {
                txtCity.setText(selectCityAdapter.getSelectedItem().getCityName(language));
                txtCity.setTag(selectCityAdapter.getSelectedItem().id);
                dialog.dismiss();
            } else {
                toastMessage(getString(R.string.please_select_one_item));
            }
            dialog.dismiss();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectCityAdapter != null) selectCityAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    int selectedExpertiseId = -1;

    @Override
    public void bindView(View view, int position) {

        if (mAdapter != null && mAdapter.layoutId == R.layout.item_skills_edit) {

            final TextView textView = view.findViewById(R.id.tv_skill);
            if (servicesList == null || servicesList.size() == 0) {
                return;
            }
            textView.setText(servicesList.get(position).getName(language));

            if (selectedExpertiseId == servicesList.get(position).id) {
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_button_bg));
                textView.setTextColor(Color.WHITE);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_BOLD));
            } else {
                textView.setBackground(ContextCompat.getDrawable(this, R.drawable.white_button_bg));
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_REGULAR));
            }

            textView.setOnClickListener(view1 -> {
                selectedExpertiseId = servicesList.get(position).id;
                mAdapter.notifyDataSetChanged();
            });

        } else if (selectedAdapter != null && selectedAdapter.layoutId == R.layout.item_selected_skills) {

            TextView txtSkill = view.findViewById(R.id.tv_skill_name);
            final TextView txtSkillLevel = view.findViewById(R.id.tv_skill_level);
            txtSkillLevel.setVisibility(View.GONE);
            ImageView imgRemove = view.findViewById(R.id.img_add_remove);
            try {
//            ArrayList<UserSkillsModel.SkillLists> skillLists = skillsActivityViewModel.getListMutableLiveData().getValue();
//            if (skillLists != null && skillLists.size() > 0) {
                if (skillsActivityViewModel.getListMutableLiveData().getValue() != null) {
                    UserSkillsModel.SkillLists skills = skillsActivityViewModel.getListMutableLiveData().getValue().get(position);

                    txtSkill.setText(skills.getName(language));

                    if (skills.isSelected) {
                        imgRemove.setImageResource(R.drawable.close_red);
//                        txtSkillLevel.setVisibility(View.VISIBLE);
//                        if (skills.rating != null) {
//                            txtSkillLevel.setText(Utils.getRatingFromId(skills.rating));
//                        } else {
//                            txtSkillLevel.setText(Utils.getRatingFromId(0));//default
//                        }
                    } else {
//                        txtSkillLevel.setVisibility(View.GONE);
                        imgRemove.setImageResource(R.drawable.add);
                    }

//                    txtSkillLevel.setOnClickListener(view1 -> skillsActivityViewModel.showSingleSelectionDialog(txtSkillLevel, skills));

                    imgRemove.setOnClickListener(view12 -> {
                        try {
//                            skills.selectedRating = Utils.getRatingId(txtSkillLevel.getText().toString());
                            if (skills.isSelected) {
                                skills.isSelected = false;
                                selectedSkillCount--;
                                Objects.requireNonNull(skillsActivityViewModel.getSelectedListMutableLiveData().getValue()).remove(skills);
                            } else {
                                skills.isSelected = true;
                                selectedSkillCount++;
                                if (skillsActivityViewModel.getSelectedListMutableLiveData().getValue() != null) {
                                    skillsActivityViewModel.getSelectedListMutableLiveData().getValue().add(skills);
                                } else {
                                    ArrayList<UserSkillsModel.SkillLists> selectedSkillLists = new ArrayList<>();
                                    selectedSkillLists.add(skills);
                                    skillsActivityViewModel.getSelectedListMutableLiveData().postValue(selectedSkillLists);
                                }

                            }
                            selectedAdapter.notifyItemChanged(position);
                            if (bindingTag != null) {
                                bindingTag.tvSkillNo.setText(String.format("%d", skillsActivityViewModel.getSelectedListMutableLiveData().getValue().size()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        if (dialogCategorySub != null) {
            getProfile();
            dialogCategorySub.dismiss();
            //update Category dialog
            disableEnableTouch(false);
            isClickableView = false;
        }
    }

    @Override
    public void onError() {
        disableEnableTouch(false);
        isClickableView = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        toastMessage(getString(R.string.connection_suspended));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toastMessage(getString(R.string.connection_failed));
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    getLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(this, 1000);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
        } else {
//            showLocationDisclosure();
        }
    }

    private void getLocationPermission() {
        try {
            Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        if (ActivityCompat.checkSelfPermission(ProfileVerificationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProfileVerificationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                        latitude = mLastLocation.getLatitude();
                        longitude = mLastLocation.getLongitude();
                        LocationAddress.getAddressFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), ProfileVerificationActivity.this, new GeocoderHandler());
                    }

                    if (report.isAnyPermissionPermanentlyDenied()) {
                        //toastMessage(getString(R.string.please_give_permission));
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).onSameThread().check();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            Address locationAddress;
            if (message.what == 1) {
                try {
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getParcelable("address");
                    if (locationAddress != null) {
                        if (txtCountry != null) {
                            txtCountry.setText(locationAddress.getCountryName() != null ? locationAddress.getCountryName() : "");
//                            txtState.setText(locationAddress.getAdminArea() != null ? locationAddress.getAdminArea() : "");
                            txtCity.setText(locationAddress.getLocality() != null ? locationAddress.getLocality() : "");

//                            if (profileData != null) {
//                                profileData.countryName = txtCountry.getText().toString();
//                                profileData.stateName = txtState.getText().toString();
//                                profileData.cityName = txtCity.getText().toString();
//                                Preferences.setProfileData(MyProfileActivity.this, profileData);
//                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getUpdateLocation() {
        if (mGoogleApiClient.isConnected()) getLocation();
        else mGoogleApiClient.connect();
    }
}
