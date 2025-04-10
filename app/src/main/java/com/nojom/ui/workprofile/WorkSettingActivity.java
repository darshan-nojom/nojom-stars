package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityWorkSettingBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.settings.NotificationActivity;
import com.nojom.ui.settings.SwitchAccountActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.util.Objects;

import io.intercom.android.sdk.Intercom;

public class WorkSettingActivity extends BaseActivity implements APIRequest.APIRequestListener {
    private ActivityWorkSettingBinding binding;
    private WorkMoreActivityVM workMoreActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_setting);
        binding.setWorkAct(this);
        workMoreActivityVM = ViewModelProviders.of(this).get(WorkMoreActivityVM.class);
        workMoreActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            binding.txtVersionName.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        binding.toolbar.tvTitle.setText(getString(R.string.setting));

        workMoreActivityVM.getIsShowProgress().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.btnSignout.setVisibility(View.INVISIBLE);
                binding.progressBarLogout.setVisibility(View.VISIBLE);
            } else {
                binding.btnSignout.setVisibility(View.VISIBLE);
                binding.progressBarLogout.setVisibility(View.GONE);
            }
        });
    }

    public void onClickNotification() {
        redirectActivity(NotificationActivity.class);
    }

    public void onClickLocation() {
        redirectActivity(UpdateLocationActivity.class);
    }

    public void onClickSwitchAccount() {
        redirectActivity(SwitchAccountActivity.class);
    }

    public void onClickLanguage() {
        selectLanguageDialog();
    }

    public void onClickCurrency() {
        selectCurrencyDialog();
    }

    public void onClickPolicyPages() {
        redirectUsingCustomTab(Constants.PRIVACY_POLICY);
//        redirectActivity(NewPolicyActivity.class);
    }

    public void onClickChat() {
//        openWhatsappChat();
        Intercom.client().displayMessageComposer();
    }

    public void onClickFaq() {
        redirectUsingCustomTab(Constants.FAQS);
    }

    public void onClickCareer() {
        redirectUsingCustomTab(Constants.CAREERS);
    }

    public void onClickShareApp() {
        shareApp();
    }

    public void onClickDataPrivacy() {
        startActivity(new Intent(this, DeleteAccountActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void selectLanguageDialog() {
        final boolean[] isEnglish = new boolean[1];
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_language);
        dialog.setCancelable(true);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView txtArabic = dialog.findViewById(R.id.txt_arabic);
        TextView txtEnglish = dialog.findViewById(R.id.txt_english);
        RelativeLayout txtApply = dialog.findViewById(R.id.rel_apply);

        if (language.equals("en")) {
            isEnglish[0] = true;
            txtEnglish.setBackgroundResource(R.drawable.blue_button_bg);
            txtEnglish.setTextColor(Color.WHITE);

            txtArabic.setBackground(null);
            txtArabic.setTextColor(Color.BLACK);
        } else {
            txtArabic.setBackgroundResource(R.drawable.blue_button_bg);
            txtArabic.setTextColor(Color.WHITE);

            txtEnglish.setBackground(null);
            txtEnglish.setTextColor(Color.BLACK);
        }

        txtEnglish.setOnClickListener(v -> {
            isEnglish[0] = true;
            txtEnglish.setBackgroundResource(R.drawable.blue_button_bg);
            txtEnglish.setTextColor(Color.WHITE);

            txtArabic.setBackground(null);
            txtArabic.setTextColor(Color.BLACK);

        });

        txtArabic.setOnClickListener(v -> {
            isEnglish[0] = false;
            txtArabic.setBackgroundResource(R.drawable.blue_button_bg);
            txtArabic.setTextColor(Color.WHITE);

            txtEnglish.setBackground(null);
            txtEnglish.setTextColor(Color.BLACK);

        });
        txtApply.setOnClickListener(v -> {
            workMoreActivityVM.updateProfile(isEnglish[0] ? "en" : "ar");
            Preferences.writeString(this, Constants.PREF_SELECTED_LANGUAGE, isEnglish[0] ? "en" : "ar");
            loadAppLanguage();
            dialog.dismiss();
            gotoMainActivity(TAB_HOME);
        });
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void selectCurrencyDialog() {
        String selCurrency = Preferences.readString(this, Constants.PREF_SELECTED_CURRENCY, "SAR");
        final boolean[] isUSDCurrency = new boolean[1];
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_currency);
        dialog.setCancelable(true);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView txtSar = dialog.findViewById(R.id.txt_sar);
        TextView txtUsd = dialog.findViewById(R.id.txt_usd);
        RelativeLayout txtApply = dialog.findViewById(R.id.rel_apply);

        if (selCurrency.equals("usd")) {
            isUSDCurrency[0] = true;
            txtUsd.setBackgroundResource(R.drawable.blue_button_bg);
            txtUsd.setTextColor(Color.WHITE);

            txtSar.setBackground(null);
            txtSar.setTextColor(Color.BLACK);
        } else {
            txtSar.setBackgroundResource(R.drawable.blue_button_bg);
            txtSar.setTextColor(Color.WHITE);

            txtUsd.setBackground(null);
            txtUsd.setTextColor(Color.BLACK);
        }

        txtSar.setOnClickListener(v -> {
            isUSDCurrency[0] = false;
            txtSar.setBackgroundResource(R.drawable.blue_button_bg);
            txtSar.setTextColor(Color.WHITE);

            txtUsd.setBackground(null);
            txtUsd.setTextColor(Color.BLACK);

        });

        txtUsd.setOnClickListener(v -> {
            isUSDCurrency[0] = true;
            txtUsd.setBackgroundResource(R.drawable.blue_button_bg);
            txtUsd.setTextColor(Color.WHITE);

            txtSar.setBackground(null);
            txtSar.setTextColor(Color.BLACK);

        });
        txtApply.setOnClickListener(v -> {
            Preferences.writeString(this, Constants.PREF_SELECTED_CURRENCY, isUSDCurrency[0] ? "usd" : "SAR");
            loadAppLanguage();
            dialog.dismiss();
            gotoMainActivity(TAB_HOME);
        });
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void onClickSignout() {
        workMoreActivityVM.showLogoutDialog(this);
    }

}
