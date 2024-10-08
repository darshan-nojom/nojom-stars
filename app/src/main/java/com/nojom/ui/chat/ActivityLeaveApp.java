package com.nojom.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityLeaveAppBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

public class ActivityLeaveApp extends BaseActivity {

    private ActivityLeaveAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_app);
        initData();
    }

    private void initData() {
        String url = getIntent().getStringExtra("link");
        if (!TextUtils.isEmpty(url)) {
            binding.txtLink.setText(url);
        }

        String s = getString(R.string.to_find_out_how_to_identify_suspicious_links_n_read_this_guide);
        int[] colorList = {R.color.c_0FA35D};
        String[] words = {"read this guide"};
        binding.txtInfo.setText(Utils.getBoldString(this, s, null, colorList, words));

        binding.imgBack.setOnClickListener(view -> onBackPressed());
        binding.txtInfo.setOnClickListener(view -> startActivity(new Intent(this, ActivityTerms.class)));
    }
}
