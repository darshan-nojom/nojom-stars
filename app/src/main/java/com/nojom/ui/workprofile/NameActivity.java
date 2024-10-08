package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityNameBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.Objects;

import retrofit2.Response;

public class NameActivity extends BaseActivity implements ResponseListener, BaseActivity.OnProfileLoadListener {
    private NameActivityVM nameActivityVM;
    private ActivityNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_name);
        nameActivityVM = ViewModelProviders.of(this).get(NameActivityVM.class);
        binding.setNameActivity(this);
        nameActivityVM.setNameActivityListener(this);
        setOnProfileLoadListener(this);
        initData();
    }

    private void initData() {
        binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        binding.toolbar.progress.setProgress(0);
        binding.toolbar.imgBack.setVisibility(View.GONE);
        Utils.trackFirebaseEvent(this, "First_Last_Name_Screen");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    public void onClickNext() {
        if (nameActivityVM.isValid(this, getFirstName(), getLastName())) {
            binding.btnNext.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            nameActivityVM.updateName(this, getFirstName(), getLastName(),RS_2_LOCATION);
        }
    }

    public String getFirstName() {
        return Objects.requireNonNull(binding.etFirstname.getText()).toString().trim();
    }

    public String getLastName() {
        return Objects.requireNonNull(binding.etLastname.getText()).toString().trim();
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        getProfile();
    }

    @Override
    public void onError() {
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        //redirectActivity(SelectExpertiseActivity.class);
        gotoMainActivity(Constants.TAB_HOME);
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }
}
