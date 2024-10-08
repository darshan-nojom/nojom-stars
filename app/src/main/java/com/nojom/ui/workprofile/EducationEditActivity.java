package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.EducationAdapter;
import com.nojom.databinding.ActivityEducationEditBinding;
import com.nojom.model.Education;
import com.nojom.ui.BaseActivity;

public class EducationEditActivity extends BaseActivity {
    private EducationEditActivityVM educationEditActivityVM;
    private ActivityEducationEditBinding binding;
    private EducationAdapter educationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_education_edit);
        educationEditActivityVM = ViewModelProviders.of(this).get(EducationEditActivityVM.class);
        initData();
        educationEditActivityVM.init(this);
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvEditCancel.setVisibility(View.GONE);
        binding.toolbar.imgBack.setVisibility(View.VISIBLE);
        binding.toolbar.tvToolbarTitle.setText(getString(R.string.education));

        binding.toolbar.tvSave.setOnClickListener(v -> {
            if (educationEditActivityVM.isValid(binding)) {
                educationEditActivityVM.addEducation();
            }
        });

        binding.tvAddEducation.setOnClickListener(v -> {
            if (educationAdapter != null) {
                educationAdapter.addEducation(new Education());
            }
        });

        binding.toolbar.rlEdit.setVisibility(View.VISIBLE);

        binding.rvEducation.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEducation.setItemAnimator(new DefaultItemAnimator());

        educationAdapter = new EducationAdapter(this);
        binding.rvEducation.setAdapter(educationAdapter);

        educationEditActivityVM.getDataList().observe(this, educations -> educationAdapter.doRefresh(educations));

        educationEditActivityVM.getIsShowProgress().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.toolbar.tvSave.setVisibility(View.INVISIBLE);
                binding.toolbar.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.toolbar.tvSave.setVisibility(View.VISIBLE);
                binding.toolbar.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        educationEditActivityVM.showDialog();
    }
}
