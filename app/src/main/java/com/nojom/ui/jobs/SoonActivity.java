package com.nojom.ui.jobs;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivitySoonBinding;
import com.nojom.ui.BaseActivity;


public class SoonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        ActivitySoonBinding chatBinding = DataBindingUtil.setContentView(this, R.layout.activity_soon);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
