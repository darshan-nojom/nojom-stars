package com.nojom.ui.balance;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.adapter.BankAccountAdapter;
import com.nojom.adapter.NewHistoryAdapter;
import com.nojom.databinding.ActivityWalletHistoryBinding;
import com.nojom.model.WalletAccount;
import com.nojom.model.WalletData;
import com.nojom.ui.BaseActivity;


public class NewHistoryActivity extends BaseActivity implements BankAccountAdapter.OnClickBankListener {
    private ActivityWalletHistoryBinding binding;
    private double availableBalance;
    private CampByIdVM campByIdVM;
    private WalletData walletData;
    public static NewHistoryActivity sActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_history);
        campByIdVM = new CampByIdVM(Task24Application.getActivity(), this);
        walletData = (WalletData) getIntent().getSerializableExtra("data");
        initData();
        sActivity = this;
    }

    private void initData() {
        campByIdVM.getHistory();
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        campByIdVM.bankAccountList.observe(this, walletData1 -> {
            if (walletData1 != null && walletData1.withdrawals != null && walletData1.withdrawals.size() > 0) {
                NewHistoryAdapter accountAdapter = new NewHistoryAdapter(this, walletData1.withdrawals);
                binding.viewpager.setAdapter(accountAdapter);
            }
        });
    }

    WalletAccount selectedWalletAccount;

    @Override
    public void onClickBank(int pos, WalletAccount walletAccount) {
        selectedWalletAccount = walletAccount;
    }
}
