package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.annotation.SuppressLint;
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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.credentials.Credential;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.databinding.ActivityMyProfileBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.UpdatePasswordActivity;
import com.nojom.ui.gigs.MyGigsActivity;
import com.nojom.util.PermissionRequest;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MyProfileActivity extends BaseActivity implements PermissionListener {
    private ActivityMyProfileBinding binding;
    private ProfileResponse profileData;

    private File profileFile = null;
    private MyProfileActivityVM myProfileActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile);
        myProfileActivityVM = ViewModelProviders.of(this).get(MyProfileActivityVM.class);
        myProfileActivityVM.init(this, null);
        initData();
    }

    private void initData() {
        setOnProfileLoadListener(this::onProfileLoad);

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        binding.tvName.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.NAME));

        binding.tvUsername.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.USERNAME));

        binding.tvWebsite.setOnClickListener(v -> showNameDialog(Utils.WindowScreen.WEBSITE));
        binding.tvSendOffer.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.OFFER));
        binding.tvMessage.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.MESSAGE));
        binding.linAgency.setOnClickListener(v -> redirectActivity(AgencyInfoActivity.class));
        binding.linService.setOnClickListener(v -> redirectActivity(MyGigsActivity.class));
        binding.linSocialMedia.setOnClickListener(v -> redirectActivity(SocialMediaActivity.class));

        binding.relViewMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, InfluencerProfileActivity.class);
            intent.putExtra("data", profileData);
            startActivity(intent);
        });

        binding.txtEditProfile.setOnClickListener(v -> checkPermission());

        profileData = Preferences.getProfileData(this);

        updateUI();

    }

    private void updateUI() {
        if (profileData != null) {

            if (profileData.firstName != null && profileData.lastName != null) {
                binding.tvName.setTextColor(getColor(R.color.black));
                binding.tvName.setText(profileData.firstName + " " + profileData.lastName);
            }
            if (profileData.username != null) {
                binding.tvUsername.setTextColor(getColor(R.color.black));
                binding.tvUsername.setText("@" + profileData.username);
            }
            if (profileData.websites != null) {
                binding.tvWebsite.setTextColor(getColor(R.color.black));
                binding.tvWebsite.setText(profileData.websites);
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

                        myProfileActivityVM.updateProfile(null, null, null, profileFile);

//                        CropImage.activity(Uri.fromFile(new File(imgPath.get(0).getPath()))).setCropShape(CropImageView.CropShape.RECTANGLE).setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.OFF)
////                            .setCropMenuCropButtonIcon(R.drawable.check_done)
//                                .setAllowFlipping(false).setMinCropResultSize((int) getResources().getDimension(R.dimen._100sdp), (int) getResources().getDimension(R.dimen._100sdp)).setMaxCropResultSize((int) getResources().getDimension(R.dimen._599sdp), (int) getResources().getDimension(R.dimen._599sdp)).setFixAspectRatio(true).setAllowRotation(false).start(this);
                    } else {
                        toastMessage(getString(R.string.image_not_selected));
                    }
                }
            } else if (requestCode == 1212) {
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
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
        myProfileActivityVM.getShowProgress().setValue(false);
        profileData = Preferences.getProfileData(this);

        if (nameDialog != null && nameDialog.isShowing()) {
            nameDialog.dismiss();
        }

        updateUI();
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(MyProfileActivity.this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }


    Dialog nameDialog;

    private void showNameDialog(Utils.WindowScreen screen) {
        nameDialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        nameDialog.setTitle(null);
        nameDialog.setContentView(R.layout.dialog_profile_name);
        nameDialog.setCancelable(true);
        RelativeLayout rlSave = nameDialog.findViewById(R.id.rel_save);
        LinearLayout linName = nameDialog.findViewById(R.id.lin_name);
        LinearLayout linUname = nameDialog.findViewById(R.id.lin_userName);
        LinearLayout linWebsite = nameDialog.findViewById(R.id.lin_website);
        EditText etFname = nameDialog.findViewById(R.id.et_firstname);
        EditText etLname = nameDialog.findViewById(R.id.et_lastname);
        EditText etuName = nameDialog.findViewById(R.id.et_userName);
        EditText etWebsite = nameDialog.findViewById(R.id.et_website);
        ProgressBar progressBar = nameDialog.findViewById(R.id.progress);

        TextView txtCancel = nameDialog.findViewById(R.id.txt_cancel);
        TextView tvSave = nameDialog.findViewById(R.id.tv_save);

        myProfileActivityVM.getShowProgress().observe(this, aBoolean -> {
            if (aBoolean) {
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


        switch (screen) {
            case NAME:
                if (profileData.firstName != null && profileData.lastName != null) {
                    etFname.setText(profileData.firstName);
                    etLname.setText(profileData.lastName);
                }
                linName.setVisibility(View.VISIBLE);
                break;
            case USERNAME:
                linUname.setVisibility(View.VISIBLE);
                if (profileData.username != null) {
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
//                        if (profileData.username != null) {
//
//                        } else {
//                            if (!TextUtils.isEmpty(s)) {
//                                etuName.setText("@" + s);
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });

                break;
            case WEBSITE:
                if (profileData.websites != null) {
                    etWebsite.setText(profileData.websites);
                }
                linWebsite.setVisibility(View.VISIBLE);
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
                    if (TextUtils.isEmpty(etLname.getText().toString().trim())) {
                        toastMessage(getString(R.string.please_enter_last_name));
                        return;
                    }
                    //save info API and close dialog
                    myProfileActivityVM.updateProfile(etFname.getText().toString(), etLname.getText().toString(), null, null);
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
                    un = un.substring(1);
                    //myProfileActivityVM.updateProfile(null, null, un, null);
                    nameDialog.dismiss();
                    break;
                case WEBSITE:
                    if (TextUtils.isEmpty(etWebsite.getText().toString().trim())) {
                        toastMessage(getString(R.string.enter_valid_website));
                        return;
                    }
                    //save info API and close dialog
                    nameDialog.dismiss();
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

}
