package com.nojom.ui.startup;

import static com.nojom.util.Constants.PREF_SELECTED_LANGUAGE;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityMainBinding;
import com.nojom.model.GeneralModel;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import io.intercom.android.sdk.Intercom;

public class MainActivity extends TabActivity {

    private MainActivityVM mainActivityVM;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String language = Preferences.readString(this, PREF_SELECTED_LANGUAGE, "en").toString();
        if (language.equals("ar"))
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        if (!isLogin()) {
            Intent i = new Intent(this, SelectAccountActivity.class);
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

}
