package com.nojom.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.nojom.R;
import com.nojom.databinding.ActivityMaintainanceBinding;
import com.nojom.util.Constants;

import io.intercom.android.sdk.Intercom;

public class MaintenanceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        ActivityMaintainanceBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_maintainance);
        binding.btnChat.setOnClickListener(v -> Intercom.client().displayMessageComposer());

        binding.btnRetry.setOnClickListener(v -> checkForMaintenance());
    }

    @Override
    public void onBackPressed() {
        return;
    }

    public void checkForMaintenance() {
        try {
            final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
            remoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this, task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        if (remoteConfig.getString(Constants.KEY_FOR_MAINTENANCE).equalsIgnoreCase("true")) {
                            toastMessage(getString(R.string.app_under_maintenance));
                        } else {
                            finish();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
