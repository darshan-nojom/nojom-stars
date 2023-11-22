package com.nojom.ui.balance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.apis.GetPaymentAccountAPI;
import com.nojom.databinding.ActivityChooseAccountBinding;
import com.nojom.model.Payment;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;

public class ChooseAccountActivity extends BaseActivity implements RecyclerviewAdapter.OnViewBindListner {
    private ActivityChooseAccountBinding binding;
    private GetPaymentAccountAPI getPaymentAccountAPI;
    private RecyclerviewAdapter mAdapter;
    private Payment paymentData;
    private ArrayList<Payment> accountList;
    private int accountId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_account);
        binding.setChooseAccount(this);
        getPaymentAccountAPI = new GetPaymentAccountAPI();
        getPaymentAccountAPI.init(this);
        initData();
    }

    private void initData() {

        if (getIntent() != null) {
            accountList = (ArrayList<Payment>) getIntent().getSerializableExtra(Constants.ACCOUNTS);
            accountId = getIntent().getIntExtra(Constants.ACCOUNT_ID, 0);
        }

        binding.noData.tvNoTitle.setText(getString(R.string.no_accounts));
        binding.noData.tvNoDescription.setText(getString(R.string.no_account_desc));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
//        chooseAccountActivityVM.getAccounts(this);
    }

    private void setAdapter(ArrayList<Payment> paymentList) {
        if (paymentList != null && paymentList.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new RecyclerviewAdapter(paymentList, R.layout.item_choose_account, this);
            }
            mAdapter.doRefresh(paymentList);
            if (binding.rvAccounts.getAdapter() == null) {
                binding.rvAccounts.setAdapter(mAdapter);
            }
        } else {
            binding.noData.llNoData.setVisibility(View.VISIBLE);
            if (mAdapter != null)
                mAdapter.doRefresh(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {
            getPaymentAccountAPI.getAccounts();
        }
    }

    @Override
    public void bindView(View view, int position) {
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvAccount = view.findViewById(R.id.tv_account);
        TextView tvStatus = view.findViewById(R.id.tv_status);
        ImageView imgCheckUnCheck = view.findViewById(R.id.img_check_uncheck);
        ImageView imgNext = view.findViewById(R.id.img_next);

        ArrayList<Payment> paymentList = getPaymentAccountAPI.getListMutableLiveData().getValue();
        if (paymentList == null || paymentList.size() == 0) {
            return;
        }

        Payment item = paymentList.get(position);
        tvEmail.setText(item.account);
        tvAccount.setText(item.provider);

        if (!TextUtils.isEmpty(item.token) || item.verified.equalsIgnoreCase("1")) {
            tvStatus.setText(getString(R.string.verified));
            tvStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.green_border_5));
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.greendark));
            imgCheckUnCheck.setVisibility(View.VISIBLE);
            imgNext.setVisibility(View.GONE);
            if (item.id == accountId) {
                imgCheckUnCheck.setImageResource(R.drawable.circle_check);
                paymentData = item;
            } else {
                imgCheckUnCheck.setImageResource(R.drawable.circle_uncheck);
            }
        } else {
            tvStatus.setText(getString(R.string.not_verified));
            tvStatus.setBackground(ContextCompat.getDrawable(this, R.drawable.red_border_5));
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.red_dark));
            imgCheckUnCheck.setVisibility(View.GONE);
            imgNext.setVisibility(View.VISIBLE);
        }

        view.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(item.token) && !item.provider.equalsIgnoreCase("Payoneer")) {
                Intent i = new Intent(this, EditPaypalActivity.class);
                i.putExtra(Constants.ACCOUNT_DATA, paymentList.get(position));
                startActivityForResult(i, 111);
            } else {
                paymentData = item;
                accountId = item.id;
                mAdapter.notifyDataSetChanged();
            }
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
