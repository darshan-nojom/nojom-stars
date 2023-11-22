package com.nojom.ui.workprofile;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityInfluencerProfileBinding;
import com.nojom.ui.BaseActivity;


public class InfluencerProfileActivity extends BaseActivity {
    private InfluencerProfileActivityVM freelancerProfileActivityVM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityInfluencerProfileBinding profileBinding = DataBindingUtil.setContentView(this, R.layout.activity_influencer_profile);
        freelancerProfileActivityVM = new InfluencerProfileActivityVM(Task24Application.getActivity(), profileBinding, this);
    }

}
