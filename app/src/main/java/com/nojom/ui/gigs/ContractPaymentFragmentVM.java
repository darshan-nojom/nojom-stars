package com.nojom.ui.gigs;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Application;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

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
                    binding.llBidding.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.GONE);
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.accept_your_job));
                    binding.tvDepositDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case Constants.BANK_TRANSFER_REVIEW:
                case Constants.WAITING_FOR_DEPOSIT:
                    binding.llBidding.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.remind_your_employer));
                    binding.tvDepositDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case 7:
                case Constants.IN_PROGRESS:
                    binding.llBidding.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);

                    if (projectData.gigStateID == 7) {
                        binding.tvPaymentText.setText(fragment.activity.getString(R.string.accept_agent_acceptance));
                    } else {
                        binding.tvPaymentText.setText(fragment.activity.getString(R.string.submit_your_job));
                    }


                    binding.llDepositDone.setVisibility(View.VISIBLE);
                    setTotal(projectData.totalPrice, binding.tvDepositDone, null);
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case Constants.SUBMIT_WAITING_FOR_PAYMENT:
                    binding.llBidding.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);

                    binding.llDepositDone.setVisibility(View.VISIBLE);
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.remind_employer_check_job));
                    setTotal(projectData.totalPrice, binding.tvDepositDone, null);
                    binding.tvReleaseDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
                    break;
                case Constants.COMPLETED:
                    binding.llBidding.setVisibility(View.VISIBLE);
                    binding.llPaymentStatus.setVisibility(View.VISIBLE);

                    binding.llDepositDone.setVisibility(View.VISIBLE);
                    binding.llReleaseDone.setVisibility(View.VISIBLE);
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.completed_paid_info));
                    setTotal(projectData.totalPrice, binding.tvReleaseDone, null);
                    binding.tvDepositDone.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", 0));
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

                    Utils.makeLinks(binding.tvPaymentText, new String[]{fragment.activity.getString(R.string.balance)
                    }, new ClickableSpan[]{balanceClick});

                    break;
                case Constants.CANCELLED:
                    binding.llBidding.setVisibility(View.VISIBLE);
                    //  llBidding.setVisibility(View.VISIBLE);
                    // tvPaymentText.setText(getString(R.string.no_payment_cancel));
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.no_payment_cancel));
                    break;

                case Constants.REFUNDED:
                    binding.llBidding.setVisibility(View.VISIBLE);
                    // llBidding.setVisibility(View.VISIBLE);
                    //tvPaymentText.setText(getString(R.string.no_payment_refunded));
                    binding.tvPaymentText.setText(fragment.activity.getString(R.string.no_payment_refunded));
                    break;
            }

//            if (projectData.jobPostBids != null)
            if (fragment.activity != null && projectData.totalPrice != 0) {
//                binding.tvTotal.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : "$%s", Utils.getDecimalValue("" + projectData.totalPrice)));

                binding.linRange.setVisibility(GONE);
                binding.linTotal.setVisibility(VISIBLE);
                binding.linDepAmnt.setVisibility(VISIBLE);
                binding.linServAmnt.setVisibility(VISIBLE);
                binding.view.setVisibility(VISIBLE);

                binding.txtDepAmnt.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", Utils.numberFormat(String.valueOf(projectData.totalPrice))));
                setTotal(projectData.totalPrice, binding.txtTotalAmnt, binding.txtServAmnt);
                binding.txtSerFee.setText(String.format(fragment.getString(R.string.service_fee_5), Utils.getDecimalValue(""+projectData.jobPostCharges.bidCharges)));
//                double val = projectData.totalPrice * 5 / 100;//TODO: its static percentage here (5%)
//                binding.txtServAmnt.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : "$%s", Utils.numberFormat(val, 2)));
//                double totalAmnt = val + projectData.totalPrice;
//                binding.txtTotalAmnt.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : "$%s", Utils.numberFormat(totalAmnt, 2)));
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

    private void setTotal(Double amount, TextView txtView, TextView txtService) {
        if (amount == null) {
            amount = 0.0;
        }

        double bidCharge = projectData.jobPostCharges.bidCharges;

        double val = amount * bidCharge / 100;//TODO: its static percentage here (5%)
        if (txtService != null) {
            txtService.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", Utils.numberFormat(val, 2)));
        }
        double totalAmnt = amount - val;
        txtView.setText(String.format(fragment.activity.getCurrency().equals("SAR") ? fragment.getString(R.string.s_sar) : fragment.getString(R.string.dollar) + "%s", Utils.numberFormat(totalAmnt, 2)));
    }
}
