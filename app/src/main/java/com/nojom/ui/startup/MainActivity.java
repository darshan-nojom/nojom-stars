package com.nojom.ui.startup;

import static com.nojom.util.Constants.PREF_SELECTED_LANGUAGE;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityMainBinding;
import com.nojom.model.GeneralModel;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.Locale;

import io.intercom.android.sdk.Intercom;

public class MainActivity extends TabActivity {

    private MainActivityVM mainActivityVM;
    private boolean doubleBackToExitPressedOnce = false;

    public void setStatusBarColor(int color, boolean darkStatusBarTint) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color);
        int flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        window.getDecorView().setSystemUiVisibility(darkStatusBarTint ? flag : 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(getResources().getColor(R.color.black), false);
        super.onCreate(savedInstanceState);
        String language = Preferences.readString(this, PREF_SELECTED_LANGUAGE, "en");
        if (language.equals("ar"))
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        loadAppLanguage(language);

        if (!isLogin()) {
//            Intent i = new Intent(this, SelectAccountActivity.class);
            Intent i = new Intent(this, OnboardingActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return;
        }
        ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityVM = new MainActivityVM(Task24Application.getActivity(), mainBinding, this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mainActivityVM.checkForIntentData(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Intercom.client().handlePushMessage();
            Utils.checkForMaintenance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserID() {
        return mainActivityVM.getUserID();
    }

    public void failureError(String message) {
        mainActivityVM.failureError(message);
    }

    public boolean checkStatus(GeneralModel model) {
        return mainActivityVM.checkStatus(model);
    }

    public String getJWT() {
        return mainActivityVM.getJWT();
    }

    public boolean isNetworkConnected() {
        return mainActivityVM.isNetworkConnected();
    }

    public void gotoMainActivity(int screen) {
        mainActivityVM.gotoMainActivity(screen);
    }

    public void clearTopActivity(Class<?> activityClass) {
        mainActivityVM.clearTopActivity(activityClass);
    }

    public void redirectActivity(Class<?> activityClass) {
        mainActivityVM.redirectActivity(activityClass);
    }

    public boolean isLogin() {
        return Preferences.readBoolean(this, Constants.IS_LOGIN, false);
    }

    public void loadAppLanguage(String language) {
        if (!TextUtils.isEmpty(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Resources res = getResources();
            DisplayMetrics dm = getBaseContext().getResources().getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(locale); // API 17+ only.
            switch (language) {
                case "ar":
                case "es":
                    conf.setLayoutDirection(locale);
                    break;
                case "en":
                    conf.locale = Locale.ENGLISH;
                    conf.setLayoutDirection(new Locale("en"));
                    break;
            }
            res.updateConfiguration(conf, dm);
        }
    }
}
