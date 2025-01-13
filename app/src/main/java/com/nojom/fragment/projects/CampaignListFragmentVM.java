package com.nojom.fragment.projects;


import static com.nojom.util.Constants.API_FETCH_CAMPAIGN;

import android.app.Application;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.adapter.CampaignAdapter2;
import com.nojom.api.APIRequest;
import com.nojom.api.CampaignListener;
import com.nojom.databinding.FragmentProjectsListBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.CampList;
import com.nojom.model.CampListData;
import com.nojom.model.Projects;
import com.nojom.ui.projects.CampaignDetailActivity2;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class CampaignListFragmentVM extends AndroidViewModel implements CampaignAdapter2.OnClickJobListener,
        CampaignListener {
    private static final String WORK_IN_PROGRESS = "in_progress";
    private static final String PENDING_PROJECT = "pending";
    private static final String HISTORY_PROJECT = "history";
    private FragmentProjectsListBinding binding;
    private BaseFragment fragment;
    private int selectedTab;
    private List<Projects.Data> projectList;
    private List<CampList> campList;
    private CampaignAdapter2 mAdapter;
    private boolean isPullToRefresh = false;
    private int pageNo = 1;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int selectedAdapterPos;
    private int jobId = 0;
    private String gigType = "";

    CampaignListFragmentVM(Application application, FragmentProjectsListBinding projectsListBinding, BaseFragment projectsListFragment) {
        super(application);
        binding = projectsListBinding;
        fragment = projectsListFragment;
        initData();
    }


    private void initData() {

        if (fragment.getArguments() != null) {
            selectedTab = fragment.getArguments().getInt(Constants.IS_WORK_INPROGRESS);
        }

        projectList = new ArrayList<>();
        campList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragment.activity);
        binding.rvProjects.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.rvProjects.getContext(),
//                linearLayoutManager.getOrientation());
//        binding.rvProjects.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    pageNo = page;
//                    ((MyProjectsActivity) fragment.activity).showHideHorizontalProgress(View.VISIBLE);
                    isPullToRefresh = true;
//                    getProjects(isWorkInProgress ? WORK_IN_PROGRESS : PAST_PROJECT);

                    switch (selectedTab) {
                        case 0:
                            fetchCampaign(WORK_IN_PROGRESS);
                            break;
                        case 1:
                            fetchCampaign(PENDING_PROJECT);
                            break;
                        case 2:
                            fetchCampaign(HISTORY_PROJECT);
                            break;
                    }

                }
            }
        };
        binding.shimmerLayout.startShimmer();
//        getProjects(isWorkInProgress ? WORK_IN_PROGRESS : PAST_PROJECT);
        switch (selectedTab) {
            case 0:
                fetchCampaign(WORK_IN_PROGRESS);
                break;
            case 1:
                fetchCampaign(PENDING_PROJECT);
                break;
            case 2:
                fetchCampaign(HISTORY_PROJECT);
                break;
        }

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            isPullToRefresh = false;
            projectList = new ArrayList<>();
            campList = new ArrayList<>();
            pageNo = 1;
            scrollListener.resetState();
//            getProjects(isWorkInProgress ? WORK_IN_PROGRESS : PAST_PROJECT);
            switch (selectedTab) {
                case 0:
                    fetchCampaign(WORK_IN_PROGRESS);
                    break;
                case 1:
                    fetchCampaign(PENDING_PROJECT);
                    break;
                case 2:
                    fetchCampaign(HISTORY_PROJECT);
                    break;
            }
        });

