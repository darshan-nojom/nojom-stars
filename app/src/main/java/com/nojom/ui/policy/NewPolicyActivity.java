package com.nojom.ui.policy;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityNewPolicyBinding;
import com.nojom.ui.BaseActivity;

public class NewPolicyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNewPolicyBinding newPolicyBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_policy);
        new NewPolicyActivityVM(Task24Application.getActivity(), newPolicyBinding, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }
}
