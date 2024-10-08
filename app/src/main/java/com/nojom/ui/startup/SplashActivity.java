package com.nojom.ui.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.nojom.R;
import com.nojom.databinding.ActivitySplashBinding;
import com.nojom.databinding.ActivitySplashNewBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;


public class SplashActivity extends BaseActivity {

    private SplashActivityVM activityViewModel;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(true);
        ActivitySplashNewBinding bin = DataBindingUtil.setContentView(this, R.layout.activity_splash_new);
        if (language.equals("ar")) {
            bin.imgText.setImageResource(R.drawable.logo_txt_splash_ar);
        }
        activityViewModel = ViewModelProviders.of(this).get(SplashActivityVM.class);
        activityViewModel.init(this);
        Log.e("TOKEN ", getToken());
//        initData();
        activityViewModel.redirectIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initData() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                        activityViewModel.checkForNewVersion();
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*try {
            if (mRegistrationBroadcastReceiver != null) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(Constants.REGISTRATION_COMPLETE));
            }

            if (!isEmpty(getToken())) {
                activityViewModel.checkForNewVersion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
