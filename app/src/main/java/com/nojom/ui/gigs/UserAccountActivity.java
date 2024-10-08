package com.nojom.ui.gigs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.SelectedPlatformAdapter;
import com.nojom.databinding.ActivityUserAccountBinding;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.MyPlatformActivityVM;
import com.nojom.ui.workprofile.MyProfileActivity;
import com.nojom.ui.workprofile.NewProfileActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.Objects;

public class UserAccountActivity extends BaseActivity {
    private ActivityUserAccountBinding binding;
    private ProfileResponse profileData;
    private MyPlatformActivityVM nameActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_account);
        nameActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
        nameActivityVM.getConnectedPlatform(this);
    }

    private void updateUi() {
        profileData = Preferences.getProfileData(this);

        if (profileData != null) {

            if (profileData.firstName != null && profileData.lastName != null) {
                binding.tvName.setTextColor(getColor(R.color.black));
                binding.tvName.setText(profileData.firstName + " " + profileData.lastName);
            }
            if (profileData.username != null) {
                binding.tvUserName.setTextColor(getColor(R.color.black));
                binding.tvUserName.setText("@" + profileData.username);
            }
            if (profileData.website != null) {
                binding.tvLink.setTextColor(getColor(R.color.black));
                binding.tvLink.setText(profileData.website);
            }

            Glide.with(this).load(getImageUrl() + profileData.profilePic).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
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
            }).into(binding.imgProfile);

            binding.imgShare.setOnClickListener(v -> {
                if (profileData != null && profileData.firebaseLink != null) {
                    String nojomLink = "nojom.com/" + profileData.username;
                    String fLink = profileData.firebaseLink.replaceAll("https://", "");
                    shareApp(fLink + "\n\n" + nojomLink);
                }
            });

            binding.tvBioLink.setOnClickListener(v -> {
                String nojomLink = "nojom.com/" + profileData.username;
                shareApp(nojomLink);
            });

            binding.tvInAppLink.setOnClickListener(v -> {
                String fLink = profileData.firebaseLink.replaceAll("https://", "");
                shareApp(fLink);
            });
        }
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.tvEditProfile.setOnClickListener(v -> {
//            startActivity(new Intent(this, MyProfileActivity.class));
            startActivity(new Intent(this, NewProfileActivity.class));
        });

        nameActivityVM.getConnectedMediaDataList().observe(this, data -> {

            if (data != null) {
                setConnectedMediaAdapter(data);
            }
        });

        binding.tvSendOffer.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.OFFER));
        binding.tvChat.setOnClickListener(v -> showOfferDialog(Utils.WindowScreen.MESSAGE));
    }

    private void setConnectedMediaAdapter(ArrayList<ConnectedSocialMedia.Data> data) {
        SelectedPlatformAdapter mAdapter = new SelectedPlatformAdapter(this, data, null);
        mAdapter.isView(true);
        binding.rvPlatform.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        if (getParent() != null)
            redirectTab(Constants.TAB_HOME);
        else
            super.onBackPressed();
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
