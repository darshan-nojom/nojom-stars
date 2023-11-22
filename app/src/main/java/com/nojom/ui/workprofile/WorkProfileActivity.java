package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityWorkProfileBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

public class WorkProfileActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener {

    private ActivityWorkProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_work_profile);
        binding.setActivity(this);
        initData();
    }

    private void initData() {
        binding.toolbar.tvTitle.setText(getString(R.string.account));

        setOnProfileLoadListener(this);

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        Utils.trackFirebaseEvent(this, "Open_Profile_Screen");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ProfileResponse profileData = Preferences.getProfileData(this);
        if (profileData != null) {
            onProfileLoad(profileData);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickPrivateInfo() {
        redirectActivity(PrivateInfoActivity.class);
    }

    public void onClickMyProfile() {
        redirectActivity(InfluencerProfileActivity.class);
    }

    public void onClickSkill() {
        redirectActivity(EditSkillsActivity.class);
    }

    public void onCliCkProfInfo() {
        redirectActivity(NewProfessionalInfoActivity.class);
    }

    public void onCliCkAgencyInfo() {
        redirectActivity(AgencyInfoActivity.class);
    }

    public void onClickMyPortfolio() {
        redirectActivity(PortfolioListActivity.class);

    }

    public void onClickMyPlatform() {
        redirectActivity(MyPlatformActivity.class);

    }

    public void onClickAvailability() {
        Intent i = new Intent(this, EditAvailabilityActivity.class);
        startActivity(i);
        openToLeft();
    }

    public void onClickMyPayRate() {
        Intent i = new Intent(this, EditRateActivity.class);
        startActivity(i);
        openToLeft();
    }

    public void onClickVerification() {
        redirectActivity(VerificationActivity.class);
    }

    @Override
    public void onProfileLoad(ProfileResponse profileData) {
        if (profileData.percentage != null) {
            try {
                String profilePercentage = Math.round(profileData.percentage.totalPercentage) + "%";
                binding.tvProfileComplete.setText(Utils.getColorString(this,
                        getString(R.string.your_profile_is, profilePercentage), profilePercentage, R.color.red));

                setPercentage(profileData.percentage.privateInfo != null ? profileData.percentage.privateInfo : 0, binding.tvPrivateInfo);
                setPercentage(profileData.percentage.professionalInfo != null ? profileData.percentage.professionalInfo : 0, binding.tvProfessionalInfo);
                setPercentage(profileData.percentage.skill != null ? profileData.percentage.skill : 0, binding.tvSkill);
                setPercentage(profileData.percentage.verification != null ? profileData.percentage.verification : 0, binding.tvVerifications);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            binding.tvProfileComplete.setText(getString(R.string.your_profile_is_incomplete));
        }
    }

    @SuppressLint("DefaultLocale")
    private void setPercentage(int percentage, TextView textView) {
        if (percentage == 100) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.green_rounded_corner));
        } else if (percentage > 50) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.orange_rounded_corner));
        } else {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.red_rounded_corner));
        }

        textView.setText(String.format("%s%%", Utils.nFormate(percentage)));
    }

}
