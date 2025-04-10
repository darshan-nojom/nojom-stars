package com.nojom.ui.projects;


import static com.nojom.util.Utils.getFileNameFromUrl;
import static com.nojom.util.Utils.getTimelineDate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.adapter.OrderStarAdapter;
import com.nojom.databinding.ActivityOrderDetailsBinding;
import com.nojom.databinding.DialogTimelineBinding;
import com.nojom.model.CampList;
import com.nojom.model.Profile;
import com.nojom.model.Timeline;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;

import java.util.Objects;

public class OrderDetailActivity extends BaseActivity implements OrderStarAdapter.OnClickStarListener {
    private MyOrdersActivityVM myOrdersActivityVM;
    private ActivityOrderDetailsBinding binding;
    private CampList orderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        myOrdersActivityVM = new MyOrdersActivityVM(Task24Application.getActivity(), this);
        orderData = (CampList) getIntent().getSerializableExtra("data");
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(view -> finish());
        binding.txtSteps.setOnClickListener(view -> showTimelineDialog());

        if (orderData != null) {
            binding.tvJobTitle.setText(orderData.campaignTitle);
//            binding.tvProjectBudget.setText(orderData.totalPrice + "");
//            binding.tvPaytype.setText(orderData.currency + "");
            if (!TextUtils.isEmpty(orderData.campaignCreatedAt)) {
                String deadline = orderData.campaignCreatedAt.replace("T", " ");
                binding.tvDeadline.setText(Utils.getDateOnly(deadline, this));
                binding.tvTime.setText(Utils.getTimeFromDate(deadline, this));
            }

            binding.tvDetails.setText(orderData.campaignBrief + "");
            binding.tvJobId.setText("#" + orderData.campaignId);

            try {
                double agencyFee = orderData.totalPrice * orderData.agency_fee_rate;
                double taxTotal = (orderData.totalPrice + agencyFee) * orderData.tax_rate;

                binding.lblAgency.setText(getString(R.string.agency_fee) + " (" + Math.round(orderData.agency_fee_rate * 100) + "%)");
                binding.lblTax.setText(getString(R.string.service_fee_10_1) + " (" + Math.round(orderData.tax_rate * 100) + "%)");

                binding.tvTotal.setText(Utils.decimalFormat(String.valueOf(orderData.totalPrice)) + " " + getString(R.string.sar));
                binding.tvAgencyFee.setText(Utils.decimalFormat(String.valueOf(agencyFee)) + " " + getString(R.string.sar));
                binding.tvServiceTax.setText(Utils.decimalFormat(String.valueOf(taxTotal)) + " " + getString(R.string.sar));
                binding.tvTotalPrice.setText(Utils.decimalFormat(String.valueOf(orderData.getActualPrice())) + " " + getString(R.string.sar));


                boolean isShowReleaseButton = false, isEnableReleaseButton = true;
                if (orderData.star_details != null) {

                    if (orderData.star_details.req_status.equals("completed") && orderData.star_details.is_released == 0) {
                        isShowReleaseButton = true;
                    } else {
                        isEnableReleaseButton = false;
                    }

                }

//                if (orderData.campaignStatus != null) {
//                    if (orderData.campaignStatus.equals("in_progress")) {
//
//                    } else if (orderData.campaignStatus.equals("completed")) {
//
//                        if (isShowReleaseButton && isEnableReleaseButton) {
//
//                        } else if (!isShowReleaseButton && !isEnableReleaseButton) {
//
//                        }
//                    }
//                }
//                boolean finalIsShowReleaseButton = isShowReleaseButton;
//                boolean finalIsEnableReleaseButton = isEnableReleaseButton;

                if (orderData.campaignStatus != null) {
                    switch (orderData.campaignStatus) {
                        case "in_progress":
                        case "approved": {
                            setBlackProgress(binding.progress1);
                            setBlackProgress(binding.progress2);
                            setBlackProgress(binding.progress3);
                            binding.imgApproved.setImageResource(R.drawable.ic_approved_sel);
                            setBlackTextColor(binding.txtApproved);
                            break;
                        }
                        case "pending": {
                            break;
                        }
                        case "completed": {
                            if (orderData.star_details != null && orderData.star_details.is_released == 1) {
                                setBlackProgress(binding.progress1);
                                setBlackProgress(binding.progress2);
                                setBlackProgress(binding.progress3);
                                setBlackProgress(binding.progress4);
                                setBlackProgress(binding.progress5);
                                setBlackProgress(binding.progress6);
                                binding.imgApproved.setImageResource(R.drawable.ic_approved_sel);
                                binding.imgDeliver.setImageResource(R.drawable.ic_delivered_sel);
                                binding.imgComplete.setImageResource(R.drawable.ic_completed_sel);
                                setBlackTextColor(binding.txtApproved);
                                setBlackTextColor(binding.txtDelivered);
                                setBlackTextColor(binding.txtCompleted);
                            } else {
                                setBlackProgress(binding.progress1);
                                setBlackProgress(binding.progress2);
                                setBlackProgress(binding.progress3);
                                setBlackProgress(binding.progress4);
                                setBlackProgress(binding.progress5);
                                binding.imgApproved.setImageResource(R.drawable.ic_approved_sel);
                                binding.imgDeliver.setImageResource(R.drawable.ic_delivered_sel);
                                setBlackTextColor(binding.txtApproved);
                                setBlackTextColor(binding.txtDelivered);
                            }
                            break;
                        }
                        case "rejected":
                        case "canceled": {
                            binding.linTimeline.setVisibility(View.GONE);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (orderData.profiles != null && orderData.profiles.size() > 0) {
                OrderStarAdapter adapter = new OrderStarAdapter(this, orderData.profiles, orderData.services, this);
                binding.rvStars.setAdapter(adapter);
            }

            if (!TextUtils.isEmpty(orderData.campaignAttachmentUrl)) {
                String fName = getFileNameFromUrl(orderData.campaignAttachmentUrl);
                binding.txtFileName.setText("" + fName + " " + fName.length() + " " + getString(R.string.kb));
            }
        }

//        binding.btnRelease.setOnClickListener(view -> {
//            //release payment in case of in-progress state only
//            if (orderData.campaign_status != null && orderData.campaign_status.equals("completed")) {
//                if (finalIsShowReleaseButton && finalIsEnableReleaseButton) {
//                    campaignStarActivityVM.paymentRelease(orderData.campaign_id);
//                }
//            }
//        });

    }

    private void setBlackProgress(ProgressBar progress) {
        progress.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
    }

    private void setBlackTextColor(TextView txtApproved) {
        txtApproved.setTextColor(getResources().getColor(R.color.black));
    }

    private void setBlackTextColor1(TextView txtApproved) {
        txtApproved.setTextColor(getResources().getColor(R.color.C_020814));
    }

    @Override
    public void onClickStar(int pos, Profile profile) {
//        Intent intent = new Intent(this, CampaignStarActivity.class);
//        intent.putExtra(Constants.PROJECT, orderData);
//        intent.putExtra("profile", profile);
//        startActivity(intent);
    }

    @Override
    public void onClickChat(int pos, Profile profile) {

    }

    public void showTimelineDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        DialogTimelineBinding bindingDialog = DialogTimelineBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(bindingDialog.getRoot());
        dialog.setCancelable(true);

        bindingDialog.txtCancel.setOnClickListener(v -> dialog.dismiss());

        if (orderData.timeline != null && orderData.timeline.size() > 0) {
            for (Timeline timeline : orderData.timeline) {
                switch (timeline.prev_status) {
                    case "pending": {
                        bindingDialog.txt2Time.setText(getTimelineDate(timeline.occurred_at));
                        bindingDialog.txt2Time.setVisibility(View.VISIBLE);
                        break;
                    }
                    case "in_progress": {
                        bindingDialog.txt3Time.setText(getTimelineDate(timeline.occurred_at));
                        bindingDialog.txt3Time.setVisibility(View.VISIBLE);
                        break;
                    }
                    case "completed": {
                        bindingDialog.txt4Time.setText(getTimelineDate(timeline.occurred_at));
                        bindingDialog.txt4Time.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }

        if (orderData.campaignStatus != null) {
            switch (orderData.campaignStatus) {
                case "approved":
                case "in_progress": {
                    bindingDialog.v1.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                    bindingDialog.v2.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                    bindingDialog.img2.setImageResource(R.drawable.radio_button_active);
                    setBlueTextColor(bindingDialog.txt2);
                    setBlueTextColor(bindingDialog.txt2Desc);
                    setBlueTextColor(bindingDialog.txt2Time);
                    break;
                }
                case "pending": {
                    bindingDialog.v1.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                    setBlueTextColor(bindingDialog.txt1);
                    setBlueTextColor(bindingDialog.txt1Desc);
                    break;
                }
                case "completed": {
                    if (orderData.profiles != null && orderData.profiles.get(0).is_released == 1) {
                        bindingDialog.v1.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                        bindingDialog.v2.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                        bindingDialog.v3.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                        bindingDialog.img2.setImageResource(R.drawable.radio_button_active);
                        bindingDialog.img3.setImageResource(R.drawable.radio_button_active);
                        bindingDialog.img4.setImageResource(R.drawable.radio_button_active);
                        setBlueTextColor(bindingDialog.txt4);
                        setBlueTextColor(bindingDialog.txt4Desc);
                        setBlueTextColor(bindingDialog.txt4Time);
                    } else {
                        bindingDialog.v1.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                        bindingDialog.v2.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                        bindingDialog.v3.setBackgroundColor(getResources().getColor(R.color.C_007AFF));
                        bindingDialog.img2.setImageResource(R.drawable.radio_button_active);
                        bindingDialog.img3.setImageResource(R.drawable.radio_button_active);
                        setBlueTextColor(bindingDialog.txt3);
                        setBlueTextColor(bindingDialog.txt3Desc);
                        setBlueTextColor(bindingDialog.txt3Time);
                    }
                    break;
                }
                case "canceled":
                case "rejected": {
                    break;
                }
            }
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void setBlueTextColor(TextView txt4Desc) {
        txt4Desc.setTextColor(getResources().getColor(R.color.C_007AFF));
    }
}
