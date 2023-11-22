package com.nojom.apis;

import static com.nojom.util.Constants.API_JOB_DETAIL;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.ProjectByID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

public class JobDetailAPI implements APIRequest.APIRequestListener {
    private BaseActivity activity;
    private MutableLiveData<ProjectByID> projectById = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<ProjectByID> getProjectById() {
        return projectById;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getProjectById(int projectId) {

        if (!activity.isNetworkConnected()) {
            return;
        }


        getIsShowProgress().postValue(true);

        CommonRequest.JobDetail jobDetail = new CommonRequest.JobDetail();
        jobDetail.setJob_post_id(projectId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_JOB_DETAIL, jobDetail.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        activity.isClickableView = false;
        if (urlEndPoint.equalsIgnoreCase(API_JOB_DETAIL)) {
            ProjectByID project = ProjectByID.getJobDetail(decryptedData);

            getProjectById().postValue(project);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        activity.isClickableView = false;
        getIsShowProgress().postValue(false);
    }
}
