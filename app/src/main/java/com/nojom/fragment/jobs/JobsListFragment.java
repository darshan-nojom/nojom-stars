package com.nojom.fragment.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.JobsAdapter;
import com.nojom.apis.GetProjectListAPI;
import com.nojom.apis.JobDetailAPI;
import com.nojom.databinding.FragmentProjectsListBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.interfaces.JobListResponseListener;
import com.nojom.model.Projects;
import com.nojom.ui.home.WorkHomeActivity;
import com.nojom.ui.jobs.JobSummaryActivity;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.Preferences;

import java.util.ArrayList;

public class JobsListFragment extends BaseFragment implements JobListResponseListener, JobsAdapter.OnItemClickListener {

    private GetProjectListAPI projectListAPI;
    private FragmentProjectsListBinding binding;
    private boolean isBidding;
    private boolean isOffer;
    private int serviceId = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private JobsAdapter mAdapter;
    private static final String AVAILABLE = "AVAILABLE";
    private static final String BIDDING = "BIDDING";
    private static final String OFFER = "OFFER";
    private boolean isSeenUpdate = false;
    private int jobPosition;
    private int selectedAdapterPos;
    private JobDetailAPI jobDetailAPI;

    public static JobsListFragment newInstance(boolean isBidding, boolean isOffer) {
        JobsListFragment fragment = new JobsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_BIDDING, isBidding);
        args.putBoolean(Constants.IS_OFFER, isOffer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_projects_list, container, false);
        projectListAPI = new GetProjectListAPI();
        projectListAPI.init(JobsListFragment.this);
        projectListAPI.setJobListResponseListener(this);
        jobDetailAPI = new JobDetailAPI();
        jobDetailAPI.init(activity);
        initData();
        return binding.getRoot();
    }

    private void initData() {

        if (getArguments() != null) {
            isBidding = getArguments().getBoolean(Constants.IS_BIDDING);
            isOffer = getArguments().getBoolean(Constants.IS_OFFER);
        }

        serviceId = Preferences.readInteger(activity, Constants.FILTER_ID, 0);
//        if (serviceId == 10) {
//            serviceId = 0;
//        }
        projectListAPI.setServiceId(serviceId);

        if (isBidding) {
            setPlaceHolder(activity.getString(R.string.no_bidding_jobs), activity.getString(R.string.no_bidding_jobs_desc));
        } else if (isOffer) {
            setPlaceHolder(activity.getString(R.string.no_offer_jobs), activity.getString(R.string.no_offer_jobs_desc));
        } else {
            setPlaceHolder(activity.getString(R.string.no_available_jobs), activity.getString(R.string.no_available_jobs_desc));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvProjects.setLayoutManager(linearLayoutManager);
        binding.shimmerLayout.startShimmer();

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    ((WorkHomeActivity) activity).hideShowHorizontalProgressBar(View.VISIBLE);
                    projectListAPI.getPageNo().setValue(page);
                    projectListAPI.getJobPost(isBidding ? BIDDING : isOffer ? OFFER : AVAILABLE, true, isBidding ? null : isOffer ? null : ((WorkHomeActivity) activity).binding.etSearch.getText().toString());
                }
            }
        };

        projectListAPI.getJobPost(isBidding ? BIDDING : isOffer ? OFFER : AVAILABLE, false, isBidding ? null : isOffer ? null : ((WorkHomeActivity) activity).binding.etSearch.getText().toString());

        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh);

        //Observer call when need to show progress [Shimmer]
        projectListAPI.getIsProgress().observe(activity, isRefresh -> {
            if (!isRefresh) {
                binding.rvProjects.setVisibility(View.INVISIBLE);
                binding.noData.llNoData.setVisibility(View.GONE);
                binding.shimmerLayout.startShimmer();
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            } else {
                binding.rvProjects.setVisibility(View.VISIBLE);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
            }
        });

        //observer to used when there are any changes in list
        projectListAPI.getJobList().observe(activity, data -> bindAdapter());

        //Observer call when something wrong
        projectListAPI.getSomethingWrong().observe(activity, isWrong -> {
            if (isWrong) {//something went wrong
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            ((WorkHomeActivity) activity).hideShowHorizontalProgressBar(View.GONE);
        });

        jobDetailAPI.getProjectById().observe(activity, projectByID -> {
            try {

                activity.runOnUiThread(() -> {
                    mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                    mAdapter.notifyItemChanged(selectedAdapterPos);
                });

                if (projectByID != null && isAdded()) {
                    Intent i = new Intent(getActivity(), isBidding ? ProjectDetailsActivity.class : JobSummaryActivity.class);
                    i.putExtra(Constants.PROJECT, projectByID);
                    startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        jobDetailAPI.getIsShowProgress().observe(activity, aBoolean -> {
            try {
                if(mAdapter.getData()!=null && mAdapter.getData().size()>0) {
                    mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                    mAdapter.notifyItemChanged(selectedAdapterPos);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void setPlaceHolder(String title, String desc) {
        binding.noData.tvNoTitle.setText(title);
        binding.noData.tvNoDescription.setText(desc);
    }

    private void bindAdapter() {
        if (projectListAPI.getJobList().getValue() != null && projectListAPI.getJobList().getValue().size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new JobsAdapter(activity, isBidding, isOffer, this);
                mAdapter.doRefresh(projectListAPI.getJobList().getValue());
                binding.rvProjects.setAdapter(mAdapter);
                if (!isBidding)
                    mAdapter.setOnNewJobClick(this::newJobClick);
            } else {
                mAdapter.doRefresh(projectListAPI.getJobList().getValue());
            }
        } else {
            if(mAdapter!=null && projectListAPI.getPageNo().getValue()!=null
                    &&projectListAPI.getPageNo().getValue()==1){
                mAdapter.doRefresh(new ArrayList<>());
                binding.rvProjects.setAdapter(mAdapter);
            }
            if (mAdapter == null || mAdapter.getItemCount() == 0)
                binding.noData.llNoData.setVisibility(View.VISIBLE);
        }

        binding.swipeRefreshLayout.setRefreshing(false);
        projectListAPI.getIsProgress().setValue(true);
        ((WorkHomeActivity) activity).hideShowHorizontalProgressBar(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (scrollListener != null)
                binding.rvProjects.addOnScrollListener(scrollListener);

            WorkHomeActivity workHomeActivity = (WorkHomeActivity) activity;
            if (workHomeActivity != null && workHomeActivity.isRefresh()) {
                if (!isBidding) {
                    serviceId = workHomeActivity.getServiceId();
                    projectListAPI.setServiceId(serviceId);
                    refresh();
                }
                workHomeActivity.setRefresh(false);
            }

            if (isSeenUpdate && projectListAPI.getJobList().getValue() != null && projectListAPI.getJobList().getValue().size() > 0) {
                projectListAPI.getJobList().getValue().get(jobPosition).seen = 0;
                bindAdapter();
                isSeenUpdate = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.rvProjects.removeOnScrollListener(scrollListener);
    }

    public void reset() {
        if (projectListAPI != null) {
            isBidding = false;
            isOffer = false;
            onResume();
        }
    }

    public void refresh() {
        if (projectListAPI != null) {
            projectListAPI.getPageNo().setValue(1);
            scrollListener.resetState();
            projectListAPI.getJobPost(isBidding ? BIDDING : isOffer ? OFFER : AVAILABLE, true, isBidding ? null : isOffer ? null : ((WorkHomeActivity) activity).binding.etSearch.getText().toString());
        }
    }

    public void getPostBySearch() {
        if (projectListAPI != null) {
            projectListAPI.getPageNo().setValue(1);
            scrollListener.resetState();
            projectListAPI.getJobPost(AVAILABLE, true, isBidding ? null : isOffer ? null : ((WorkHomeActivity) activity).binding.etSearch.getText().toString());
        }
    }

    private void newJobClick(int position) {
        this.jobPosition = position;
        isSeenUpdate = true;
    }

    @Override
    public void onJobsResponse(Projects projects, int page) {
        if (page == 1) {
            if (isBidding) {
                ((WorkHomeActivity) activity).getLayoutBinderHelper().setBiddingCount(projects!=null?projects.count:0);
            } else if (!isOffer) {
                ((WorkHomeActivity) activity).getLayoutBinderHelper().setAvailableCount(projects!=null?projects.count:0);
            } else {
                ((WorkHomeActivity) activity).getLayoutBinderHelper().setOfferCount(projects!=null?projects.count:0);
            }
        }
        ((WorkHomeActivity) activity).setFilterResult(false);
    }

    @Override
    public void onClickJob(int jobId, int selectedPos) {

        selectedAdapterPos = selectedPos;
        activity.isClickableView = true;
        jobDetailAPI.getProjectById(jobId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
