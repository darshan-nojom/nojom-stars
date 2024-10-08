package com.nojom.ui.auth;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.adapter.AddSocialAdapter;
import com.nojom.databinding.ActivityAddSocialBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.model.SocialMediaResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.MyPlatformActivityVM;
import com.nojom.ui.workprofile.NameActivityVM;
import com.nojom.ui.workprofile.SocialMediaActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

public class SocialActivity extends BaseActivity implements ResponseListener, BaseActivity.OnProfileLoadListener, AddSocialAdapter.ItemChangedListener {

    private ActivityAddSocialBinding binding;
    private NameActivityVM nameActivityVM;
    private MyPlatformActivityVM myPlatformActivityVM;
    String PREFIX;
    AddSocialAdapter socialAdapter;
    List<ConnectedSocialMedia.Data> preList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_social);
        nameActivityVM = ViewModelProviders.of(this).get(NameActivityVM.class);
        nameActivityVM.setNameActivityListener(this);
        myPlatformActivityVM = ViewModelProviders.of(this).get(MyPlatformActivityVM.class);
        //setOnProfileLoadListener(this);
        myPlatformActivityVM.getInfluencerPlatform(this);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.txtSkip, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv3, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv4, Constants.FONT_AR_REGULAR);
//            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
        }
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        myPlatformActivityVM.getConnectedPlatform(this);
    }

    private void initData() {
        PREFIX = getString(R.string.nojom_com_e);
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtSkip.setOnClickListener(view -> {
            nameActivityVM.updateProfile(this, "", RS_5_PROFILE, 0);
            redirectActivity(ProfilePicActivity.class);
        });
//        setPublicStatusValue(1, binding.txtStatus);
        binding.relLogin.setOnClickListener(v -> {
//            if (!TextUtils.isEmpty(getUsername())) {
//                nameActivityVM.updateProfile(this, getUsername(), RS_5_PROFILE, 2);
//            }
            boolean isAnyValue = false;
            for (int i = 0; i < socialAdapter.getItemCount(); i++) {
                ConnectedSocialMedia.Data data = socialAdapter.getData(i);
                if (data != null && !TextUtils.isEmpty(data.username)) {
                    isAnyValue = true;
                    break;
                }
            }
            if (isAnyValue) {
                if (socialAdapter != null && socialAdapter.getItemCount() > 0) {
                    nameActivityVM.updateProfile(this, -1, RS_5_PROFILE);

                    for (int i = 0; i < socialAdapter.getItemCount(); i++) {
                        ConnectedSocialMedia.Data data = socialAdapter.getData(i);
                        if (data != null && !TextUtils.isEmpty(data.username)) {
                            nameActivityVM.addPlatform(data.username, "0", data.id, data.social_platform_type_id, 1);
                        }
                        if (i == (socialAdapter.getItemCount() - 1)) {//last position
                            redirectActivity(ProfilePicActivity.class);
                        }
                    }
                }
            }
        });
        binding.relAddMore.setOnClickListener(v -> {
            Intent intent = new Intent(this, SocialMediaActivity.class);
            intent.putExtra("isFrom", true);
            launchSomeActivity.launch(intent);
        });
//        binding.txtStatus.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatus));

        socialAdapter = new AddSocialAdapter(this, this);
        binding.rvSocial.setAdapter(socialAdapter);

        myPlatformActivityVM.getConnectedMediaDataList().observe(this, data -> {

            if (data != null) {
                socialAdapter.doRefresh(data);
            }

            if (socialAdapter.getItemCount() > 0) {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(SocialActivity.this, R.color.black));
            } else {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(SocialActivity.this, R.color.C_E5E5EA));
            }
        });

        myPlatformActivityVM.getSocialMediaDataList().observe(this, data -> {
            for (SocialMediaResponse.Data data1 : data) {
                if (data1.id == 1) {//social
                    for (SocialMediaResponse.SocialPlatform plt : data1.social_platforms) {
                        if (plt.id == 2 || plt.id == 6) {//snapchat & tiktok
                            ConnectedSocialMedia.Data sel = new ConnectedSocialMedia.Data();
                            sel.name = plt.name;
                            sel.nameAr = plt.nameAr;
                            sel.username = "";
                            sel.filename = plt.filename;
                            sel.web_url = plt.web_url;
                            sel.id = plt.id;
                            sel.social_platform_type_id = plt.social_platform_type_id;
                            preList.add(sel);
                        }
                    }
                }
            }
            Collections.reverse(preList);
            socialAdapter.doRefresh(preList);
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
        //redirectActivity(ProfilePicActivity.class);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        nameActivityVM.updateProfile(this, "", RS_5_PROFILE, 0);
//        gotoMainActivity(Constants.TAB_HOME);
    }

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //nameActivityVM.getConnectedPlatform(PlatformActivity.this);
                    if (result.getData() != null) {
                        SocialMediaResponse.Data data = (SocialMediaResponse.Data) result.getData().getSerializableExtra("data");
                        SocialMediaResponse.SocialPlatform plat = (SocialMediaResponse.SocialPlatform) result.getData().getSerializableExtra("plat");
                        ConnectedSocialMedia.Data sel = new ConnectedSocialMedia.Data();
                        if (data != null && plat != null) {
                            for (ConnectedSocialMedia.Data preData : preList) {
                                if (preData.id == data.id) {
                                    return;
                                }
                            }
                            sel.name = data.name;
                            sel.nameAr = data.nameAr;
                            sel.username = "";
                            sel.filename = plat.filename;
                            sel.web_url = plat.web_url;
                            sel.id = plat.id;
                            sel.social_platform_type_id = plat.social_platform_type_id;
                            preList.add(sel);
                        }

                        socialAdapter.doRefresh(preList);
                    }
                }
            });

    public void whoCanSeeDialog(TextView textView) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_who);
        dialog.setCancelable(true);
        RadioButton chkPublic = dialog.findViewById(R.id.chk_public);
        RadioButton chkBrand = dialog.findViewById(R.id.chk_brand);
        RadioButton chkMe = dialog.findViewById(R.id.chk_me);
        ImageView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView title = dialog.findViewById(R.id.txt_title);
        TextView lblPub = dialog.findViewById(R.id.txt_lbl_public);
        TextView tv1 = dialog.findViewById(R.id.tv1);
        TextView lblBrand = dialog.findViewById(R.id.txt_lbl_brand);
        TextView tv2 = dialog.findViewById(R.id.tv2);
        TextView lblMe = dialog.findViewById(R.id.txt_lbl_me);
        TextView tv3 = dialog.findViewById(R.id.tv3);
        TextView tvSend = dialog.findViewById(R.id.tv_send);
        RelativeLayout relSave = dialog.findViewById(R.id.rel_save);
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

        if (language.equals("ar")) {
            setArFont(title, Constants.FONT_AR_MEDIUM);
//            setArFont(tvCancel, Constants.FONT_AR_REGULAR);
            setArFont(lblPub, Constants.FONT_AR_REGULAR);
            setArFont(tv1, Constants.FONT_AR_REGULAR);
            setArFont(tv1, Constants.FONT_AR_REGULAR);
            setArFont(lblBrand, Constants.FONT_AR_REGULAR);
            setArFont(tv2, Constants.FONT_AR_REGULAR);
            setArFont(lblMe, Constants.FONT_AR_REGULAR);
            setArFont(tv3, Constants.FONT_AR_REGULAR);
            setArFont(tvSend, Constants.FONT_AR_BOLD);
        }

        int defSt = 1;
        if (textView != null && textView.getTag() != null) {
            defSt = Integer.parseInt(textView.getTag().toString());
        }


        switch (defSt) {
            case 2:
                chkBrand.setChecked(true);
                break;
            case 3:
                chkMe.setChecked(true);
                break;
            default:
                chkPublic.setChecked(true);
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

        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

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


            textView.setTag(status);
            setPublicStatusValue(status, textView);

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

    @Override
    public void onTextChanged(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(SocialActivity.this, R.color.black));

        }
    }
}
