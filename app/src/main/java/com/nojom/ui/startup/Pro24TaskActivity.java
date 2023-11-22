package com.nojom.ui.startup;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityPro24taskBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

public class Pro24TaskActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPro24taskBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pro_24task);
        binding.tvTitle.setText(Utils.getColorString(this, getString(R.string._24task_pro), getString(R.string.pro), R.color.red));
        binding.btn24taskPro.setText(Utils.getColorString(this, getString(R.string.become_a_24task_pro), getString(R.string.pro), R.color.red));
        binding.btn24taskPro.setOnClickListener(view -> redirectActivity(Pro24TaskDetailActivity.class));
    }
}