//        binding.noData.btnPostJob.setOnClickListener(v -> fragment.activity.gotoMainActivity(Constants.TAB_POST_JOB));
    }

    private void getProjects(String statId) {
        if (!fragment.activity.isNetworkConnected()) {
            binding.swipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (!isPullToRefresh) {
            binding.rvProjects.setVisibility(View.INVISIBLE);
            binding.noData.llNoData.setVisibility(View.GONE);
            binding.shimmerLayout.startShimmer();
            binding.shimmerLayout.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("page_no", pageNo + "");
        map.put("job_post_type", statId);

//        ApiRequest apiRequest = new ApiRequest();
//        apiRequest.apiRequest(this, fragment.activity, API_GET_JOB_POST, true, map);
    }

    private void fetchCampaign(String statId) {
        if (!fragment.activity.isNetworkConnected()) {
            binding.swipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (!isPullToRefresh) {
            binding.rvProjects.setVisibility(View.INVISIBLE);
            binding.noData.llNoData.setVisibility(View.GONE);
            binding.shimmerLayout.startShimmer();
            binding.shimmerLayout.setVisibility(View.VISIBLE);
        }

//        CampaignType campaignType = new CampaignType(statId);

//        ApiRequest apiRequest = new ApiRequest();
        String url = API_FETCH_CAMPAIGN + statId + "&page=" + pageNo;
//        apiRequest.fetchCampaign(this, fragment.activity, url, campaignType);

        APIRequest apiRequest = new APIRequest();
        apiRequest.fetchCampaign(this, fragment.activity, url, null);
    }

    private void setAdapter() {
        if (campList != null && campList.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            if (mAdapter == null) {
                mAdapter = new CampaignAdapter2((CampaignListFragment) fragment, this);
//                mAdapter.setMode(Attributes.Mode.Single);
            }
            if (pageNo == 1) {
                mAdapter.initList(campList);
            } else {
                mAdapter.doRefresh(campList);
            }

//            mAdapter.mItemManger.closeAllItems();

            if (binding.rvProjects.getAdapter() == null) {
                binding.rvProjects.setAdapter(mAdapter);
            }
        } else {
            binding.noData.llNoData.setVisibility(View.VISIBLE);
            binding.swipeRefreshLayout.setRefreshing(false);
            if (mAdapter != null)
                mAdapter.initList(campList);
        }
        binding.rvProjects.setVisibility(View.VISIBLE);
//        ((MyCampaignActivity) fragment.activity).showHideHorizontalProgress(View.GONE);
    }

    void onResumeMethod() {
        if (scrollListener != null)
            binding.rvProjects.addOnScrollListener(scrollListener);

        String projectId = Preferences.readString(fragment.activity, Constants.PROJECT_ID, "");
        if (!fragment.activity.isEmpty(projectId)) {
            Preferences.writeString(fragment.activity, Constants.PROJECT_ID, "");
            Intent i = new Intent(fragment.activity, ProjectDetailsActivity.class);
            i.putExtra(Constants.PROJECT_ID, Integer.parseInt(projectId));
            fragment.startActivity(i);
        }
        boolean isRefresh = Preferences.readBoolean(fragment.activity, "refresh", false);

        if (isRefresh) {
            isPullToRefresh = false;
            projectList = new ArrayList<>();
            campList = new ArrayList<>();
            pageNo = 1;
            scrollListener.resetState();
//            getProjects(isWorkInProgress ? WORK_IN_PROGRESS : PAST_PROJECT);
            switch (selectedTab) {
                case 0:
                    fetchCampaign(WORK_IN_PROGRESS);
                    break;
                case 1:
                    fetchCampaign(PENDING_PROJECT);
                    break;
                case 2:
                    fetchCampaign(HISTORY_PROJECT);
                    break;
            }

            Preferences.writeBoolean(fragment.activity, "refresh", false);
        }
    }

    void onPauseMethod() {
        if (scrollListener != null)
            binding.rvProjects.removeOnScrollListener(scrollListener);
    }

//    @Override
//    public void successResponse(String responseBody, String url, String message, String data) {
//        if (url.equalsIgnoreCase(API_GET_JOB_POST)) {
//            isPullToRefresh = false;
//            Projects projects = Projects.getJobPostObject(responseBody);
//            if (projects != null && projects.data != null) {
//                projectList.addAll(projects.data);
//            }
//            setAdapter();
//            binding.shimmerLayout.stopShimmer();
//            binding.shimmerLayout.setVisibility(View.GONE);
//            binding.swipeRefreshLayout.setRefreshing(false);
//        } else if (url.equalsIgnoreCase(API_FETCH_CAMPAIGN + pageNo)) {
//            isPullToRefresh = false;
//            CampListResponse projects = CampListResponse.getCampaignDataList(responseBody);
//            if (projects != null && projects.data != null && projects.data.campaigns != null) {
//                campList.addAll(projects.data.campaigns);
//            }
//            setAdapter();
//            binding.shimmerLayout.stopShimmer();
//            binding.shimmerLayout.setVisibility(View.GONE);
//            binding.swipeRefreshLayout.setRefreshing(false);
//        } else if (url.equalsIgnoreCase(API_JOB_DETAILS)) {
//            ProjectByID project = ProjectByID.getJobDetail(responseBody);
//
//            if (project != null) {
//
//                fragment.activity.runOnUiThread(() -> {
//                    mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
//                    mAdapter.notifyItemChanged(selectedAdapterPos);
//                });
//
//                Intent i = new Intent(fragment.activity, ProjectDetailsActivity.class);
//                i.putExtra(Constants.PROJECT, project);
//                i.putExtra("state", selectedTab);
//                fragment.startActivity(i);
//
//            }
//            fragment.activity.isClickableView = false;
//        } else if (url.equalsIgnoreCase(API_GET_CONTRACT_DETAILS + "/" + jobId) || url.equalsIgnoreCase(API_GET_CUSTOM_CONTRACT_DETAILS + "/" + jobId)) {
//            ProjectGigByID project = ProjectGigByID.getProjectGigById(responseBody);
//
//            if (project != null) {
//
//                fragment.activity.runOnUiThread(() -> {
//                    mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
//                    mAdapter.notifyItemChanged(selectedAdapterPos);
//                });
//
//                Intent i = new Intent(fragment.activity, ProjectGigDetailsActivity.class);
//                i.putExtra(Constants.PROJECT_GIG, project);
//                i.putExtra("state", selectedTab);
//                i.putExtra("gigType", gigType);
//                fragment.startActivity(i);
//
//            }
//            fragment.activity.isClickableView = false;
//        }
//    }

    @Override
    public void successResponse(CampListData responseBody, String url, String message) {
//        if (url.equalsIgnoreCase(API_FETCH_CAMPAIGN + pageNo)) {
        isPullToRefresh = false;
        if (responseBody != null && responseBody.campaigns != null) {
            campList.addAll(responseBody.campaigns);
        }
        setAdapter();
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
//        }
    }

    @Override
    public void failureResponse(Throwable throwable, String url, String message) {
        isPullToRefresh = false;
        fragment.activity.isClickableView = false;
        binding.swipeRefreshLayout.setRefreshing(false);
//        ((MyCampaignActivity) fragment.activity).showHideHorizontalProgress(View.GONE);
        setAdapter();
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClickJob(int jpId, int position, String jobType, String gigDataType, CampList campList) {
        selectedAdapterPos = position;
        jobId = jpId;
        gigType = gigDataType;
        if (jobType.contains("job")) {//create job post case
            getProjectById();
        } else {//campaign case
            fragment.activity.runOnUiThread(() -> {
                mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                mAdapter.notifyItemChanged(selectedAdapterPos);
            });

            Intent i = new Intent(fragment.activity, CampaignDetailActivity2.class);
            i.putExtra(Constants.PROJECT, campList);
            i.putExtra("state", selectedTab);
            fragment.startActivity(i);
        }

    }

    void getProjectById() {
        if (!fragment.activity.isNetworkConnected()) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("job_id", jobId + "");

//        ApiRequest apiRequest = new ApiRequest();
//        apiRequest.apiRequest(this, fragment.activity, API_JOB_DETAILS, true, map);
    }

    void getContractDetails() {
        if (!fragment.activity.isNetworkConnected()) {
            return;
        }

//        ApiRequest apiRequest = new ApiRequest();
//
//        if (gigType.equalsIgnoreCase("1") || gigType.equalsIgnoreCase("3")) {
//            apiRequest.apiRequest(this, fragment.activity, API_GET_CUSTOM_CONTRACT_DETAILS + "/" + jobId, false, null);
//        } else {
//            apiRequest.apiRequest(this, fragment.activity, API_GET_CONTRACT_DETAILS + "/" + jobId, false, null);
//        }
    }
}
