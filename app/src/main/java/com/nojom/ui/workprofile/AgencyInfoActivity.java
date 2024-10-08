package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.textview.TextViewSFTextPro;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.databinding.ActivityAgencyInfoBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;
import com.nojom.util.Preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class AgencyInfoActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener, PermissionListener {

    private ActivityAgencyInfoBinding binding;
    private AgencyInfoActivityVM agencyInfoActivityVM;
    private ProfileResponse profileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_info);
        agencyInfoActivityVM = ViewModelProviders.of(this).get(AgencyInfoActivityVM.class);
        agencyInfoActivityVM.init(this);
        if (language.equals("ar")) {
            setArFont(binding.etAgencyName, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.tvSummary, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatusAbout, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv1, Constants.FONT_AR_BOLD);
            setArFont(binding.txtStatusPhone, Constants.FONT_AR_BOLD);
            setArFont(binding.tvPhone, Constants.FONT_AR_REGULAR);
            setArFont(binding.tvEmail, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatusEmail, Constants.FONT_AR_BOLD);
            setArFont(binding.tvWebsite, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatusWebsite, Constants.FONT_AR_BOLD);
            setArFont(binding.tvAddress, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatusAdds, Constants.FONT_AR_BOLD);
            setArFont(binding.tvSave, Constants.FONT_AR_MEDIUM);
        }
        initData();
    }

    private void initData() {
        setOnProfileLoadListener(this);

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.relSave.setOnClickListener(v -> {
            if (isValid(true)) {
                agencyInfoActivityVM.addAgencyInfoAPI(binding.etAgencyName.getText().toString(), binding.tvSummary.getText().toString(), binding.tvPhone.getText().toString(), binding.tvEmail.getText().toString(), binding.tvWebsite.getText().toString(), binding.tvAddress.getText().toString(), null, profileData.profile_agencies != null ? profileData.profile_agencies.id : 0, Integer.parseInt(binding.txtStatus.getTag().toString()), Integer.parseInt(binding.txtStatusAdds.getTag().toString()), Integer.parseInt(binding.txtStatusAbout.getTag().toString()), Integer.parseInt(binding.txtStatusPhone.getTag().toString()), Integer.parseInt(binding.txtStatusEmail.getTag().toString()), Integer.parseInt(binding.txtStatusWebsite.getTag().toString()), 1, profileFile);
            }
        });

        agencyInfoActivityVM.getIsShowProgress().observe(this, aBoolean -> {
            binding.progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            binding.tvSave.setVisibility(aBoolean ? View.INVISIBLE : View.VISIBLE);
        });

        agencyInfoActivityVM.getIsAgencyAddSuccess().observe(this, aBoolean -> {
            if (aBoolean) {
                onBackPressed();
            }
        });
        binding.imgAdd.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                /*Intent mediaIntent = new Intent(Intent.ACTION_PICK);
                mediaIntent.setType("image/*"); // Set the MIME type to include both images and videos
                startActivityForResult(mediaIntent, 1212);*/

                Intent intent = new Intent(this, ImagePickActivity.class);
                intent.putExtra(IS_NEED_CAMERA, true);
                intent.putExtra(Constant.MAX_NUMBER, 1);
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
            } else {
                checkPermission();
            }
        });

        profileData = Preferences.getProfileData(this);
        refreshViews();

        setupGroups();

        textChangeListener(binding.etAgencyName);
        textChangeListener(binding.tvSummary);
        textChangeListener(binding.tvEmail);
        textChangeListener(binding.tvAddress);
        textChangeListener(binding.tvWebsite);
        textChangeListener(binding.tvPhone);

    }

    private void textChangeListener(TextInputEditText etAgencyName) {
        etAgencyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isValid(false)) {
                    binding.relSave.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_button_bg_10, null));
                } else {
                    binding.relSave.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_l_button_bg_10, null));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupGroups() {
        try {
//            binding.sgAgencyName.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgAgencyName));
//            binding.sgAddress.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgAddress));
//            binding.sgAbout.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgAbout));
//            binding.sgPhone.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgPhone));
//            binding.sgEmail.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgEmail));
//            binding.sgWebsite.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgWebsite));
//            binding.sgNote.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgNote));

//            binding.sgAgencyName.setEnabled(false);
//            binding.sgAddress.setEnabled(false);
//            binding.sgAbout.setEnabled(false);
//            binding.sgPhone.setEnabled(false);
//            binding.sgEmail.setEnabled(false);
//            binding.sgWebsite.setEnabled(false);

            binding.txtStatus.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatus));
            binding.txtStatusAdds.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusAdds));
            binding.txtStatusEmail.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusEmail));
            binding.txtStatusAbout.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusAbout));
            binding.txtStatusPhone.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusPhone));
            binding.txtStatusWebsite.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatusWebsite));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPositionChanges(int position, SegmentedButtonGroup segmentedButtonGroup) {
        segmentedButtonGroup.setPosition(position);
//        agencyInfoActivityVM.makePublicPrivate(segmentedButtonGroup);
    }

    private void refreshViews() {

        if (profileData != null) {
            if (profileData.profile_agencies != null) {

                if (!TextUtils.isEmpty(profileData.profile_agencies.name)) {
                    binding.etAgencyName.setText(profileData.profile_agencies.name);
                }

                if (!TextUtils.isEmpty(profileData.profile_agencies.about)) {
                    binding.tvSummary.setText(profileData.profile_agencies.about);
                }

                if (!TextUtils.isEmpty(profileData.profile_agencies.phone)) {
                    binding.tvPhone.setText(profileData.profile_agencies.phone);
                }

                if (!TextUtils.isEmpty(profileData.profile_agencies.email)) {
                    binding.tvEmail.setText(profileData.profile_agencies.email);
                }

                if (!TextUtils.isEmpty(profileData.profile_agencies.address)) {
                    binding.tvAddress.setText(String.format("%s", profileData.profile_agencies.address));
                }

                if (!TextUtils.isEmpty(profileData.profile_agencies.website)) {
                    binding.tvWebsite.setText(profileData.profile_agencies.website);
                }
            }

            try {
                setPublicStatusValue(profileData.profile_agencies != null ? profileData.profile_agencies.address_public : 1, binding.txtStatusAdds);
                setPublicStatusValue(profileData.profile_agencies != null ? profileData.profile_agencies.phone_public : 1, binding.txtStatusPhone);
                setPublicStatusValue(profileData.profile_agencies != null ? profileData.profile_agencies.email_public : 1, binding.txtStatusEmail);
                setPublicStatusValue(profileData.profile_agencies != null ? profileData.profile_agencies.website_public : 1, binding.txtStatusWebsite);
                setPublicStatusValue(profileData.profile_agencies != null ? profileData.profile_agencies.about_public : 1, binding.txtStatusAbout);
                setPublicStatusValue(profileData.profile_agencies != null ? profileData.profile_agencies.name_public : 1, binding.txtStatus);

                Glide.with(this).load(profileData.filePaths.agency + profileData.profile_agencies.filename).placeholder(R.drawable.ic_agency_dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(binding.roundedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isValid(boolean isShow) {

        if (TextUtils.isEmpty(binding.etAgencyName.getText().toString().trim())) {
            if (isShow) {
                toastMessage(getString(R.string.please_enter_agency_name));
            }
            return false;
        }
//        if (TextUtils.isEmpty(binding.tvSummary.getText().toString().trim())) {
//            toastMessage(getString(R.string.please_enter_agency_about));
//            return false;
//        }
//        if (TextUtils.isEmpty(binding.tvPhone.getText().toString().trim())) {
//            toastMessage(getString(R.string.please_enter_phone));
//            return false;
//        }
//        if (TextUtils.isEmpty(binding.tvEmail.getText().toString().trim())) {
//            toastMessage(getString(R.string.please_enter_email));
//            return false;
//        }
        if (TextUtils.isEmpty(binding.tvWebsite.getText().toString().trim())) {
            if (isShow) {
                toastMessage(getString(R.string.please_enter_website));
            }
            return false;
        }
//        if (TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())) {
//            toastMessage(getString(R.string.please_enter_address));
//            return false;
//        }


        return true;
    }


    @Override
    public void onProfileLoad(ProfileResponse data) {
        if (data != null) {
            profileData = data;
            refreshViews();
            agencyInfoActivityVM.getIsShowProgress().postValue(false);
            onBackPressed();
        }
    }

    private void setPublicStatusValue(int publicStatus, TextView txtStatus) {
        txtStatus.setTag(publicStatus);
        switch (publicStatus) {
            case 2://brands
                txtStatus.setText(getString(R.string.brand_only));
                txtStatus.setTextColor(getResources().getColor(R.color.c_075E45));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_C7EBD1));
                break;
            case 3://only me
                txtStatus.setText(getString(R.string.only_me));
                txtStatus.setTextColor(getResources().getColor(R.color.red_dark));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_FADCD9));
                break;
            default:
                txtStatus.setText(getString(R.string.public_));
                txtStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_D4E4FA));
                break;
        }
    }

    public void whoCanSeeDialog(TextViewSFTextPro txtStatus) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_who);
        dialog.setCancelable(true);
        RadioButton chkPublic = dialog.findViewById(R.id.chk_public);
        RadioButton chkBrand = dialog.findViewById(R.id.chk_brand);
        RadioButton chkMe = dialog.findViewById(R.id.chk_me);
        ImageView tvCancel = dialog.findViewById(R.id.tv_cancel);
        RelativeLayout relSave = dialog.findViewById(R.id.rel_save);
        TextView txtTitle = dialog.findViewById(R.id.txt_title);
        TextView txtLblPub = dialog.findViewById(R.id.txt_lbl_public);
        TextView txtLblBrn = dialog.findViewById(R.id.txt_lbl_brand);
        TextView txtLblMe = dialog.findViewById(R.id.txt_lbl_me);
        TextView tv1 = dialog.findViewById(R.id.tv1);
        TextView tv2 = dialog.findViewById(R.id.tv2);
        TextView tv3 = dialog.findViewById(R.id.tv3);
        TextView tvSend = dialog.findViewById(R.id.tv_send);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        if (language.equals("ar")) {
//            setArFont(tvCancel, Constants.FONT_AR_REGULAR);
            setArFont(txtTitle, Constants.FONT_AR_MEDIUM);
            setArFont(txtLblPub, Constants.FONT_AR_REGULAR);
            setArFont(txtLblBrn, Constants.FONT_AR_REGULAR);
            setArFont(txtLblMe, Constants.FONT_AR_REGULAR);
            setArFont(tv1, Constants.FONT_AR_REGULAR);
            setArFont(tv2, Constants.FONT_AR_REGULAR);
            setArFont(tv3, Constants.FONT_AR_REGULAR);
            setArFont(tvSend, Constants.FONT_AR_BOLD);
        }
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        txtTitle.setText(getString(R.string.who_can_see_this_item_1));

        int stat = Integer.parseInt(txtStatus.getTag().toString());
        switch (stat) {
            case 1:
                chkPublic.setChecked(true);
                break;
            case 2:
                chkBrand.setChecked(true);
                break;
            case 3:
                chkMe.setChecked(true);
                break;

        }

        chkMe.setOnClickListener(v -> {
            chkMe.setChecked(true);
            chkBrand.setChecked(false);
            chkPublic.setChecked(false);
        });
        chkBrand.setOnClickListener(v -> {
            chkMe.setChecked(false);
            chkBrand.setChecked(true);
            chkPublic.setChecked(false);
        });
        chkPublic.setOnClickListener(v -> {
            chkMe.setChecked(false);
            chkBrand.setChecked(false);
            chkPublic.setChecked(true);
        });

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        relSave.setOnClickListener(v -> {
            int status = 0;
            if (chkPublic.isChecked()) {
                status = 1;
            } else if (chkBrand.isChecked()) {
                status = 2;
            } else if (chkMe.isChecked()) {
                status = 3;
            }

            if (status == 0) {
                toastMessage(getString(R.string.please_select_any));
                return;
            }
//            myStoreActivityVM.updateStores(status, companies.id, "", companies.title, companies.link);

            setPublicStatusValue(status, txtStatus);
            binding.relSave.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_button_bg_10, null));
            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void checkPermission() {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }

    File profileFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
//                        binding.toolbar.tvSave.setVisibility(View.VISIBLE);
                        profileFile = new File(imgPath.get(0).getPath());

                        Glide.with(this).load(profileFile).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                                        binding.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                                        binding.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(binding.roundedImage);
                        if (profileData != null) {
                            if (profileData.profile_agencies != null) {
                                binding.relSave.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_button_bg_10, null));
                            }
                        }
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
}
