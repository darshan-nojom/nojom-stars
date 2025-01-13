package com.nojom.ui.balance;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.adapter.BankAccountAdapter;
import com.nojom.adapter.PaymentAdapter;
import com.nojom.apis.GetPaymentAccountAPI;
import com.nojom.databinding.ActivityAddCardBinding;
import com.nojom.model.ProfileResponse;
import com.nojom.model.WalletAccount;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AddNewBankAccountActivity extends BaseActivity implements BankAccountAdapter.OnClickBankListener, PaymentAdapter.OnClickPaymentListener {
    private ActivityAddCardBinding binding;
    private double availableBalance;
    private CampByIdVM campByIdVM;
    public static AddNewBankAccountActivity sActivity;
    private GetPaymentAccountAPI getPaymentAccountAPI;
    private List<ProfileResponse.BankName> bankNameList;
    private ProfileResponse profileData;
    private WalletAccount walletAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_card);
        campByIdVM = new CampByIdVM(Task24Application.getActivity(), this);
        walletAccount = (WalletAccount) getIntent().getSerializableExtra("selData");

        getPaymentAccountAPI = new GetPaymentAccountAPI();
        getPaymentAccountAPI.init(this);
        bankNameList = new ArrayList<>();
        initData();
        sActivity = this;
    }

    private void initData() {

        getPaymentAccountAPI.getAccounts();
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        if (walletAccount != null) {
            binding.txtBank.setText(walletAccount.bank_name);
            binding.etBenifName.setText(walletAccount.beneficiary_name);
            binding.etAccounts.setText(walletAccount.account_number);
            binding.etIban.setText(walletAccount.iban);
            binding.imgDelete.setVisibility(View.VISIBLE);
        }

        binding.imgDelete.setOnClickListener(view -> {
            if (walletAccount != null) {//edit
                campByIdVM.deleteBank(walletAccount.id);
            }
        });

        binding.txtAddBalance.setOnClickListener(view -> {
            //open to add new account screen

            if (binding.txtBank.getText().toString().equals(getString(R.string.bank))) {
                toastMessage(getString(R.string.please_select_bank_name));
                return;
            }
            if (TextUtils.isEmpty(binding.etBenifName.getText().toString().trim())) {
                toastMessage(getString(R.string.pleas_enter_beneficiary_name));
                return;
            }
            if (TextUtils.isEmpty(binding.etAccounts.getText().toString().trim())) {
                toastMessage(getString(R.string.add_new_account));
                return;
            }
            if (binding.etAccounts.getText().toString().trim().length() < 12) {
                toastMessage(getString(R.string.add_new_account));
                return;
            }
            if (TextUtils.isEmpty(binding.etIban.getText().toString().trim())) {
                toastMessage(getString(R.string.iban));
                return;
            }
            if (walletAccount != null) {//edit
                campByIdVM.updateCard(binding.txtBank.getText().toString(), binding.etBenifName.getText().toString()
                        , binding.etAccounts.getText().toString(), binding.etIban.getText().toString(), walletAccount.id);
            } else {//add
                campByIdVM.addCard(binding.txtBank.getText().toString(), binding.etBenifName.getText().toString()
                        , binding.etAccounts.getText().toString(), binding.etIban.getText().toString());
            }

        });
        binding.relBank.setOnClickListener(view -> showBankNameDialog());

        campByIdVM.mutableProgress.observe(this, aBoolean -> {
            if (aBoolean) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.txtAddBalance.setVisibility(View.INVISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtAddBalance.setVisibility(View.VISIBLE);
            }
        });

        profileData = Preferences.getProfileData(this);

        if (profileData.banks != null && profileData.banks.size() > 0) {
            bankNameList.addAll(profileData.banks);
        }
    }

    WalletAccount selectedWalletAccount;

    @Override
    public void onClickBank(int pos, WalletAccount walletAccount) {
        selectedWalletAccount = walletAccount;
    }

    Dialog dialogBankName;
    PaymentAdapter paymentAdapter;

    void showBankNameDialog() {
        dialogBankName = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialogBankName.setTitle(null);
        dialogBankName.setContentView(R.layout.dialog_item_select_black);
        dialogBankName.setCancelable(true);

        LinearLayout llButton = dialogBankName.findViewById(R.id.ll_bottom);
        TextView tvCancel = dialogBankName.findViewById(R.id.tv_cancel);
        TextView tvApply = dialogBankName.findViewById(R.id.tv_apply);
        final EditText etSearch = dialogBankName.findViewById(R.id.et_search);
        RecyclerView rvTypes = dialogBankName.findViewById(R.id.rv_items);

        etSearch.setVisibility(View.GONE);
        llButton.setVisibility(View.GONE);

        rvTypes.setLayoutManager(new LinearLayoutManager(this));
        try {
            paymentAdapter = new PaymentAdapter(this, bankNameList, this);
            paymentAdapter.setSelectedLanguageList(binding.txtBank.getText().toString());
            rvTypes.setAdapter(paymentAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCancel.setOnClickListener(v -> {
            dialogBankName.dismiss();
        });

        tvApply.setOnClickListener(v -> {
            dialogBankName.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogBankName.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialogBankName.show();
        dialogBankName.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBankName.getWindow().setAttributes(lp);
        etSearch.setOnFocusChangeListener((v, hasFocus) -> etSearch.post(() -> Utils.openSoftKeyboard(this, etSearch)));
        etSearch.requestFocus();
    }

    @Override
    public void onClickBank(ProfileResponse.BankName name, int adapterPos) {
        binding.txtBank.setText(name.getBankName(language));
        binding.txtBank.setTag("" + name.id);
        if (dialogBankName != null) {
            dialogBankName.dismiss();
        }
    }
}
