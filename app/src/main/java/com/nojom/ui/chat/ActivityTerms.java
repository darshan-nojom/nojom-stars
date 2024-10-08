package com.nojom.ui.chat;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityTermsBinding;
import com.nojom.ui.BaseActivity;

public class ActivityTerms extends BaseActivity {

    private ActivityTermsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(view -> onBackPressed());
    }
}
