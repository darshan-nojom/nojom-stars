package com.nojom.ui.gigs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.databinding.ActivityEnterRateBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.EqualSpacingItemDecoration;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EnterPriceActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {
    private ActivityEnterRateBinding binding;
    private Double rate;
    private double percent;
    public String selectedAmount = "";
    private RecyclerviewAdapter mAdapter;
    private List<String> amountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_rate);
        binding.toolbar.tvTitle.setText(getString(R.string.package_price));

        String data = getIntent().getStringExtra("data");
        percent = getIntent().getDoubleExtra("percent", 0);
        amountList = new ArrayList<>();

        amountList.add("10");
        amountList.add("25");
        amountList.add("50");
        amountList.add("100");
        amountList.add("200");
        amountList.add("250");
        amountList.add("300");
        amountList.add("500");
        amountList.add("750");
        amountList.add("1000");

        if (!TextUtils.isEmpty(data)) {
            rate = getCurrency().equals("SAR") ? Double.parseDouble(Utils.priceWithoutSAR(this,data)) : Double.parseDouble(Utils.priceWithout$(data));

            if (amountList.contains("" + data)) {
                selectedAmount = data;
                calculatePercentage(rate);
            } else {
                if (rate >= 0) {
                    binding.etPrice.setText("" + rate);
                    calculatePercentage(Double.parseDouble(binding.etPrice.getText().toString()));
                }
            }
        }
        initData();
    }

    private void initData() {

        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        binding.rvAmount.setLayoutManager(manager);
        binding.rvAmount.addItemDecoration(new EqualSpacingItemDecoration(20));

        mAdapter = new RecyclerviewAdapter((ArrayList<?>) amountList, R.layout.item_gig_amount, EnterPriceActivity.this);
        binding.rvAmount.setAdapter(mAdapter);
        binding.rvAmount.setFocusable(false);

        binding.toolbar.imgBack.setOnClickListener(v -> finish());
        binding.tvSave.setOnClickListener(v -> {
            Intent intent = new Intent();
            if (TextUtils.isEmpty(getRate())) {
                if (!TextUtils.isEmpty(selectedAmount)) {
                    intent.putExtra("data", selectedAmount);
                } else {
                    toastMessage(getString(R.string.please_select_or_enter_price));
                    return;
                }
            } else {
                try {
                    if (!getRate().equals(".") &&
                            !getRate().equals("..") &&
                            !getRate().equals("...") &&
                            !getRate().equals("....")) {
                        if (Double.parseDouble(getRate()) >= 0) {
                            intent.putExtra("data", getRate());
                        } else {
                            toastMessage(getString(R.string.price_should_not_be_negative));
                            return;
                        }
                    } else {
                        toastMessage(getString(R.string.please_enter_valid_amount));
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setResult(RESULT_OK, intent);
            finish();
        });

//        binding.rateSeekbar.setOnSeekbarChangeListener(minValue -> {
////                if (isFixedPrice) {
//            binding.tvMinValue.setText(String.format("$%s", minValue));
////                } else {
////                    binding.tvMinValue.setText(String.format("$%s/hr", minValue));
////                }
//            binding.etRate.setText(String.valueOf(minValue));
//        });

        binding.etPrice.setOnEditorActionListener((v, actionId, event) -> {
            Utils.hideSoftKeyboard(this);
            return false;
        });


        binding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString()) &&
                        !s.toString().equals(".") &&
                        !s.toString().equals("..") &&
                        !s.toString().equals("...") &&
                        !s.toString().equals("....")) {
                    try {
                        calculatePercentage(Double.parseDouble(s.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void calculatePercentage(double amount) {
        try {
            double percentAmount = Double.parseDouble(Utils.get2DecimalPlaces((amount / 100.0f) * percent));
            double finalAmountReceived = amount - percentAmount;

            binding.tvValid.setVisibility(View.VISIBLE);
            if (getCurrency().equals("SAR")) {
                binding.tvValid.setText(getString(R.string.you_will_get_total_amount) + " : " + Utils.get2DecimalPlaces(finalAmountReceived) +" "+getString(R.string.sar)+ " (" + percent + "%)");
            } else {
                binding.tvValid.setText(getString(R.string.you_will_get_total_amount) + " : "+getString(R.string.dollar) + Utils.get2DecimalPlaces(finalAmountReceived) + " (" + percent + "%)");
            }

        } catch (Exception e) {
            binding.tvValid.setText("");
            e.printStackTrace();
        }
    }

    private String getRate() {
        return binding.etPrice.getText().toString().trim();
    }

    @Override
    public void bindView(View view, int position) {
        final TextView textView = view.findViewById(R.id.tv_amount);
        String amount = amountList.get(position);
        textView.setText(getCurrency().equals("SAR") ? amount + " "+getString(R.string.sar) : getString(R.string.dollar) + amount);

        if (selectedAmount.equalsIgnoreCase(amount)) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.black_button_bg));
            textView.setTextColor(Color.WHITE);
            textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_SEMIBOLD));
        } else {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.gray_border_5));
            textView.setTextColor(getResources().getColor(R.color.black_50));
            textView.setTypeface(Typeface.createFromAsset(getAssets(), Constants.SFTEXT_REGULAR));
        }

        textView.setOnClickListener(view1 -> {
            selectedAmount = amountList.get(position);
            mAdapter.notifyDataSetChanged();
            calculatePercentage(Double.parseDouble(selectedAmount));
            binding.etPrice.setText("");
        });
    }
}
