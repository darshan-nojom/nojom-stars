package com.nojom.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.PortfolioListAdapter;
import com.nojom.apis.GetPortfolioAPI;
import com.nojom.databinding.FragmentReviewsProfileBinding;
import com.nojom.fragment.BaseFragment;

public class PortfolioFragment extends BaseFragment {
    private FragmentReviewsProfileBinding binding;
    private GetPortfolioAPI getPortfolioAPI;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews_profile, container, false);
        getPortfolioAPI = new GetPortfolioAPI();
        getPortfolioAPI.init(activity);
        initData();
        return binding.getRoot();
    }

    public void getPortfolio() {
        getPortfolioAPI.getMyPortfolios();
    }

    private void initData() {
        binding.noData.tvNoTitle.setText(getString(R.string.no_portfolio));
        binding.noData.tvNoDescription.setText(getString(R.string.no_portfolio_desc));

        binding.rvReviews.setLayoutManager(new LinearLayoutManager(activity));
        getPortfolioAPI.getMyPortfolios();

        getPortfolioAPI.getListMutableLiveData().observe(this, data -> {
            if (data != null && data.size() > 0) {
                binding.noData.llNoData.setVisibility(View.GONE);
                PortfolioListAdapter portfolioFileAdapter = new PortfolioListAdapter(activity, data);
                binding.rvReviews.setAdapter(portfolioFileAdapter);
            } else {
                binding.noData.llNoData.setVisibility(View.VISIBLE);
            }
        });

        getPortfolioAPI.getIsProgress().observe(this, isShow -> {
            if (isShow) {
                binding.shimmerLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.startShimmer();
            } else {
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.shimmerLayout.stopShimmer();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
