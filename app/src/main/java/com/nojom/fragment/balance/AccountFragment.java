package com.nojom.fragment.balance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.AccountsAdapter;
import com.nojom.apis.GetPaymentAccountAPI;
import com.nojom.databinding.FragmentBalanceAccountBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.Payment;
import com.nojom.ui.balance.ChoosePaymentMethodActivity;

import java.util.List;

public class AccountFragment extends BaseFragment {
    private FragmentBalanceAccountBinding binding;
    private GetPaymentAccountAPI getPaymentAccountAPI;
    private AccountsAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_balance_account, container, false);
        getPaymentAccountAPI = new GetPaymentAccountAPI();
        getPaymentAccountAPI.init(activity);
        initData();
        return binding.getRoot();
    }

    private void initData() {

        binding.noData.tvNoTitle.setText(getString(R.string.no_accounts));
        binding.noData.tvNoDescription.setText(getString(R.string.no_account_desc));

        binding.rvAccounts.setLayoutManager(new LinearLayoutManager(activity));

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.shimmerLayout.startShimmer();
            getPaymentAccountAPI.getAccounts();
        });

        binding.tvAddAccount.setOnClickListener(view -> {
            activity.redirectActivity(ChoosePaymentMethodActivity.class);
//            Intent i = new Intent(activity, VerifyPaymentActivity.class);
//            startActivityForResult(i, 121);
        });

        getPaymentAccountAPI.getListMutableLiveData().observe(activity, this::setAdapter);

        getPaymentAccountAPI.getIsShowProgress().observe(activity, isShow -> {
            if (!isShow) {
                binding.shimmerLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
            } else {
                if (!binding.swipeRefreshLayout.isRefreshing()) {
                    binding.shimmerLayout.startShimmer();
                    binding.shimmerLayout.setVisibility(View.VISIBLE);
                    binding.shimmerLayout.setVisibility(View.INVISIBLE);
                    binding.noData.llNoData.setVisibility(View.INVISIBLE);
                } else {
                    binding.shimmerLayout.setVisibility(View.VISIBLE);
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setAdapter(List<Payment> paymentList) {
        if (paymentList != null && paymentList.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new AccountsAdapter(activity);
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
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPaymentAccountAPI.getAccounts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
