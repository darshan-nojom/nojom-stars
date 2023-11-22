package com.nojom.ui.gigs;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nojom.R;
import com.nojom.adapter.GigProjectsAdapter;
import com.nojom.databinding.FragmentProjectsListBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.Utils;

import static com.nojom.util.Constants.GIG_IN_PROGRESS;
import static com.nojom.util.Constants.GIG_PAST_PROJECT;

public class GigProjectListFragment extends BaseFragment implements
        GigProjectsAdapter.GigClickListener {

    public static GigProjectListFragment newInstance(boolean isWorkInProgress, int gigId, String gigType) {
        GigProjectListFragment fragment = new GigProjectListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_WORK_INPROGRESS, isWorkInProgress);
        args.putInt("gigid", gigId);
        args.putString("gigtype", gigType);
        fragment.setArguments(args);
        return fragment;
    }

    private GigProjectListFragmentVM projectsListFragmentVM;
    private FragmentProjectsListBinding binding;
    private boolean isWorkInProgress;
    private GigProjectsAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int selectedAdapterPos = -1;
    private int gigId;
    private String gigType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_projects_list, container, false);

        projectsListFragmentVM = ViewModelProviders.of(this).get(GigProjectListFragmentVM.class);
        projectsListFragmentVM.init(GigProjectListFragment.this);
        initData();

        Utils.trackFirebaseEvent(activity, "Gig_List_Screen");
        return binding.getRoot();
    }


    private void initData() {
        binding.shimmerLayoutProjects.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);

        if (getArguments() != null) {
            isWorkInProgress = getArguments().getBoolean(Constants.IS_WORK_INPROGRESS);
            gigId = getArguments().getInt("gigid", 0);
            gigType = getArguments().getString("gigtype");
        }

        binding.noData.tvNoTitle.setText(getString(isWorkInProgress ? R.string.no_inprogress_gigs : R.string.no_past_gigs));
        binding.noData.tvNoDescription.setText(getString(isWorkInProgress ? R.string.no_inprogress_gigs_desc : R.string.no_past_gigs_desc));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvProjects.setLayoutManager(linearLayoutManager);

        //scroll more listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
//                    if (activity instanceof MyProjectsActivity) {
//                        ((MyProjectsActivity) activity).showHideHorizontalProgress(View.VISIBLE);
//                    }
                    projectsListFragmentVM.getPageNo().setValue(page);
                    projectsListFragmentVM.getProjectList(isWorkInProgress ? GIG_IN_PROGRESS : GIG_PAST_PROJECT, false, gigId);
                }
            }
        };

        //Observer call when need to show progress [Shimmer]
        projectsListFragmentVM.getIsProgress().observe(this, isRefresh -> {
            if (isRefresh) {

                binding.rvProjects.setVisibility(View.INVISIBLE);
                binding.noData.llNoData.setVisibility(View.GONE);
                binding.shimmerLayout.startShimmer();
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            } else {

                binding.rvProjects.setVisibility(View.VISIBLE);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
//                if (mAdapter == null || mAdapter.getItemCount() == 0) {
//                    binding.noData.llNoData.setVisibility(View.VISIBLE);
//                }
            }

        });

        //call to get data from server
        //projectsListFragmentVM.getProjectList(isWorkInProgress ? GIG_IN_PROGRESS : GIG_PAST_PROJECT, true, gigId);

        //swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        //observer to used when there are any changes in list
        projectsListFragmentVM.getJobList().observe(this, data -> bindAdapter());

        //Observer call when something wrong [Shimmer]
        projectsListFragmentVM.getSomethingWrong().observe(this, isWrong -> {
            if (isWrong) {//something went wrong
                binding.swipeRefreshLayout.setRefreshing(false);
//                if (activity instanceof MyProjectsActivity) {
//                    ((MyProjectsActivity) activity).showHideHorizontalProgress(View.GONE);
//                }
            }
        });

        projectsListFragmentVM.getContractDetails().observe(this, project -> {
            activity.runOnUiThread(() -> {
                if (selectedAdapterPos != -1) {
                    mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                    mAdapter.notifyItemChanged(selectedAdapterPos);
                }
            });

            if (project != null) {
                Intent i = new Intent(activity, ContractDetailsActivity.class);
                i.putExtra(Constants.PROJECT, project);
                i.putExtra("gigtype", gigType);
                startActivity(i);
                selectedAdapterPos = -1;
            }
        });

        projectsListFragmentVM.getContractDetailFail().observe(this, isFail -> {
            if (selectedAdapterPos != -1) {
                mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                mAdapter.notifyItemChanged(selectedAdapterPos);
            }
            selectedAdapterPos = -1;
        });

    }

    private void bindAdapter() {
        if (projectsListFragmentVM.getJobList().getValue() != null && projectsListFragmentVM.getJobList().getValue().size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new GigProjectsAdapter(activity, this);
                mAdapter.doRefresh(projectsListFragmentVM.getJobList().getValue());
                binding.rvProjects.setAdapter(mAdapter);
            } else {
                mAdapter.doRefresh(projectsListFragmentVM.getJobList().getValue());
            }
        } else {
            if (mAdapter != null && projectsListFragmentVM.getJobList().getValue() != null) {
                mAdapter.doRefresh(projectsListFragmentVM.getJobList().getValue());
            }
            if (mAdapter == null || mAdapter.getItemCount() == 0) {
                binding.noData.llNoData.setVisibility(View.VISIBLE);
            }
        }

        if (mAdapter != null) {
            mAdapter.setState(isWorkInProgress);
        }
        binding.swipeRefreshLayout.setRefreshing(false);
//        projectsListFragmentVM.getIsProgress().setValue(true);
//        if (activity instanceof MyProjectsActivity) {
//            ((MyProjectsActivity) activity).showHideHorizontalProgress(View.GONE);
//        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshList();
        }
    };

    private void refreshList() {
        projectsListFragmentVM.getPageNo().setValue(1);
        scrollListener.resetState();
        projectsListFragmentVM.getProjectList(isWorkInProgress ? GIG_IN_PROGRESS : GIG_PAST_PROJECT, false, gigId);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
        if (scrollListener != null)
            binding.rvProjects.addOnScrollListener(scrollListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.rvProjects.removeOnScrollListener(scrollListener);
    }

    @Override
    public void onClickGig(int jobId, int selectedPos) {
        selectedAdapterPos = selectedPos;
        activity.isClickableView = true;
        if (gigType.equalsIgnoreCase("1") || gigType.equals("3")) {//custom gig
            projectsListFragmentVM.getCustomContractById(jobId);
        } else {
            projectsListFragmentVM.getContractById(jobId);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
