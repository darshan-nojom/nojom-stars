package com.nojom.fragment.balance;

import static com.nojom.util.Constants.API_CONTRACT_DETAIL;
import static com.nojom.util.Constants.API_CUSTOM_CONTRACT_DETAIL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.IncomeAdapter;
import com.nojom.api.APIRequest;
import com.nojom.apis.JobDetailAPI;
import com.nojom.databinding.FragmentBalanceIncomeBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.Balance;
import com.nojom.model.ContractDetails;
import com.nojom.ui.balance.BalanceActivity;
import com.nojom.ui.gigs.ContractDetailsActivity;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;

import java.util.List;

public class IncomeFragment extends BaseFragment implements IncomeAdapter.OnClickListener, APIRequest.JWTRequestResponseListener {
    private FragmentBalanceIncomeBinding binding;
    private IncomeFragmentVM incomeFragmentVM;
    private IncomeAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int selectedAdapterPos;
    private JobDetailAPI jobDetailAPI;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_balance_income, container, false);
        incomeFragmentVM = ViewModelProviders.of(this).get(IncomeFragmentVM.class);
        incomeFragmentVM.init(this);
        jobDetailAPI = new JobDetailAPI();
        jobDetailAPI.init(activity);
        initData();
        return binding.getRoot();
    }

    private void initData() {
        binding.noData.tvNoTitle.setText(getString(R.string.no_income_balance));
        binding.noData.tvNoDescription.setText(getString(R.string.no_income_balance_desc));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvIncome.setLayoutManager(linearLayoutManager);
        incomeFragmentVM.getIncome(1);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            scrollListener.resetState();
            incomeFragmentVM.getIncome(1);
        });


        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    ((BalanceActivity) activity).hideShowHorizontalProgressBar(View.VISIBLE);
                    incomeFragmentVM.getIncome(page);
                }
            }
        };

        incomeFragmentVM.getIsShowProgress().observe(this, isShow -> {
            activity.disableEnableTouch(isShow);
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

        incomeFragmentVM.getListMutableLiveData().observe(this, this::setAdapter);

        jobDetailAPI.getIsShowProgress().observe(this, aBoolean -> {
            mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
            mAdapter.notifyItemChanged(selectedAdapterPos);

            ((BalanceActivity) activity).hideShowHorizontalProgressBar(View.GONE);
        });

        jobDetailAPI.getProjectById().observe(this, projectByID -> {
            activity.runOnUiThread(() -> {
                mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                mAdapter.notifyItemChanged(selectedAdapterPos);
            });

            if (projectByID != null && isAdded()) {
                Intent i = new Intent(getActivity(), ProjectDetailsActivity.class);
                i.putExtra(Constants.PROJECT, projectByID);
                startActivity(i);
            }
        });
    }

    private void setAdapter(List<Balance.Income> incomeList) {
        if (incomeList != null && incomeList.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new IncomeAdapter(activity, IncomeFragment.this);
            }
            mAdapter.doRefresh(incomeList);
            if (binding.rvIncome.getAdapter() == null) {
                binding.rvIncome.setAdapter(mAdapter);
            }
        } else {
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.noData.llNoData.setVisibility(View.VISIBLE);
            if (mAdapter != null)
                mAdapter.doRefresh(null);
        }
        binding.swipeRefreshLayout.setRefreshing(false);
        ((BalanceActivity) activity).hideShowHorizontalProgressBar(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scrollListener != null)
            binding.rvIncome.addOnScrollListener(scrollListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.rvIncome.removeOnScrollListener(scrollListener);
    }

    public void getGigDetailAPI(int id) {
        if (!activity.isNetworkConnected())
            return;

        String url = API_CONTRACT_DETAIL + id;
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, url, false, null);
    }

    String customGigDetailUrl;

    public void getCustomGigDetails(int id) {
        if (!activity.isNetworkConnected())
            return;

        customGigDetailUrl = API_CUSTOM_CONTRACT_DETAIL + id;
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, customGigDetailUrl, false, null);
    }


    @Override
    public void onClickItem(int jobPostId, int pos, String postType, String gigType) {
        selectedAdapterPos = pos;
        activity.isClickableView = true;
        if (postType.equalsIgnoreCase("2")) {//job
            jobDetailAPI.getProjectById(jobPostId);
        } else {//gig
            if (gigType.equals("1") || gigType.equals("3")) {//custom
                getCustomGigDetails(jobPostId);
            } else {//standard
                getGigDetailAPI(jobPostId);
            }
        }
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        activity.isClickableView = false;
        try {
            ContractDetails project = ContractDetails.getContractDetails(responseBody);
            activity.runOnUiThread(() -> {
                mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                mAdapter.notifyItemChanged(selectedAdapterPos);
            });
            if (project != null) {
                Intent i = new Intent(activity, ContractDetailsActivity.class);
                i.putExtra(Constants.PROJECT, project);
//                if (!TextUtils.isEmpty(customGigDetailUrl)) {
                i.putExtra("gigtype", project.gigType);//custom gig
//                }
                startActivity(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        activity.isClickableView = false;
        try {
            mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
            mAdapter.notifyItemChanged(selectedAdapterPos);
//        }
            activity.isClickableView = false;
            ((BalanceActivity) activity).hideShowHorizontalProgressBar(View.GONE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
