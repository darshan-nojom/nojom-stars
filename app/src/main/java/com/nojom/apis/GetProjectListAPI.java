package com.nojom.apis;

import static com.nojom.util.Constants.API_GET_JOBPOST;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.fragment.BaseFragment;
import com.nojom.interfaces.JobListResponseListener;
import com.nojom.model.Projects;
import com.nojom.model.requestmodel.CommonRequest;

import java.util.ArrayList;
import java.util.List;

public class GetProjectListAPI implements APIRequest.APIRequestListener {

    private MutableLiveData<Integer> mPageNoData;
    private List<Projects.Data> projectsList;
    private MutableLiveData<Boolean> isProgress;
    private MutableLiveData<Boolean> somethingWrong;
    private int serviceId = 0;
    private JobListResponseListener jobListResponseListener;
    private MutableLiveData<List<Projects.Data>> mJobList;
    private BaseFragment fragment;

    public MutableLiveData<List<Projects.Data>> getJobList() {
        if (mJobList == null) {
            mJobList = new MutableLiveData<>();
        }
        return mJobList;
    }

    public void init(BaseFragment projectsListFragment) {
        fragment = projectsListFragment;
        mPageNoData = new MutableLiveData<>();
        projectsList = new ArrayList<>();
        isProgress = new MutableLiveData<>();
        somethingWrong = new MutableLiveData<>();
    }


    public void setJobListResponseListener(JobListResponseListener jobListResponseListener) {
        this.jobListResponseListener = jobListResponseListener;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }


    public MutableLiveData<Integer> getPageNo() {
        return mPageNoData;
    }

    public MutableLiveData<Boolean> getIsProgress() {
        return isProgress;
    }

    public MutableLiveData<Boolean> getSomethingWrong() {
        return somethingWrong;
    }


    public void getJobPost(String statId, boolean isPullToRefresh, String searchTag) {
        if (!fragment.activity.isNetworkConnected()) {
            getSomethingWrong().postValue(true);
            return;
        }

        getIsProgress().postValue(isPullToRefresh);

        if (getPageNo().getValue() == null) {
            getPageNo().setValue(1);
        }
        if (getPageNo().getValue() == 1) {
            projectsList = new ArrayList<>();
        }
        //make request
        CommonRequest.JobPostRequest jobPostRequest = new CommonRequest.JobPostRequest();
        jobPostRequest.setJob_post_type(statId);
        jobPostRequest.setPage_no(getPageNo().getValue());
        jobPostRequest.setService_category_id(serviceId);
        jobPostRequest.setSearch_by(searchTag);
        //make api call
        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment.activity, API_GET_JOBPOST, jobPostRequest.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        Projects projects = Projects.getJobPostObject(decryptedData);
        if (projects != null) {
            if (jobListResponseListener != null) {
                if (getPageNo().getValue() != null) {
                    jobListResponseListener.onJobsResponse(projects, getPageNo().getValue());
                } else {
                    jobListResponseListener.onJobsResponse(projects, 0);
                }
            }
            if (projects.data != null) {
                projectsList.addAll(projects.data);
                getJobList().postValue(projectsList);//used when call from background thread
            }
        } else {
            if (getPageNo().getValue() == null || getPageNo().getValue() == 1) {
                fragment.activity.toastMessage(message);
            }
            getJobList().postValue(null);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getSomethingWrong().postValue(true);
        getJobList().postValue(null);
        if (getPageNo().getValue() == null || getPageNo().getValue() == 1) {
//            fragment.activity.toastMessage(message);
            if(jobListResponseListener!=null) {
                jobListResponseListener.onJobsResponse(null, 1);
            }
        }
    }
}
