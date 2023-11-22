package com.nojom.ui.balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.apis.GetPaymentAccountAPI;
import com.nojom.databinding.ActivityWithdrawMoneyBinding;
import com.nojom.model.Payment;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;

public class WithdrawMoneyActivity extends BaseActivity {

    private WithdrawMoneyActivityVM withdrawMoneyActivityVM;
    private ActivityWithdrawMoneyBinding binding;
    private double withdrawBalance;
    private double availableBalance;
    private int accountId = 0;
    private int REQ_ACCOUNT_ID = 101;
    private int REQ_ACCOUNTS = 102;
    private GetPaymentAccountAPI getPaymentAccountAPI;
    private ArrayList<Payment> paymentList;
    private boolean isForList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw_money);
        withdrawMoneyActivityVM = ViewModelProviders.of(this).get(WithdrawMoneyActivityVM.class);
        getPaymentAccountAPI = new GetPaymentAccountAPI();
        getPaymentAccountAPI.init(this);
        initData();
    }

    private void initData() {
        paymentList = new ArrayList<>();
        if (getIntent() != null) {
            withdrawBalance = getIntent().getDoubleExtra(Constants.WITHDRAW_AMOUNT, 0);
            availableBalance = getIntent().getDoubleExtra(Constants.AVAILABLE_BALANCE, 0);
        }

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        getPaymentAccountAPI.getAccounts();

        binding.rlPaypal.setOnClickListener(v -> {
            if (paymentList != null && paymentList.size() > 0) {
                Intent i = new Intent(WithdrawMoneyActivity.this, ChooseAccountActivity.class);
                i.putExtra(Constants.ACCOUNTS, paymentList);
                i.putExtra(Constants.ACCOUNT_ID, accountId);
                startActivityForResult(i, REQ_ACCOUNT_ID);
            } else {
                isForList = true;
                getPaymentAccountAPI.getAccounts();
            }
        });

        binding.btnWithdraw.setOnClickListener(v -> {
            if (accountId != 0)
                withdrawMoneyActivityVM.addWithdraw(this, accountId, withdrawBalance);
            else
                toastMessage(getString(R.string.please_select_your_account_first));
        });

        binding.tvBalance.setText(Utils.currencyFormat(withdrawBalance).replace("$", ""));
        binding.tvRemainingBalance.setText(String.format(getString(R.string.ramaining_balance_), Utils.currencyFormat(availableBalance - withdrawBalance)));
        binding.tvAvailableBalance.setText(String.format(getString(R.string._usd), Utils.currencyFormat(availableBalance)));

        withdrawMoneyActivityVM.getOnClickDialog().observe(this, aBoolean -> {
            Intent i = new Intent(WithdrawMoneyActivity.this, BalanceActivity.class);
            i.putExtra(Constants.TAB_BALANCE, Constants.BALANCE_WITHDRAW);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finishToRight();
        });

        withdrawMoneyActivityVM.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            if (isShowProgress) {
                binding.btnWithdraw.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.btnWithdraw.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        getPaymentAccountAPI.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            if (isShowProgress) {
                binding.rlPaypal.setBackgroundResource(R.drawable.transp_rounded_corner_10);
                binding.progressBarTo.setVisibility(View.VISIBLE);
            } else {
                binding.rlPaypal.setBackgroundResource(R.drawable.white_rounded_corner_10);
                binding.progressBarTo.setVisibility(View.GONE);
            }
        });

        getPaymentAccountAPI.getListMutableLiveData().observe(this, (Observer<ArrayList<Payment>>) payments -> {
            if (payments != null) {
                paymentList = payments;
                for (Payment payment : payments) {
                    if (payment.isPrimary.equalsIgnoreCase("1")) {
                        binding.tvProvider.setText(String.format("%s:", payment.provider));
                        binding.tvPaypalEmail.setText(payment.account);
                        accountId = payment.id;
                        break;
                    }
                }

                if (isForList) {
                    Intent i = new Intent(WithdrawMoneyActivity.this, ChooseAccountActivity.class);
                    i.putExtra(Constants.ACCOUNTS, (ArrayList<Payment>) payments);
                    i.putExtra(Constants.ACCOUNT_ID, accountId);
                    startActivityForResult(i, REQ_ACCOUNT_ID);
                    isForList = false;
                }
            }
        });

        getPaymentAccountAPI.getMessage().observe(this, message -> {
            if (message.equalsIgnoreCase("Payment account not found.")) {
                startActivityForResult(new Intent(WithdrawMoneyActivity.this, ChoosePaymentMethodActivity.class), REQ_ACCOUNTS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_ACCOUNT_ID) {
            if (data != null) {
                Payment paymentData = (Payment) data.getSerializableExtra(Constants.ACCOUNT_DATA);
                if (paymentData != null) {
                    binding.tvProvider.setText(String.format("%s:", paymentData.provider));
                    binding.tvPaypalEmail.setText(paymentData.account);
                    accountId = paymentData.id;
                }
            }
        } else if (resultCode == RESULT_OK && requestCode == REQ_ACCOUNTS) {
            getPaymentAccountAPI.getAccounts();
        }
    }
}
