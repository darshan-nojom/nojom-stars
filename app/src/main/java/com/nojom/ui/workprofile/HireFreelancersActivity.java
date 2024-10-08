package com.nojom.ui.workprofile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityHireFreelancerBinding;
import com.nojom.ui.BaseActivity;

public class HireFreelancersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        ActivityHireFreelancerBinding settingBinding = DataBindingUtil.setContentView(this, R.layout.activity_hire_freelancer);
        settingBinding.setHireAct(this);
        settingBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        settingBinding.toolbar.tvTitle.setText(getString(R.string.more_apps));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void openClientStore() {
        openClientAppOnPlaystore();
    }

    public void openRedlineStore() {
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.setData(Uri.parse(
                "https://play.google.com/store/apps/details?id=com.redline.coin&hl=en_IN"));
        intent1.setPackage("com.android.vending");
        startActivity(intent1);
    }
}
