package com.nojom.ui.balance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.adapter.BankAccountAdapter;
import com.nojom.databinding.ActivityBankaccountListBinding;
import com.nojom.model.WalletAccount;
import com.nojom.model.WalletData;
import com.nojom.ui.BaseActivity;


public class MyAccountActivity extends BaseActivity implements BankAccountAdapter.OnClickBankListener {
    private ActivityBankaccountListBinding binding;
    private double availableBalance;
    private CampByIdVM campByIdVM;
    private WalletData walletData;
    public static MyAccountActivity sActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bankaccount_list);
        campByIdVM = new CampByIdVM(Task24Application.getActivity(), this);
        walletData = (WalletData) getIntent().getSerializableExtra("data");
        initData();
        sActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        campByIdVM.getAccounts();
    }

    private void initData() {


        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.txtAddBalance.setOnClickListener(view -> {
            //open to add new account screen
            Intent intent = new Intent(this, AddNewBankAccountActivity.class);
            intent.putExtra("data", walletData);
            startActivity(intent);
        });

        campByIdVM.mutableProgress.observe(this, aBoolean -> {
            if (aBoolean) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.txtAddBalance.setVisibility(View.INVISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtAddBalance.setVisibility(View.VISIBLE);
            }
        });

        campByIdVM.bankAccountList.observe(this, walletData1 -> {
            if (walletData1 != null && walletData1.bankAccounts != null && walletData1.bankAccounts.size() > 0) {
                BankAccountAdapter accountAdapter = new BankAccountAdapter(this, walletData1.bankAccounts, this);
                accountAdapter.isHideButton = true;
                binding.rvAccount.setAdapter(accountAdapter);
            }
        });
    }

    WalletAccount selectedWalletAccount;

    @Override
    public void onClickBank(int pos, WalletAccount walletAccount) {
        selectedWalletAccount = walletAccount;

        Intent intent = new Intent(this, AddNewBankAccountActivity.class);
        intent.putExtra("selData", walletAccount);
        startActivity(intent);
    }
}
