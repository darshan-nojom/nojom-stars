package com.nojom.ui.projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.adapter.OrderAdapter;
import com.nojom.databinding.ActivityMyOrdersBinding;
import com.nojom.model.CampList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends BaseActivity implements View.OnClickListener, OrderAdapter.OnClickJobListener {
    private MyOrdersActivityVM myOrdersActivityVM;
    private ActivityMyOrdersBinding binding;
    private List<CampList> campList;
    private OrderAdapter mAdapter;
    //    private PastOrderAdapter pastOrderAdapter;
    int pageNo = 1;
    int pastPageNo = 1;
    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isEndCurrentOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders);
        myOrdersActivityVM = new MyOrdersActivityVM(Task24Application.getActivity(), this);

        initData();
    }

    private void initData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvCurrentOrders.setLayoutManager(linearLayoutManager);

        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer();

        myOrdersActivityVM.currentWalletData.observe(this, walletData -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (Integer.parseInt(walletData.currentPage) == walletData.totalPages) {//if both match then its END
                isEndCurrentOrderList = true;
                myOrdersActivityVM.getOrders(1, true);
            } else {
                if (walletData.campaigns != null && walletData.campaigns.size() > 0) {
                    if (pageNo == 1) {
                        CampList campList1 = new CampList();
                        campList1.isHeaderType = true;
                        campList1.headerText = getString(R.string.current_orders);
                        campList.add(campList1);
                    }
                    campList.addAll(walletData.campaigns);
                    setAdapter();
                }
            }

            binding.shimmerLayout.setVisibility(View.GONE);
            binding.shimmerLayout.stopShimmer();
        });
        myOrdersActivityVM.pastOrderMutableLiveData.observe(this, orderData -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (orderData != null && orderData.size() > 0) {
                if (pastPageNo == 1) {
                    CampList campList1 = new CampList();
                    campList1.isHeaderType = true;
                    campList1.headerText = getString(R.string.past_orders);
                    campList.add(campList1);
                }
                campList.addAll(orderData);
//                setPastAdapter();
                setAdapter();
            }
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.shimmerLayout.stopShimmer();
        });

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                if (totalItemsCount > 9) {
                if (isEndCurrentOrderList) {//past records
                    pastPageNo = page;
                    myOrdersActivityVM.getOrders(pastPageNo, true);
                } else {//current records
                    pageNo = page;
                    myOrdersActivityVM.getOrders(pageNo, false);
                }

//                }
            }
        };
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            campList = new ArrayList<>();
            pageNo = 1;
            pastPageNo = 1;
            isEndCurrentOrderList = false;
            scrollListener.resetState();
            myOrdersActivityVM.getOrders(1, false);
        });

        campList = new ArrayList<>();
        pastPageNo = 1;
        pageNo = 1;
        isEndCurrentOrderList = false;
        myOrdersActivityVM.getOrders(1, false);
    }

    private void setAdapter() {
        if (campList != null && campList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new OrderAdapter(this, this);
            }
            if (pageNo == 1 && pastPageNo == 1) {
                mAdapter.initList(campList);
            } else {
                mAdapter.doRefresh(campList);
            }

            if (binding.rvCurrentOrders.getAdapter() == null) {
                binding.rvCurrentOrders.setAdapter(mAdapter);
            }
        } else {
            if (mAdapter != null) mAdapter.initList(campList);
        }
        binding.rvCurrentOrders.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onClickJob(int jpId, int position, String jobType, String gigType, CampList campList) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("data", campList);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scrollListener != null) binding.rvCurrentOrders.addOnScrollListener(scrollListener);
//        myOrdersActivityVM.getOrders(1, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scrollListener != null) binding.rvCurrentOrders.removeOnScrollListener(scrollListener);
    }
}
