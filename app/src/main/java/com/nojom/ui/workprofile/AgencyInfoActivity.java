package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityAgencyInfoBinding;
import com.nojom.databinding.ActivityProfessionalInfoNewBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

public class AgencyInfoActivity extends BaseActivity implements BaseActivity.OnProfileLoadListener {

    private ActivityAgencyInfoBinding binding;
    private AgencyInfoActivityVM agencyInfoActivityVM;
    private ProfileResponse profileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_info);
        agencyInfoActivityVM = ViewModelProviders.of(this).get(AgencyInfoActivityVM.class);
        agencyInfoActivityVM.init(this);
        initData();
    }

    private void initData() {
        setOnProfileLoadListener(this);

        binding.llToolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.llToolbar.tvSave.setOnClickListener(v -> {
            if (isValid()) {
                agencyInfoActivityVM.addAgencyInfoAPI(binding.etAgencyName.getText().toString(),
                        binding.tvSummary.getText().toString(),
                        binding.tvPhone.getText().toString(),
                        binding.tvEmail.getText().toString(),
                        binding.tvWebsite.getText().toString(),
                        binding.tvAddress.getText().toString(), binding.tvNote.getText().toString(), profileData.profile_agencies.id
                        , binding.sgAgencyName.getPosition(), binding.sgAddress.getPosition(), binding.sgAbout.getPosition(),
                        binding.sgPhone.getPosition(), binding.sgEmail.getPosition(), binding.sgWebsite.getPosition(), binding.sgNote.getPosition());
            }
        });
        binding.llToolbar.tvToolbarTitle.setText(getString(R.string.agency_info));

        agencyInfoActivityVM.getIsShowProgress().observe(this, aBoolean -> {
            binding.llToolbar.progressBar.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            binding.llToolbar.tvSave.setVisibility(aBoolean ? View.INVISIBLE : View.VISIBLE);
        });

        agencyInfoActivityVM.getIsAgencyAddSuccess().observe(this, aBoolean -> {
            if (aBoolean) {
                onBackPressed();
            }
        });

        profileData = Preferences.getProfileData(this);
        refreshViews();

        setupGroups();

    }

    private void setupGroups() {
        try {
            binding.sgAgencyName.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgAgencyName));
            binding.sgAddress.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgAddress));
            binding.sgAbout.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgAbout));
            binding.sgPhone.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgPhone));
            binding.sgEmail.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgEmail));
            binding.sgWebsite.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgWebsite));
            binding.sgNote.setOnPositionChangedListener(position -> onPositionChanges(position, binding.sgNote));

//            binding.sgAgencyName.setEnabled(false);
//            binding.sgAddress.setEnabled(false);
//            binding.sgAbout.setEnabled(false);
//            binding.sgPhone.setEnabled(false);
//            binding.sgEmail.setEnabled(false);
//            binding.sgWebsite.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPositionChanges(int position, SegmentedButtonGroup segmentedButtonGroup) {
        segmentedButtonGroup.setPosition(position);
//        agencyInfoActivityVM.makePublicPrivate(segmentedButtonGroup);
    }

    private void refreshViews() {

        if (profileData != null && profileData.profile_agencies != null) {

            if (!TextUtils.isEmpty(profileData.profile_agencies.name)) {
                binding.etAgencyName.setText(profileData.profile_agencies.name);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.about)) {
                binding.tvSummary.setText(profileData.profile_agencies.about);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.phone)) {
                binding.tvPhone.setText(profileData.profile_agencies.phone);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.email)) {
                binding.tvEmail.setText(profileData.profile_agencies.email);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.address)) {
                binding.tvAddress.setText(String.format("%s", profileData.profile_agencies.address));
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.website)) {
                binding.tvWebsite.setText(profileData.profile_agencies.website);
            }

            if (!TextUtils.isEmpty(profileData.profile_agencies.note)) {
                binding.tvNote.setText(profileData.profile_agencies.note);
            }

            try {
                binding.sgAddress.setPosition(profileData.profile_agencies.address_public);
                binding.sgPhone.setPosition(profileData.profile_agencies.phone_public);
                binding.sgEmail.setPosition(profileData.profile_agencies.email_public);
                binding.sgWebsite.setPosition(profileData.profile_agencies.website_public);
                binding.sgAbout.setPosition(profileData.profile_agencies.about_public);
                binding.sgNote.setPosition(profileData.profile_agencies.note_public);
                binding.sgAgencyName.setPosition(profileData.profile_agencies.name_public);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid() {

        if (TextUtils.isEmpty(binding.etAgencyName.getText().toString().trim())) {
            toastMessage("Please enter Agency name");
            return false;
        }
        if (TextUtils.isEmpty(binding.tvSummary.getText().toString().trim())) {
            toastMessage("Please enter Agency about");
            return false;
        }
        if (TextUtils.isEmpty(binding.tvPhone.getText().toString().trim())) {
            toastMessage("Please enter Phone");
            return false;
        }
        if (TextUtils.isEmpty(binding.tvEmail.getText().toString().trim())) {
            toastMessage("Please enter Email");
            return false;
        }
        if (TextUtils.isEmpty(binding.tvWebsite.getText().toString().trim())) {
            toastMessage("Please enter Website");
            return false;
        }
        if (TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())) {
            toastMessage("Please enter Address");
            return false;
        }
        if (TextUtils.isEmpty(binding.tvNote.getText().toString().trim())) {
            toastMessage("Please enter Note");
            return false;
        }

        return true;
    }


    @Override
    public void onProfileLoad(ProfileResponse data) {
        if (data != null) {
            profileData = data;
            refreshViews();
            agencyInfoActivityVM.getIsShowProgress().postValue(false);
        }
    }
}
