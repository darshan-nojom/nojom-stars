package com.nojom.ui.balance;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityEditPaypalBinding;
import com.nojom.model.Payment;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

public class EditPaypalActivity extends BaseActivity {
    private ActivityEditPaypalBinding binding;
    private EditPaypalActivityVM editPaypalActivityVM;
    private Payment paymentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_paypal);
        binding.setEditAct(this);
        editPaypalActivityVM = ViewModelProviders.of(this).get(EditPaypalActivityVM.class);
        editPaypalActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        if (getIntent() != null) {
            paymentData = (Payment) getIntent().getSerializableExtra(Constants.ACCOUNT_DATA);
        }

        if (paymentData == null) {
            finish();
            return;
        }

        binding.txtLbl.setText(getString(R.string.edit) + " " + paymentData.provider);
        binding.txtTitle.setText(paymentData.provider + " " + getString(R.string.account_email));

        if (TextUtils.isEmpty(paymentData.token) && !paymentData.verified.equalsIgnoreCase("1")) {
            binding.tvStatus.setText(getString(R.string.not_verified));
            binding.tvStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.red_border_5));
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red_dark));
            binding.llSendEmail.setVisibility(View.VISIBLE);
        } else if (paymentData.verified.equalsIgnoreCase("1")) {
            binding.tvStatus.setText(getString(R.string.verified));
            binding.tvStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.green_border_5));
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.greendark));
        }

        binding.tvPaypalEmail.setText(paymentData.account);
        binding.segmentGroup.setPosition(paymentData.isPrimary.equals("1") ? 1 : 0);

        binding.segmentGroup.setOnPositionChangedListener(position -> editPaypalActivityVM.isPrimary(position, paymentData));

        if (!paymentData.verified.equalsIgnoreCase("1")) {
            binding.segmentGroup.setEnabled(false);
        }

        editPaypalActivityVM.getIsShowProgress().observe(this, isShow -> {
            if (isShow) {
                showHideView(binding.progressBar, View.VISIBLE);
                showHideView(binding.tvDeleteAccount, View.INVISIBLE);
            } else {
                showHideView(binding.tvDeleteAccount, View.VISIBLE);
                showHideView(binding.progressBar, View.GONE);
            }
            disableEnableTouch(isShow);
        });

        editPaypalActivityVM.getIsShowProgressEmail().observe(this, isShow -> {
            if (isShow) {
                showHideView(binding.progressBarEmail, View.VISIBLE);
                showHideView(binding.tvSendEmail, View.GONE);
            } else {
                showHideView(binding.tvSendEmail, View.VISIBLE);
                showHideView(binding.progressBarEmail, View.GONE);
            }
            disableEnableTouch(isShow);
        });

        editPaypalActivityVM.getDeleteAccount().observe(this, aBoolean -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    public void sendEmail() {
        editPaypalActivityVM.verifyAccount(paymentData);
    }

    public void deleteAccount() {
        editPaypalActivityVM.showDeleteDialog(paymentData);
    }

    private void showHideView(View view, int visibility) {
        view.setVisibility(visibility);
    }
}
