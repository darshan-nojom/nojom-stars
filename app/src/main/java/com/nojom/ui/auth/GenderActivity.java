package com.nojom.ui.auth;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.textview.TextViewSFTextPro;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityGenderBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.NameActivityVM;
import com.nojom.util.Constants;

import java.util.Objects;

import retrofit2.Response;

public class GenderActivity extends BaseActivity implements ResponseListener, BaseActivity.OnProfileLoadListener {

    private ActivityGenderBinding binding;
    private NameActivityVM nameActivityVM;
    int selectedGender = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gender);
        nameActivityVM = ViewModelProviders.of(this).get(NameActivityVM.class);
        nameActivityVM.setNameActivityListener(this);
        setOnProfileLoadListener(this);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv3, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.txtSkip, Constants.FONT_AR_MEDIUM);
            setArFont(binding.txtMale, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtFemale, Constants.FONT_AR_REGULAR);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
        }
        initData();
    }

    private void initData() {

        binding.imgBack.setOnClickListener(v -> finish());
        binding.txtSkip.setOnClickListener(view -> {
            setOnProfileLoadListener(null);
            nameActivityVM.updateProfile(this, -1, Integer.parseInt(binding.txtStatus.getTag().toString()), RS_4_SOCIAL);
            redirectActivity(SocialActivity.class);
        });
        binding.relLogin.setOnClickListener(v -> {
            if (selectedGender != -1) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setVisibility(View.INVISIBLE);
                nameActivityVM.updateProfile(this, selectedGender, Integer.parseInt(binding.txtStatus.getTag().toString()), RS_4_SOCIAL);
            }
        });

        binding.relFemale.setOnClickListener(view -> {
            selectedGender = 1;
            binding.imgFemale.setImageResource(R.drawable.radio_button_active);
            binding.imgMale.setImageResource(R.drawable.circle_uncheck);
            binding.relFemale.setBackgroundResource(R.drawable.white_button_bg_border_7);
            binding.relMale.setBackgroundResource(R.drawable.white_button_bg_7);
            DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(GenderActivity.this, R.color.black));
        });
        binding.relMale.setOnClickListener(view -> {
            selectedGender = 2;
            binding.imgMale.setImageResource(R.drawable.radio_button_active);
            binding.imgFemale.setImageResource(R.drawable.circle_uncheck);
            binding.relMale.setBackgroundResource(R.drawable.white_button_bg_border_7);
            binding.relFemale.setBackgroundResource(R.drawable.white_button_bg_7);
            DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(GenderActivity.this, R.color.black));
        });

        binding.tv3.setText(getString(R.string.visible_to) + " " + getString(R.string.everyone).toLowerCase());
        binding.txtStatus.setTag(1);
        setPublicStatusValue(1, binding.txtStatus);
        binding.txtStatus.setOnClickListener(view -> whoCanSeeDialog(binding.txtStatus));
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
        redirectActivity(SocialActivity.class);
        //gotoMainActivity(Constants.TAB_HOME);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        setOnProfileLoadListener(null);
//        nameActivityVM.updateProfile(this, -1, RS_4_SOCIAL);
//        gotoMainActivity(Constants.TAB_HOME);
    }

    private void setPublicStatusValue(int publicStatus, TextView txtStatus) {
        txtStatus.setTag(publicStatus);
        switch (publicStatus) {
            case 2://brands
                txtStatus.setText(getString(R.string.brand_only));
                txtStatus.setTextColor(getResources().getColor(R.color.c_34A853));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.c_34A853));
                break;
            case 3://only me
                txtStatus.setText(getString(R.string.only_me));
                txtStatus.setTextColor(getResources().getColor(R.color.red_dark));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.red_dark));
                break;
            default:
                txtStatus.setText(getString(R.string.public_));
                txtStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                DrawableCompat.setTint(txtStatus.getBackground(), ContextCompat.getColor(this, R.color.colorPrimary));
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
//        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        txtTitle.setText(getString(R.string.who_can_see_this_item_1));
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
            switch (status) {
                case 1:
                    binding.tv3.setText(getString(R.string.visible_to) + " " + getString(R.string.everyone));
                    break;
                case 2:
                    binding.tv3.setText(getString(R.string.visible_to) + " " + getString(R.string.brand_only).toLowerCase());
                    break;
                case 3:
                    binding.tv3.setText(getString(R.string.visible_to) + " " + getString(R.string.only_me).toLowerCase());
                    break;
            }

//            binding.relSave.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.black_button_bg_10, null));
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
}
