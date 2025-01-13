package com.nojom.ui.projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.FragmentCampPayBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.CampList;
import com.nojom.util.Utils;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

public class CampPayFragment extends BaseFragment {
    private FragmentCampPayBinding binding;
    private CampList campList;
    private final PrettyTime p = new PrettyTime();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camp_pay, container, false);
        campList = ((CampaignDetailActivity2) activity).campList;
        renderView();
        return binding.getRoot();
    }

    private void renderView() {
        double agencyFee = campList.totalPrice * campList.agency_fee_rate;
        double taxTotal = (campList.totalPrice + agencyFee) * campList.tax_rate;

        binding.lblAgency.setText(getString(R.string.agency_fee) + " (" + Math.round(campList.agency_fee_rate * 100) + "%)");
        binding.lblTax.setText(getString(R.string.service_fee_10_1) + " (" + Math.round(campList.tax_rate * 100) + "%)");

        binding.tvTotal.setText(Utils.decimalFormat(String.valueOf(campList.totalPrice)) + " " + activity.getString(R.string.sar));
        binding.tvAgencyFee.setText(Utils.decimalFormat(String.valueOf(agencyFee)) + " " + activity.getString(R.string.sar));
        binding.tvServiceTax.setText(Utils.decimalFormat(String.valueOf(taxTotal)) + " " + activity.getString(R.string.sar));
        binding.tvTotalPrice.setText(Utils.decimalFormat(String.valueOf(campList.getActualPrice())) + " " + activity.getString(R.string.sar));

        if (campList.star_details.is_released) {
            binding.txtReleaseAmount.setText(Utils.decimalFormat(String.valueOf(campList.getActualPrice())) + " " + activity.getString(R.string.sar));
            binding.txtDepositAmount.setText(0 + " " + activity.getString(R.string.sar));
            binding.imgChkReleased.setVisibility(View.VISIBLE);
            binding.imgChkDeposit.setVisibility(View.GONE);
        } else {
            binding.txtDepositAmount.setText(Utils.decimalFormat(String.valueOf(campList.getActualPrice())) + " " + activity.getString(R.string.sar));
            binding.txtReleaseAmount.setText(0 + " " + activity.getString(R.string.sar));
            binding.imgChkReleased.setVisibility(View.GONE);
            binding.imgChkDeposit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
