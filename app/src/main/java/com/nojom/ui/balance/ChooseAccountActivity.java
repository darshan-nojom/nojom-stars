package com.nojom.ui.balance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.apis.GetPaymentAccountAPI;
import com.nojom.databinding.ActivityChooseAccountBinding;
import com.nojom.model.BankAccounts;
import com.nojom.model.Payment;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.PaymentActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;

public class ChooseAccountActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {
    private ActivityChooseAccountBinding binding;
    private GetPaymentAccountAPI getPaymentAccountAPI;
    private RecyclerviewAdapter mAdapter;
    private BankAccounts.Data paymentData;
    private ArrayList<BankAccounts.Data> accountList;
    private int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_account);
        binding.setChooseAccount(this);
        getPaymentAccountAPI = new GetPaymentAccountAPI();
        getPaymentAccountAPI.init(this);
        initData();
    }

    private void initData() {

        if (getIntent() != null) {
            accountList = (ArrayList<BankAccounts.Data>) getIntent().getSerializableExtra(Constants.ACCOUNTS);
            accountId = getIntent().getIntExtra(Constants.ACCOUNT_ID, 0);
        }

        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(this));

        binding.rvAccounts.setVisibility(View.VISIBLE);
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);

        getPaymentAccountAPI.getListMutableLiveData().observe(this, this::setAdapter);

        getPaymentAccountAPI.getIsShowProgress().observe(this, isShow -> {
            if (isShow) {
                binding.rvAccounts.setVisibility(View.GONE);
                binding.shimmerLayout.startShimmer();
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                binding.rvAccounts.setVisibility(View.VISIBLE);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
            }
        });

        if (accountList != null) {
            getPaymentAccountAPI.getListMutableLiveData().postValue(accountList);
        }

        binding.tvAddAccount.setOnClickListener(v -> {
//            redirectActivity(PaymentActivity.class);
            Intent intent=new Intent(this,PaymentActivity.class);
            launchSomeActivity.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        getPaymentAccountAPI.getAccounts();
                    }
                }
            });

    @Override
    protected void onResume() {
        super.onResume();
//        chooseAccountActivityVM.getAccounts(this);
    }

    private void setAdapter(ArrayList<BankAccounts.Data> paymentList) {
        if (paymentList != null && paymentList.size() > 0) {
            binding.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new RecyclerviewAdapter(paymentList, R.layout.item_bank_account, this);
            }
            mAdapter.doRefresh(paymentList);
            if (binding.rvAccounts.getAdapter() == null) {
                binding.rvAccounts.setAdapter(mAdapter);
            }
        } else {
            binding.llNoData.setVisibility(View.VISIBLE);
            if (mAdapter != null)
                mAdapter.doRefresh(null);
        }
    }

    @Override
    public void bindView(View view, int position) {
        TextView tvBn = view.findViewById(R.id.tv_bankName);
        TextView tvPrimary = view.findViewById(R.id.tv_primary);
        TextView tvBenfName = view.findViewById(R.id.tv_benfName);
        ImageView imgArrow = view.findViewById(R.id.img_arrow);
        ImageView imgCheckUnCheck = view.findViewById(R.id.img_check);
        TextView tvAcc = view.findViewById(R.id.tv_accNo);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        tvPrimary.setVisibility(View.GONE);
        imgArrow.setVisibility(View.GONE);
        imgCheckUnCheck.setVisibility(View.VISIBLE);
        imgCheckUnCheck.setImageResource(R.drawable.circle_uncheck);

        ArrayList<BankAccounts.Data> paymentList = getPaymentAccountAPI.getListMutableLiveData().getValue();
        if (paymentList == null || paymentList.size() == 0) {
            return;
        }

        BankAccounts.Data item = paymentList.get(position);

        tvBn.setText(item.getName(language));
        tvAcc.setText(item.iban);
        tvBenfName.setText(item.beneficiary_name);
        tvBn.setTag(item.bank_id + "");

        if (item.status.equalsIgnoreCase("1")) {
            tvStatus.setText(getString(R.string.verified));
            tvStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.green_border_5));
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.greendark));
        } else {
            tvStatus.setText(getString(R.string.not_verified));
            tvStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.red_border_5));
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red_dark));
        }

        if (item.id == accountId) {
            imgCheckUnCheck.setImageResource(R.drawable.circle_check);
            paymentData = item;
        } else {
            imgCheckUnCheck.setImageResource(R.drawable.circle_uncheck);
        }

        view.setOnClickListener(view1 -> {
//            if (TextUtils.isEmpty(item.token)) {
//                Intent i = new Intent(this, PaymentActivity.class);
//                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(position));
//                startActivityForResult(i, 111);
//            } else {
                paymentData = item;
                accountId = item.id;
                mAdapter.notifyDataSetChanged();
//            }
        });
    }

    public void onClickSave() {
        if (paymentData != null) {
            Intent i = new Intent();
            i.putExtra(Constants.ACCOUNT_DATA, paymentData);
            setResult(RESULT_OK, i);
            finish();
        } else {
            toastMessage(getString(R.string.please_select_one_account_for_get_paid));
        }
    }

    public void onClickBack() {
        onBackPressed();
    }
}
