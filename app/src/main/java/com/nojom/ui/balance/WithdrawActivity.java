package com.nojom.ui.balance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.databinding.ActivityWithdrawBinding;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.Objects;

public class WithdrawActivity extends BaseActivity implements Constants {
    private ActivityWithdrawBinding binding;
    private double availableBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw);
        binding.setWithdrawAct(this);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        if (getIntent() != null) {
            availableBalance = getIntent().getDoubleExtra(AVAILABLE_BALANCE, 0);
        }

        binding.tvBalance.setText(Utils.priceWith$(Utils.getDecimalValue("" + availableBalance)));

        Utils.trackFirebaseEvent(this, "Withdraw_Balance_Screen");
    }

    public void onClickNext() {
        if (getFormatAmount() < 100) {
            binding.tvValidation.setText(getString(R.string.minimum_amount_doll_1));
        } else if (getFormatAmount() > availableBalance) {
            binding.tvValidation.setText(String.format(getString(R.string.maximum_amount_is_), Utils.priceWith$(Utils.getDecimalValue("" + availableBalance))));
        } else {
            binding.tvValidation.setText("");
            Intent i = new Intent(this, WithdrawMoneyActivity.class);
            i.putExtra(AVAILABLE_BALANCE, availableBalance);
            i.putExtra(WITHDRAW_AMOUNT, getFormatAmount());
            startActivity(i);
        }
    }

    private String getAmount() {
        return Objects.requireNonNull(binding.etBalance.getText()).toString();
    }

    private double getFormatAmount() {
        if (!TextUtils.isEmpty(getAmount()))
            return Double.parseDouble(getAmount());
        else
            return 0;
    }
}
