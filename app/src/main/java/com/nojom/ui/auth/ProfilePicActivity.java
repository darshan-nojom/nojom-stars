package com.nojom.ui.auth;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.databinding.ActivityProfileBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.NameActivityVM;
import com.nojom.util.Constants;
import com.nojom.util.PermissionRequest;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Response;

public class ProfilePicActivity extends BaseActivity implements ResponseListener, BaseActivity.OnProfileLoadListener, PermissionListener {

    private ActivityProfileBinding binding;
    private NameActivityVM nameActivityVM;
    private File profileFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        nameActivityVM = ViewModelProviders.of(this).get(NameActivityVM.class);
        nameActivityVM.setNameActivityListener(this);
        setOnProfileLoadListener(this);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv3, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.txtSkip, Constants.FONT_AR_MEDIUM);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);

        }
        initData();
    }

    private void initData() {
        binding.txtSkip.setOnClickListener(view -> {
//            setOnProfileLoadListener(null);
            nameActivityVM.updateProfile(this, "", null, RS_6_USERNAME);
        });
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.relLogin.setOnClickListener(v -> {
            if (profileFile != null) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setVisibility(View.INVISIBLE);
                nameActivityVM.updateProfile(this, getProfileData().firstName, profileFile, RS_6_USERNAME);
            }
        });

        binding.imgEdit.setOnClickListener(view -> {
            if (checkAndRequestPermissions()) {
                Intent intent = new Intent(this, ImagePickActivity.class);
                intent.putExtra(IS_NEED_CAMERA, true);
                intent.putExtra(Constant.MAX_NUMBER, 1);
                startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
            } else {
                checkPermission();
            }
        });

        binding.imgDelete.setOnClickListener(view -> {
            if (profileFile != null) {
                profileFile = null;
                binding.imgDelete.setVisibility(View.INVISIBLE);
                binding.imgProfile.setImageResource(R.drawable.ic_pic_dp);
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(ProfilePicActivity.this, R.color.C_E5E5EA));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.c_AEAEB2));
            }
        });
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        getProfile();
    }

    @Override
    public void onError() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        redirectActivity(UsernameActivity.class);
        //gotoMainActivity(Constants.TAB_HOME);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
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

                        Glide.with(this).load(profileFile).placeholder(R.drawable.ic_pic_dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
                        }).into(binding.imgProfile);
                        DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(ProfilePicActivity.this, R.color.black));
                        binding.imgDelete.setVisibility(View.VISIBLE);
                    } else {
                        toastMessage(getString(R.string.image_not_selected));
                        binding.imgDelete.setVisibility(View.INVISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        setOnProfileLoadListener(null);
//        nameActivityVM.updateProfile(this, "", null, RS_6_USERNAME);
//        gotoMainActivity(Constants.TAB_HOME);
    }
}
