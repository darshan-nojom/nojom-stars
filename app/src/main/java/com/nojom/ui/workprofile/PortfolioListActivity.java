package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.PortfolioListAdapter;
import com.nojom.apis.GetPortfolioAPI;
import com.nojom.databinding.ActivityPortfolioListBinding;
import com.nojom.ui.BaseActivity;

public class PortfolioListActivity extends BaseActivity {
    private GetPortfolioAPI getPortfolioAPI;
    private ActivityPortfolioListBinding binding;
    private PortfolioListAdapter portfolioFileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_portfolio_list);
        getPortfolioAPI = new GetPortfolioAPI();
        getPortfolioAPI.init(this);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvSave.setVisibility(View.GONE);
        binding.toolbar.tvAdd.setVisibility(View.VISIBLE);
        binding.toolbar.tvAdd.setOnClickListener(v -> redirectActivity(PortfolioActivity.class));

        binding.noData.tvNoTitle.setText(getString(R.string.no_portfolio));
        binding.noData.tvNoDescription.setText(getString(R.string.no_portfolio_desc));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getPortfolioAPI.getListMutableLiveData().observe(this, data -> {
            if (data != null && data.size() > 0) {
                binding.noData.llNoData.setVisibility(View.GONE);
                portfolioFileAdapter = new PortfolioListAdapter(this, data);
                binding.recyclerView.setAdapter(portfolioFileAdapter);
            } else {
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.recyclerView.post(() -> {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.noData.llNoData.setVisibility(View.VISIBLE);
                });
            }
        });

        getPortfolioAPI.getIsProgress().observe(this, isShow -> {
            if (isShow) {
                binding.recyclerView.setVisibility(View.GONE);
                binding.shimmerLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.startShimmer();
            } else {
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.shimmerLayout.stopShimmer();
                if (portfolioFileAdapter != null && portfolioFileAdapter.getData() != null && portfolioFileAdapter.getData().size() > 0) {
                    binding.recyclerView.post(() -> {
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.noData.llNoData.setVisibility(View.GONE);
                    });
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.noData.llNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPortfolioAPI.getMyPortfolios();
    }
}
