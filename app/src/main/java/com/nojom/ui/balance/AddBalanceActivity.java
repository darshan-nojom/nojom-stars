package com.nojom.ui.balance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.adapter.BankAccountAdapter;
import com.nojom.databinding.ActivityWalletAddBalanceBinding;
import com.nojom.model.WalletAccount;
import com.nojom.model.WalletData;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Utils;


public class AddBalanceActivity extends BaseActivity implements BankAccountAdapter.OnClickBankListener {
    private ActivityWalletAddBalanceBinding binding;
    private double availableBalance;
    private CampByIdVM campByIdVM;
    private WalletData walletData;
    public static AddBalanceActivity sActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_add_balance);
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
        if (getCurrency().equals("SAR")) {
            binding.txtSign.setText(getString(R.string.sar));
        } else {
            binding.txtSign.setText(getString(R.string.dollar));
        }

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtWalletBalance.setText(Utils.priceWithSAR(this, Utils.getDecimalValue("" + walletData.available_balance)).replace(getString(R.string.sar), ""));

        binding.txtAddBalance.setOnClickListener(view -> {
            if (TextUtils.isEmpty(binding.etAmount.getText().toString().trim())) {
                toastMessage(getString(R.string.amount_should_not_be_zero));
                return;
            }
            if (selectedWalletAccount == null) {
                toastMessage("Please select any account");
                return;
            }
            campByIdVM.withdrawMoney(selectedWalletAccount.id, Double.parseDouble(binding.etAmount.getText().toString()));
        });

        binding.txtHistory.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewHistoryActivity.class);
            intent.putExtra("data", walletData);
            startActivity(intent);
        });
        binding.relAddAccount.setOnClickListener(view -> {
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

        campByIdVM.mpWalletBalanceData.observe(this, walletData1 -> {
            if (walletData1 != null) {
                String url = walletData1.embed_url;
                Uri uri = Uri.parse(url);
                // Extract the last segment from the path
                String lastSegment = uri.getLastPathSegment();

                Intent intent = new Intent(this, WebViewWalletActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("bal", "" + binding.etAmount.getText().toString());
                intent.putExtra("intent", "" + lastSegment);
                intent.putExtra("campId", "" + getUserID());
                startActivity(intent);
            }
        });

        campByIdVM.bankAccountList.observe(this, walletData1 -> {
            if (walletData1 != null && walletData1.bankAccounts != null && walletData1.bankAccounts.size() > 0) {
                BankAccountAdapter accountAdapter = new BankAccountAdapter(this, walletData1.bankAccounts, this);
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
