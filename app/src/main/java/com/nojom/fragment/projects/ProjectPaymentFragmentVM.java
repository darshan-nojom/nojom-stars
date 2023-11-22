package com.nojom.fragment.projects;

import android.app.Application;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nojom.R;
import com.nojom.databinding.FragmentProjectPaymentBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProjectByID;
import com.nojom.ui.balance.BalanceActivity;
import com.nojom.ui.policy.TermsConditionActivity;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

class ProjectPaymentFragmentVM extends AndroidViewModel {
    private FragmentProjectPaymentBinding binding;
    private BaseFragment fragment;
    private ProjectByID projectData;

    ProjectPaymentFragmentVM(Application application, FragmentProjectPaymentBinding projectPaymentBinding, BaseFragment projectPaymentFragment) {
        super(application);
        binding = projectPaymentBinding;
        fragment = projectPaymentFragment;
        initData();
    }

    private void initData() {
        if (fragment.activity != null) {
            projectData = ((ProjectDetailsActivity) fragment.activity).getProjectData();
        }

        if (projectData != null && projectData.jobPostStateId != null) {
            switch (projectData.jobPostStateId) {
                case Constants.BIDDING:
                    binding.llBidding.setVisibility(View.VISIBLE);
                    break;
                case Constants.WAITING_FOR_ACCEPTANCE:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.GONE);
                    binding.tvNoDeposit.setText(fragment.activity.getString(R.string.accept_your_job));
                    break;
                case Constants.WAITING_FOR_DEPOSIT:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    binding.tvNoDeposit.setText(fragment.activity.getString(R.string.remind_your_employer));
                    break;
                case Constants.IN_PROGRESS:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    binding.tvNoDeposit.setText(fragment.activity.getString(R.string.submit_your_job));

                    if (projectData.jobPostBids != null) {
                        binding.llDepositDone.setVisibility(View.VISIBLE);
                        binding.tvDepositDone.setText(String.format("$%s", projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue("" + projectData.jobPostBids.jpcFixedPrice) : 0));
                    }
                    break;
                case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    if (projectData.jobPostBids != null) {
                        binding.llDepositDone.setVisibility(View.VISIBLE);
                        binding.tvNoDeposit.setText(fragment.activity.getString(R.string.remind_employer_check_job));
                        binding.tvDepositDone.setText(String.format("$%s", projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue("" + projectData.jobPostBids.jpcFixedPrice) : 0));
                    }
                    break;
                case Constants.COMPLETED:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    if (projectData.jobPostBids != null) {
                        binding.llDepositDone.setVisibility(View.VISIBLE);
                        binding.llReleaseDone.setVisibility(View.VISIBLE);
                        binding.tvNoDeposit.setText(fragment.activity.getString(R.string.completed_paid_info));
                        binding.tvReleaseDone.setText(String.format("$%s", projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue("" + projectData.jobPostBids.jpcFixedPrice) : 0));

                        ClickableSpan balanceClick = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View view) {
                                fragment.activity.redirectActivity(BalanceActivity.class);
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(true);
                            }
                        };

                        Utils.makeLinks(binding.tvNoDeposit, new String[]{fragment.getString(R.string.balance)}, new ClickableSpan[]{balanceClick});
                    }
                    break;
                case Constants.CANCELLED:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    //  llBidding.setVisibility(View.VISIBLE);
                    // tvPaymentText.setText(getString(R.string.no_payment_cancel));
                    binding.tvNoDeposit.setText(fragment.activity.getString(R.string.no_payment_cancel));
                    break;

                case Constants.REFUNDED:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    // llBidding.setVisibility(View.VISIBLE);
                    //tvPaymentText.setText(getString(R.string.no_payment_refunded));
                    binding.tvNoDeposit.setText(fragment.activity.getString(R.string.no_payment_refunded));
                    break;
            }

            if (projectData.jobPostBids != null)
                if (fragment.activity != null) {
                    binding.tvTotal.setText(String.format("$%s", projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue("" + projectData.jobPostBids.jpcFixedPrice) : 0));
                    binding.tvTotalBid.setText(String.format("$%s", projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue("" + projectData.jobPostBids.jpcFixedPrice) : 0));
                }
        }

        ClickableSpan tandc = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                fragment.activity.redirectUsingCustomTab(Constants.TERMS_USE);
                fragment.startActivity(new Intent(fragment.activity, TermsConditionActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        Utils.makeLinks(binding.tvTnc, new String[]{fragment.getString(R.string.terms_and_conditions)}, new ClickableSpan[]{tandc});

    }
}
