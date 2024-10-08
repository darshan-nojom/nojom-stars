package com.nojom.ui.partner;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityPartnerProfileBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.WorkProfileActivity;
import com.nojom.util.Utils;

public class PartnerProfileActivity extends BaseActivity {
    ActivityPartnerProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_partner_profile);

        initData();
    }

    private void initData() {

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        if (getProfileData().percentage != null) {
            if (Math.round(getProfileData().percentage.totalPercentage) == 100) {
                binding.relCompleteProfile.setVisibility(View.INVISIBLE);
                binding.txtLaststep.setVisibility(View.INVISIBLE);
            }
            String profilePercentage = Math.round(getProfileData().percentage.totalPercentage) + "%";
            binding.tvProfileComplete.setText(Utils.getColorString(PartnerProfileActivity.this,
                    getString(R.string.gig_profile_is, profilePercentage), profilePercentage, R.color.red));
        } else {
            binding.relCompleteProfile.setVisibility(View.INVISIBLE);
            binding.txtLaststep.setVisibility(View.INVISIBLE);
        }

        binding.relCompleteProfile.setOnClickListener(v -> redirectActivity(WorkProfileActivity.class));
    }

}
