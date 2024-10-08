package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityWithdrawDetailBinding;
import com.nojom.model.ContractDetails;
import com.nojom.model.ProfileResponse;
import com.nojom.model.ProjectByID;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.Locale;

public class WithdrawDetailActivity extends BaseActivity {
    private ActivityWithdrawDetailBinding binding;
    private ProfileResponse profileData;

    private ProjectByID projectData;
    private ContractDetails contractData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw_detail);
        initData();
    }

    private void initData() {

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvSave.setVisibility(View.GONE);
        binding.toolbar.tvToolbarTitle.setText(getString(R.string.withdraw_details));

        if (getIntent() != null) {
            projectData = (ProjectByID) getIntent().getSerializableExtra(Constants.PROJECT);
            contractData = (ContractDetails) getIntent().getSerializableExtra(Constants.PROJECT);

            if (projectData != null) {
                binding.tvDetails.setText("" + projectData.description);
                binding.tvCid.setText(String.format(Locale.US, "%d", projectData.id));
                binding.tvBn.setText(projectData.clientFirstName + " " + projectData.clientLastName);
                binding.tvStatus.setText(projectData.status == 1 ? getString(R.string.done) : getString(R.string.pending));

                String budget;
                if (projectData.clientRateId == 0) {
                    budget = getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.jobBudget) + " " + getString(R.string.sar) : getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.jobBudget);
                } else {
                    if (projectData.clientRate != null) {
                        if (projectData.clientRate.rangeTo != null && projectData.clientRate.rangeTo != 0) {
                            budget = getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.clientRate.rangeFrom) + " " + getString(R.string.sar) + " - " + Utils.getDecimalValue("" + projectData.clientRate.rangeTo) + " " + getString(R.string.sar) : getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.clientRate.rangeFrom) + " - $" + Utils.getDecimalValue("" + projectData.clientRate.rangeTo);
                        } else {
                            budget = getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.clientRate.rangeFrom) + " " + getString(R.string.sar) : getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.clientRate.rangeFrom);
                        }
                    } else if (projectData.jobBudget != null) {
                        budget = getCurrency().equals("SAR") ? Utils.getDecimalValue("" + projectData.jobBudget) + " " + getString(R.string.sar) : getString(R.string.dollar) + Utils.getDecimalValue("" + projectData.jobBudget);
                    } else {
                        budget = getString(R.string.free);
                    }
                }

                if (!TextUtils.isEmpty(budget)) binding.tvProjectBudget.setText(budget);
            } else if (contractData != null) {
                binding.tvDetails.setText("" + contractData.gigDescription);
                binding.tvCid.setText(String.format(Locale.US, "%d", contractData.id));
                binding.tvBn.setText("-");
                binding.tvStatus.setText(contractData.status ? getString(R.string.done) : getString(R.string.pending));

                binding.tvProjectBudget.setText(getCurrency().equals("SAR") ? Utils.getDecimalValue("" + contractData.totalPrice) + " " + getString(R.string.sar)
                        : getString(R.string.dollar) + Utils.getDecimalValue("" + contractData.totalPrice));

            }
        }

        profileData = Preferences.getProfileData(this);

    }


    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishToRight();
    }
}
