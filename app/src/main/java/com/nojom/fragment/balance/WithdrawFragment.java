package com.nojom.fragment.balance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.WithdrawAdapter;
import com.nojom.databinding.FragmentBalanceWithdrawBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.Withdrawal;

import java.util.List;

public class WithdrawFragment extends BaseFragment {
    private FragmentBalanceWithdrawBinding binding;
    private WithdrawFragmentVM withdrawFragmentVM;
    private WithdrawAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_balance_withdraw, container, false);
        withdrawFragmentVM = ViewModelProviders.of(this).get(WithdrawFragmentVM.class);
        withdrawFragmentVM.init(this);
        initData();
        return binding.getRoot();
    }

    private void initData() {
        binding.noData.tvNoTitle.setText(getString(R.string.no_withdraw_balance));
        binding.noData.tvNoDescription.setText(getString(R.string.no_withdraw_balance_desc));

        binding.rvWithdraw.setLayoutManager(new LinearLayoutManager(activity));
        withdrawFragmentVM.getWithdrawals();

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.shimmerLayout.startShimmer();
            withdrawFragmentVM.getWithdrawals();
        });

        withdrawFragmentVM.getIsShowProgress().observe(this, isShow -> {
            if (!isShow) {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
            } else {
                if (!binding.swipeRefreshLayout.isRefreshing()) {
                    binding.shimmerLayout.startShimmer();
                    binding.shimmerLayout.setVisibility(View.VISIBLE);
                    binding.noData.llNoData.setVisibility(View.INVISIBLE);
                } else {
                    binding.shimmerLayout.stopShimmer();
                    binding.shimmerLayout.setVisibility(View.GONE);
                }
            }
        });

        withdrawFragmentVM.getListMutableLiveData().observe(this, this::setAdapter);
    }

    private void setAdapter(List<Withdrawal> withdrawList) {
        if (withdrawList != null && withdrawList.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new WithdrawAdapter(activity, null);
            }
            mAdapter.doRefresh(withdrawList);
            if (binding.rvWithdraw.getAdapter() == null) {
                binding.rvWithdraw.setAdapter(mAdapter);
            }
        } else {
            binding.noData.llNoData.setVisibility(View.VISIBLE);
            if (mAdapter != null)
                mAdapter.doRefresh(null);
        }
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
