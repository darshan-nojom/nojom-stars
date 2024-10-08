package com.nojom.ui.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityJobSummaryBinding;
import com.nojom.model.ProjectByID;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.io.Serializable;
import java.util.Locale;

public class JobSummaryActivity extends BaseActivity {
    private ActivityJobSummaryBinding binding;
    private JobSummaryActivityVM jobSummaryActivityVM;
    private ProjectByID projectData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_summary);
        jobSummaryActivityVM = ViewModelProviders.of(this).get(JobSummaryActivityVM.class);
        jobSummaryActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvPlaceBid.setOnClickListener(placeBidListener);
        binding.rlFiles.setOnClickListener(filesListener);
        binding.tvReportBlock.setOnClickListener(reportBlockListener);

        if (getIntent() != null) {
            projectData = (ProjectByID) getIntent().getSerializableExtra(Constants.PROJECT);
        }

        if (projectData != null) {
            binding.shimmerLayout.stopShimmer();
            binding.shimmerLayout.setVisibility(View.GONE);
            jobSummaryActivityVM.getDataMutableLiveData().setValue(projectData);
        }

        jobSummaryActivityVM.getDataMutableLiveData().observe(this, data -> {
            if (data != null) {
                setData(data);
            }
        });
    }

    View.OnClickListener placeBidListener = v -> {
        if (jobSummaryActivityVM.getDataMutableLiveData() != null && jobSummaryActivityVM.getDataMutableLiveData().getValue() != null) {
            Intent i = new Intent(JobSummaryActivity.this, PlaceBidActivity.class);
            i.putExtra(Constants.PROJECT_DATA, jobSummaryActivityVM.getDataMutableLiveData().getValue());
            startActivity(i);
        }
    };

    View.OnClickListener filesListener = v -> {
        if (jobSummaryActivityVM.getDataMutableLiveData() != null && jobSummaryActivityVM.getDataMutableLiveData().getValue() != null
                && jobSummaryActivityVM.getDataMutableLiveData().getValue().attachments != null
                && jobSummaryActivityVM.getDataMutableLiveData().getValue().attachments.size() > 0) {
            Intent i = new Intent(JobSummaryActivity.this, EmployerFilesActivity.class);
            i.putExtra(Constants.ATTACH_FILE, (Serializable) jobSummaryActivityVM.getDataMutableLiveData().getValue().attachments);
            startActivity(i);
        }
    };

    View.OnClickListener reportBlockListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (projectData != null) {
                jobSummaryActivityVM.refundPaymentReasonDialog(projectData.id);
            }
        }
    };

    private void setData(ProjectByID projectData) {
        if (projectData != null) {
            binding.tvJobTitle.setText(projectData.title);
            binding.tvDetails.setText(projectData.description);
            String budget = null;
            if (projectData.clientRateId == 0 && projectData.jobBudget != null) {
                budget = getCurrency().equals("SAR") ? projectData.jobBudget + " " + getString(R.string.sar) : getString(R.string.dollar) + projectData.jobBudget;
            } else {
                if (projectData.clientRate != null) {
                    if (projectData.clientRate.rangeTo != null && projectData.clientRate.rangeTo != 0) {
                        budget = getCurrency().equals("SAR") ? projectData.clientRate.rangeFrom + " " + getString(R.string.sar) + " - " + projectData.clientRate.rangeTo + " " + getString(R.string.sar)
                                : getString(R.string.dollar) + projectData.clientRate.rangeFrom + " - "+getString(R.string.dollar) + projectData.clientRate.rangeTo;
                    } else {
                        budget = getCurrency().equals("SAR") ? projectData.clientRate.rangeFrom + " " + getString(R.string.sar) : getString(R.string.dollar) + projectData.clientRate.rangeFrom;
                    }
                } else if (projectData.jobBudget != null) {
                    budget = getCurrency().equals("SAR") ? projectData.jobBudget + " " + getString(R.string.sar) : getString(R.string.dollar) + projectData.jobBudget;
                } else {
                    budget = getString(R.string.free);
                }
            }

//            if (!TextUtils.isEmpty(budget)) {
//                if (budget.equalsIgnoreCase(getString(R.string.free))) {
//                    binding.tvProjectBudget.setText(String.format("%s", getString(R.string.free)));
//                } else {
//                    binding.tvProjectBudget.setText(String.format(getCurrency().equals("SAR") ? getString(R.string.s_sar) : "%s", budget));
//                }
//            }

//            if (projectData.scName != null)
//                binding.tvService.setText(projectData.scName);
//            if (projectData.servicesName != null)
//                binding.tvLookingFor.setText(projectData.servicesName);
            if (projectData.attachments != null) {
                binding.tvAttachments.setText(String.format(getString(R.string._files), projectData.attachments.size()));

                if (projectData.attachments.size() == 0) {
                    binding.imgArrow.setVisibility(View.INVISIBLE);
                }
            } else {
                binding.imgArrow.setVisibility(View.INVISIBLE);
            }

            binding.tvJobId.setText(String.format(Locale.US, "%d", projectData.id));

            binding.tvClientName.setText(projectData.clientFirstName + " " + projectData.clientLastName);

            if (!TextUtils.isEmpty(projectData.deadline)) {
                String deadline = projectData.deadline.replace("T", " ");
                binding.tvDeadline.setText(Utils.setDeadLine1(this, deadline));
            } else {
                binding.tvDeadline.setText("-");
            }
        }
    }

}
