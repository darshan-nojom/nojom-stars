package com.nojom.fragment.projects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Application;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

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
                    binding.llBidding.setVisibility(View.VISIBLE);

                    binding.llPaymentStatus.setVisibility(View.GONE);
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.accept_your_job));
                    break;
                case Constants.BANK_TRANSFER_REVIEW:
                case Constants.WAITING_FOR_DEPOSIT:
                    binding.llBidding.setVisibility(View.VISIBLE);

                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.remind_your_employer));
                    binding.tvDepositDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case Constants.IN_PROGRESS:
                    binding.llBidding.setVisibility(View.VISIBLE);

                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.submit_your_job));

                    if (projectData.jobPostBids != null) {
                        binding.llDepositDone.setVisibility(View.VISIBLE);
                        setTotal(projectData.jobPostBids.jpcFixedPrice, binding.tvDepositDone, null);
                    }
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                    binding.llBidding.setVisibility(View.VISIBLE);

                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    if (projectData.jobPostBids != null) {
                        binding.llDepositDone.setVisibility(View.VISIBLE);
                        binding.tvPaymentText.setText(fragment.activity.getString(R.string.remind_employer_check_job));
                        setTotal(projectData.jobPostBids.jpcFixedPrice, binding.tvDepositDone, null);
                    }
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case Constants.COMPLETED:
                    binding.llBidding.setVisibility(View.VISIBLE);

                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    if (projectData.jobPostBids != null) {
                        binding.llDepositDone.setVisibility(View.VISIBLE);
                        binding.llReleaseDone.setVisibility(View.VISIBLE);
                        binding.tvPaymentText.setText(fragment.activity.getString(R.string.completed_paid_info));
                        setTotal(projectData.jobPostBids.jpcFixedPrice, binding.tvReleaseDone, null);

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

                        Utils.makeLinks(binding.tvPaymentText, new String[]{fragment.getString(R.string.balance)}, new ClickableSpan[]{balanceClick});
                    }
                    binding.tvDepositDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case Constants.CANCELLED:
                    binding.llBidding.setVisibility(View.VISIBLE);

                    //  llBidding.setVisibility(View.VISIBLE);
                    // tvPaymentText.setText(getString(R.string.no_payment_cancel));
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.no_payment_cancel));
                    binding.tvDepositDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;

                case Constants.REFUNDED:
                    binding.llBidding.setVisibility(View.VISIBLE);

                    // llBidding.setVisibility(View.VISIBLE);
                    //tvPaymentText.setText(getString(R.string.no_payment_refunded));
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.no_payment_refunded));
                    binding.tvDepositDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
            }

            if (projectData.jobPostBids != null) if (fragment.activity != null) {
//                binding.tvTotal.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : "$%s", projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue("" + projectData.jobPostBids.jpcFixedPrice) : 0));
//                    binding.tvTotalBid.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : "$%s",
//                            projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue("" + projectData.jobPostBids.jpcFixedPrice) : 0));
                hideShowView(true);
                if (projectData.jobPostBids.jpcFixedPrice == null) {
                    binding.txtDepAmnt.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                } else {
                    binding.txtDepAmnt.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", Utils.numberFormat(String.valueOf(projectData.jobPostBids.jpcFixedPrice))));
                }
                setTotal(projectData.jobPostBids.jpcFixedPrice, binding.txtTotalAmnt, binding.txtServAmnt);
                binding.txtSerFee.setText(String.format(fragment.getString(R.string.service_fee_5), Utils.getDecimalValue("" + projectData.jobPostCharges.bidCharges)));
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

    private void setTotal(Double amount, TextView txtView, TextView txtService) {
        if (amount == null) {
            amount = 0.0;
        }
        double val = amount * projectData.jobPostCharges.bidCharges / 100;//TODO: its static percentage here (5%)
        if (txtService != null) {
            txtService.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", Utils.numberFormat(String.valueOf(val))));
        }
        double totalAmnt = amount - val;
        txtView.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", Utils.numberFormat(String.valueOf(totalAmnt))));
    }

    private void hideShowView(boolean isFeeVisible) {
        if (isFeeVisible) {
            binding.linRange.setVisibility(GONE);
            binding.linTotal.setVisibility(VISIBLE);
            binding.linDepAmnt.setVisibility(VISIBLE);
            binding.linServAmnt.setVisibility(VISIBLE);
            binding.view.setVisibility(VISIBLE);
        } else {
            binding.linRange.setVisibility(VISIBLE);
            binding.linTotal.setVisibility(GONE);
            binding.linDepAmnt.setVisibility(GONE);
            binding.linServAmnt.setVisibility(GONE);
            binding.view.setVisibility(GONE);
        }
    }
}
