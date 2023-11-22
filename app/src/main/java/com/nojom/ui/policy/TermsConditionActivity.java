package com.nojom.ui.policy;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityTermsConditionBinding;
import com.nojom.ui.BaseActivity;

public class TermsConditionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTermsConditionBinding termsConditionBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms_condition);

        termsConditionBinding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
