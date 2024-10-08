package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.WorkAdapter;
import com.nojom.databinding.ActivityEmploymentEditBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Work;
import com.nojom.ui.BaseActivity;

public class EmploymentEditActivity extends BaseActivity {

    private EmploymentEditActivityVM employmentEditActivityVM;
    private ActivityEmploymentEditBinding binding;
    private WorkAdapter workAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_employment_edit);
        employmentEditActivityVM = ViewModelProviders.of(this).get(EmploymentEditActivityVM.class);
        initData();
        employmentEditActivityVM.init(this);
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvEditCancel.setVisibility(View.GONE);
        binding.toolbar.imgBack.setVisibility(View.VISIBLE);
        binding.toolbar.tvToolbarTitle.setText(getString(R.string.employment));

        binding.toolbar.tvSave.setOnClickListener(v -> {
            if (employmentEditActivityVM.isValid(binding)) {
                employmentEditActivityVM.addWork();
            }
        });

        binding.tvAddWork.setOnClickListener(v -> {
            if (workAdapter != null) {
                workAdapter.addWork(new Work());
            }
        });

        binding.toolbar.rlEdit.setVisibility(View.VISIBLE);

        binding.rvWork.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWork.setItemAnimator(new DefaultItemAnimator());

        workAdapter = new WorkAdapter(this);
        binding.rvWork.setAdapter(workAdapter);

        employmentEditActivityVM.getWorkDataList().observe(this, works -> workAdapter.doRefresh(works));

        setOnProfileLoadListener(this::onProfileLoad);

        employmentEditActivityVM.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            if (isShowProgress) {
                binding.toolbar.tvSave.setVisibility(View.INVISIBLE);
                binding.toolbar.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.toolbar.tvSave.setVisibility(View.VISIBLE);
                binding.toolbar.progressBar.setVisibility(View.GONE);
            }

        });
    }

    public void onProfileLoad(ProfileResponse data) {
        finish();
    }

    @Override
    public void onBackPressed() {
        employmentEditActivityVM.showDialog();
    }

}
