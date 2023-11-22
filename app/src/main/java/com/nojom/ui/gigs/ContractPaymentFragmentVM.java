package com.nojom.ui.gigs;

import android.app.Application;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nojom.R;
import com.nojom.databinding.FragmentProjectPaymentBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;
import com.nojom.ui.balance.BalanceActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

class ContractPaymentFragmentVM extends AndroidViewModel {
    private FragmentProjectPaymentBinding binding;
    private BaseFragment fragment;
    private ContractDetails projectData;

    ContractPaymentFragmentVM(Application application, FragmentProjectPaymentBinding projectPaymentBinding, BaseFragment projectPaymentFragment) {
        super(application);
        binding = projectPaymentBinding;
        fragment = projectPaymentFragment;
        initData();
    }

    private void initData() {
        if (fragment.activity != null) {
            projectData = ((ContractDetailsActivity) fragment.activity).getProjectData();
        }

        if (projectData != null && projectData.gigStateID != 0) {
            switch (projectData.gigStateID) {
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
                case 7:
                case Constants.IN_PROGRESS:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);

                    if (projectData.gigStateID == 7) {
                        binding.tvNoDeposit.setText(fragment.activity.getString(R.string.accept_agent_acceptance));
                    } else {
                        binding.tvNoDeposit.setText(fragment.activity.getString(R.string.submit_your_job));
                    }


                    binding.llDepositDone.setVisibility(View.VISIBLE);
                    binding.tvDepositDone.setText(String.format("$%s", Utils.getDecimalValue("" + projectData.totalPrice)));
                    break;
                case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);

                    binding.llDepositDone.setVisibility(View.VISIBLE);
                    binding.tvNoDeposit.setText(fragment.activity.getString(R.string.remind_employer_check_job));
                    binding.tvDepositDone.setText(String.format("$%s", Utils.getDecimalValue("" + projectData.totalPrice)));

                    break;
                case Constants.COMPLETED:
                    binding.llProjectStatus.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);

                    binding.llDepositDone.setVisibility(View.VISIBLE);
                    binding.llReleaseDone.setVisibility(View.VISIBLE);
                    binding.tvNoDeposit.setText(fragment.activity.getString(R.string.completed_paid_info));
                    binding.tvReleaseDone.setText(String.format("$%s", Utils.getDecimalValue("" + projectData.totalPrice)));

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

                    Utils.makeLinks(binding.tvNoDeposit, new String[]{fragment.activity.getString(R.string.balance)
                    }, new ClickableSpan[]{balanceClick});

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

//            if (projectData.jobPostBids != null)
            if (fragment.activity != null && projectData.totalPrice != 0) {
                binding.tvTotal.setText(String.format("$%s", Utils.getDecimalValue("" + projectData.totalPrice)));
            }
        }

        ClickableSpan tandc = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                fragment.activity.redirectUsingCustomTab(Constants.TERMS_USE);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        Utils.makeLinks(binding.tvTnc, new String[]{fragment.activity.getString(R.string.terms_and_conditions)}, new ClickableSpan[]{tandc});

    }
}
