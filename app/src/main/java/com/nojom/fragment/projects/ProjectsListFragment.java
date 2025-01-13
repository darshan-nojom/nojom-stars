package com.nojom.fragment.projects;

import static com.nojom.util.Constants.API_CONTRACT_DETAIL;
import static com.nojom.util.Constants.API_CUSTOM_CONTRACT_DETAIL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nojom.R;
import com.nojom.adapter.ProjectsAdapter;
import com.nojom.api.APIRequest;
import com.nojom.apis.GetProjectListAPI;
import com.nojom.apis.JobDetailAPI;
import com.nojom.databinding.FragmentProjectsListBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;
import com.nojom.model.Projects;
import com.nojom.ui.balance.BalanceActivity;
import com.nojom.ui.gigs.ContractDetailsActivity;
import com.nojom.ui.projects.MyProjectsActivity;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.Preferences;

import java.util.ArrayList;

public class ProjectsListFragment extends BaseFragment implements ProjectsAdapter.JobClickListener, APIRequest.JWTRequestResponseListener {

    public static ProjectsListFragment newInstance(boolean isWorkInProgress) {
        ProjectsListFragment fragment = new ProjectsListFragment();
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_WORK_INPROGRESS, isWorkInProgress);
        fragment.setArguments(args);
        return fragment;
    }

    private GetProjectListAPI getProjectListAPI;
    private FragmentProjectsListBinding binding;
    private boolean isWorkInProgress;
    private ProjectsAdapter mAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    private static final String IN_PROGRESS = "IN-PROGRESS";
    private static final String PAST_PROJECT = "PAST-PROJECT";
    private int selectedAdapterPos;
    private JobDetailAPI jobDetailAPI;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_projects_list, container, false);

        getProjectListAPI = new GetProjectListAPI();
        getProjectListAPI.init(ProjectsListFragment.this);
        getProjectListAPI.setJobListResponseListener(null);
        jobDetailAPI = new JobDetailAPI();
        jobDetailAPI.init(activity);
        initData();
        return binding.getRoot();
    }

    private void initData() {
        binding.shimmerLayoutProjects.setVisibility(View.GONE);
        binding.shimmerLayout.setVisibility(View.VISIBLE);

        if (getArguments() != null) {
            isWorkInProgress = getArguments().getBoolean(Constants.IS_WORK_INPROGRESS);
        }

        binding.noData.tvNoTitle.setText(getString(isWorkInProgress ? R.string.no_inprogress_jobs : R.string.no_past_jobs));
        binding.noData.tvNoDescription.setText(getString(isWorkInProgress ? R.string.no_inprogress_jobs_desc : R.string.no_past_jobs_desc));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvProjects.setLayoutManager(linearLayoutManager);

        //scroll more listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    if (activity instanceof MyProjectsActivity) {
                        ((MyProjectsActivity) activity).showHideHorizontalProgress(View.VISIBLE);
                    }
                    getProjectListAPI.getPageNo().setValue(page);
                    getProjectListAPI.getJobPost(isWorkInProgress ? IN_PROGRESS : PAST_PROJECT, false, null);
                }
            }
        };

        //Observer call when need to show progress [Shimmer]
        getProjectListAPI.getIsProgress().observe(activity, isRefresh -> {
            if (isRefresh) {

                binding.rvProjects.setVisibility(View.INVISIBLE);
                binding.noData.llNoData.setVisibility(View.GONE);
                binding.shimmerLayout.startShimmer();
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            } else {

                binding.rvProjects.setVisibility(View.VISIBLE);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                if (mAdapter == null || mAdapter.getItemCount() == 0) {
                    binding.noData.llNoData.setVisibility(View.VISIBLE);
                }
            }

        });

        //call to get data from server
        getProjectListAPI.getJobPost(isWorkInProgress ? IN_PROGRESS : PAST_PROJECT, true, null);

        //swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        //observer to used when there are any changes in list
        getProjectListAPI.getJobList().observe(activity, data -> bindAdapter());

        //Observer call when something wrong [Shimmer]
        getProjectListAPI.getSomethingWrong().observe(activity, isWrong -> {
            if (isWrong) {//something went wrong
                binding.swipeRefreshLayout.setRefreshing(false);
                if (activity instanceof MyProjectsActivity) {
                    ((MyProjectsActivity) activity).showHideHorizontalProgress(View.GONE);
                }
            }
        });

        jobDetailAPI.getProjectById().observe(activity, projectByID -> {

            activity.runOnUiThread(() -> {
                if (mAdapter != null && mAdapter.getData() != null && mAdapter.getData().size() > 0) {
                    mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                    mAdapter.notifyItemChanged(selectedAdapterPos);
                }
            });

            if (projectByID != null && isAdded()) {
                Intent i = new Intent(getActivity(), ProjectDetailsActivity.class);
                i.putExtra(Constants.PROJECT, projectByID);
                startActivity(i);
            }
        });

        jobDetailAPI.getIsShowProgress().observe(activity, aBoolean -> {
            try {
                mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                mAdapter.notifyItemChanged(selectedAdapterPos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void bindAdapter() {
        if (getProjectListAPI.getJobList().getValue() != null && getProjectListAPI.getJobList().getValue().size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new ProjectsAdapter(activity, this);
                mAdapter.doRefresh(getProjectListAPI.getJobList().getValue());
                binding.rvProjects.setAdapter(mAdapter);
            } else {
                mAdapter.doRefresh(getProjectListAPI.getJobList().getValue());
            }
        } else {
            if (getProjectListAPI.getPageNo() != null
                    && getProjectListAPI.getPageNo().getValue() != null
                    && getProjectListAPI.getPageNo().getValue() == 1
                    && mAdapter != null) {
                mAdapter.doRefresh(new ArrayList<>());
                mAdapter = null;
            }
            if (mAdapter == null || mAdapter.getItemCount() == 0) {
                binding.noData.llNoData.setVisibility(View.VISIBLE);
            }
        }

        if (mAdapter != null) {
            mAdapter.setState(isWorkInProgress);
        }
        binding.swipeRefreshLayout.setRefreshing(false);
        getProjectListAPI.getIsProgress().setValue(false);
        if (activity instanceof MyProjectsActivity) {
            ((MyProjectsActivity) activity).showHideHorizontalProgress(View.GONE);
        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getProjectListAPI.getPageNo().setValue(1);
            scrollListener.resetState();
            getProjectListAPI.getJobPost(isWorkInProgress ? IN_PROGRESS : PAST_PROJECT, false, null);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (scrollListener != null)
            binding.rvProjects.addOnScrollListener(scrollListener);

        String projectId = Preferences.readString(activity, Constants.PROJECT_ID, "");
        if (!activity.isEmpty(projectId)) {
            Preferences.writeString(activity, Constants.PROJECT_ID, "");
            Intent i = new Intent(activity, ProjectDetailsActivity.class);
            i.putExtra(Constants.PROJECT_ID, Integer.parseInt(projectId));
            startActivity(i);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.rvProjects.removeOnScrollListener(scrollListener);
    }

    @Override
    public void onClickJob(Projects.Data data, int jobId, int selctedPos, String jobType, String gigType) {
        selectedAdapterPos = selctedPos;
        activity.isClickableView = true;
        try {
            if (jobType.equalsIgnoreCase("paid")) {//new detail screen
                activity.isClickableView = false;
                activity.runOnUiThread(() -> {
                    mAdapter.getData().get(selectedAdapterPos).isShowProgress = false;
                    mAdapter.notifyItemChanged(selectedAdapterPos);
                });
                Intent intent = new Intent(activity, CampaignDetailActivity.class);
                intent.putExtra(Constants.PROJECT, data);
                activity.startActivity(intent);
            } else {//other
                jobDetailAPI.getProjectById(jobId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                i.putExtra("gigtype", project.gigType);//custom gig=1
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
            activity.isClickableView = false;
            if (activity instanceof BalanceActivity) {
                ((BalanceActivity) activity).hideShowHorizontalProgressBar(View.GONE);
            }
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
