package com.nojom.ui.jobs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.nojom.R;
import com.nojom.databinding.ActivityPlaceBidBinding;
import com.nojom.model.ProjectByID;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

public class PlaceBidActivity extends BaseActivity {

    private PlaceBidActivityVM placeBidActivityVM;
    private ActivityPlaceBidBinding binding;
    private ProjectByID projectData;
    private boolean isEdit;
    private PowerMenu powerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_bid);
        placeBidActivityVM = ViewModelProviders.of(this).get(PlaceBidActivityVM.class);
        placeBidActivityVM.init(this);
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableEnableTouch(false);
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.tvPlaceBid.setOnClickListener(placeBidListener);
        binding.tvDeadlineType.setOnClickListener(deadlineTypeListener);

        if (getIntent() != null) {
            isEdit = getIntent().getBooleanExtra(Constants.IS_EDIT, false);
            projectData = (ProjectByID) getIntent().getSerializableExtra(Constants.PROJECT_DATA);
            if (projectData != null)
                binding.tvProjectTitle.setText(projectData.title);
        }

        binding.etAmount.addTextChangedListener(textChangeListener);

        if (isEdit && projectData != null) {
            binding.etAmount.setText(String.format("%s", projectData.jobPostBids.jpcFixedPrice != null ? Utils.getDecimalValue(projectData.jobPostBids.jpcFixedPrice.toString()) : Utils.getDecimalValue(projectData.jobPostBids.amount.toString())));
            binding.tvCurrency.setText(projectData.jobPostBids.currency);
            binding.tvDeadlineType.setText(projectData.jobPostBids.deadlineType);
            binding.etDeadlineValue.setText(String.format("%d", projectData.jobPostBids.deadlineValue));
            binding.etDescribeBid.setText(projectData.jobPostBids.message);

            //binding.tvPlaceBid.setText(getString(R.string.save_bid));
        }

        placeBidActivityVM.getValidateError().observe(this, isShowError -> {
            if (isShowError && projectData != null) {
                binding.tvBidAmountFee.setText(String.format(getString(R.string.minimum_bid_amount_should_be_s), Double.valueOf(get2DecimalPlaces(projectData.jobPostCharges.bidDollarCharges))));
            }
        });

        placeBidActivityVM.getIsLoading().observe(this, isLoading -> {
            disableEnableTouch(isLoading);

            binding.tvPlaceBid.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        Utils.trackFirebaseEvent(this, "Place_Bid_Screen");

        binding.etDescribeBid.setOnTouchListener((v, event) -> {
            if (binding.etDescribeBid.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });
    }

    private String getBidText() {
        return binding.etDescribeBid.getText().toString().trim();
    }

    private String getCurrency() {
        return binding.tvCurrency.getText().toString().trim();
    }

    private String getAmount() {
        return binding.etAmount.getText().toString().trim();
    }

    private String getDeadlineType() {
        return binding.tvDeadlineType.getText().toString().trim();
    }

    private String getDeadlineValue() {
        return binding.etDeadlineValue.getText().toString().trim();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String proposalText = data.getStringExtra(Constants.PROPOSAL);
            if (!isEmpty(proposalText)) {
                binding.etDescribeBid.setText(proposalText);
            }
        }
    }

    View.OnClickListener placeBidListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (projectData != null) {
                if (placeBidActivityVM.isValid(getAmount(), getDeadlineValue(), getBidText(), projectData)) {
                    placeBidActivityVM.placeBid(projectData, getAmount(), isEdit, getBidText(), getCurrency(), getDeadlineType(),
                            getDeadlineValue());
                }
            }
        }
    };

    View.OnClickListener deadlineTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Typeface tf = Typeface.createFromAsset(getAssets(), Constants.SFTEXT_REGULAR);
            String deadlineType = binding.tvDeadlineType.getText().toString();
            PowerMenuItem days, hours;
            days = new PowerMenuItem("" + getString(R.string.days), deadlineType.equals(getString(R.string.days)));
            hours = new PowerMenuItem(getString(R.string.hours), !deadlineType.equals(getString(R.string.days)));

            powerMenu = new PowerMenu.Builder(PlaceBidActivity.this)
                    .addItem(days)
                    .addItem(hours)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                    .setMenuRadius(10f)
                    .setMenuShadow(10f)
                    .setTextColor(ContextCompat.getColor(PlaceBidActivity.this, R.color.black))
                    .setTextGravity(Gravity.CENTER)
                    .setTextTypeface(tf)
                    .setSelectedTextColor(Color.WHITE)
                    .setMenuColor(Color.WHITE)
                    .setSelectedMenuColor(ContextCompat.getColor(PlaceBidActivity.this, R.color.colorPrimary))
                    .setOnMenuItemClickListener(onMenuItemClickListener)
                    .build();

            powerMenu.showAsDropDown(binding.tvDeadlineType);
        }
    };

    private final OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            powerMenu.setSelectedPosition(position);
            powerMenu.dismiss();
            binding.tvDeadlineType.setText(item.getTitle());
        }
    };

    TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {

                if (getAmount().startsWith(".")) {
                    toastMessage(getString(R.string.an_entered_amount_invalid));
                    return;
                }
                if (projectData == null) {
                    return;
                }
                if (projectData.jobPayTypeId != null && projectData.jobPayTypeId == 5/*Free*/) {
                    return;
                }
                if (projectData.jobPostCharges != null && !isEmpty(getAmount()) && Double.parseDouble(getAmount()) < projectData.jobPostCharges.bidDollarCharges) {
                    binding.tvBidAmountFee.setText(String.format(getString(R.string.minimum_bid_amount_should_be_), Double.valueOf(get2DecimalPlaces(projectData.jobPostCharges.bidDollarCharges))));
                    return;
                }
                if (!isEmpty(getAmount()) && projectData != null) {
                    //double bidFee = projectData.jobPostContracts.bidCharges;
                    double bidDollarCharges = projectData.jobPostCharges != null ? projectData.jobPostCharges.bidDollarCharges : 0;

                    double bidPercFee = projectData.jobPostCharges != null ? projectData.jobPostCharges.bidPercentCharges : 0;


                    double percentage = (Double.parseDouble(getAmount()) * bidPercFee) / 100;
                    double total = 0;

                    if (percentage > bidDollarCharges) {
                        total = Double.parseDouble(getAmount()) - percentage;
                    } else {
                        total = Double.parseDouble(getAmount()) - bidDollarCharges;
                    }

                    binding.tvBidAmountFee.setText(String.format(getString(R.string.you_will_get_total_amount) + ": $%s (%s fee)", get2DecimalPlaces(total), get2DecimalPlaces(percentage > bidDollarCharges ? bidPercFee + "%" : "$" + bidDollarCharges)));
                } else {
                    binding.tvBidAmountFee.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
