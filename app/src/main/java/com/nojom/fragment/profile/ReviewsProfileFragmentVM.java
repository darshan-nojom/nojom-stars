package com.nojom.fragment.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProfileResponse;
import com.nojom.model.SocialPlatformList;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_AGENT_REVIEW;
import static com.nojom.util.Constants.API_GET_SOCIAL_PLATFORM_LIST;

public class ReviewsProfileFragmentVM extends ViewModel implements APIRequest.APIRequestListener, APIRequest.JWTRequestResponseListener {
    private List<ProfileResponse.ProjectReview> reviewsList = new ArrayList<>();
    private MutableLiveData<List<ProfileResponse.ProjectReview>> listMutableLiveData;
    private MutableLiveData<List<SocialPlatformList.Data>> serviceListMutableData;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<List<ProfileResponse.ProjectReview>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public MutableLiveData<List<SocialPlatformList.Data>> getServiceListMutableData() {
        if (serviceListMutableData == null) {
            serviceListMutableData = new MutableLiveData<>();
        }
        return serviceListMutableData;
    }

    public ReviewsProfileFragmentVM() {

    }

    void getAgentReviews(BaseFragment baseFragment, int page) {
        if (!baseFragment.activity.isNetworkConnected())
            return;

        if (page == 1) {
            reviewsList = new ArrayList<>();
            getIsShowProgress().setValue(true);
        }

        CommonRequest.AgentReview agentReview = new CommonRequest.AgentReview();
        agentReview.setPage_no(page);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(baseFragment.activity, API_AGENT_REVIEW, agentReview.toString(), true, this);
    }

    public void getAgentReviews(BaseActivity baseFragment, int page) {
        if (!baseFragment.isNetworkConnected())
            return;

        if (page == 1) {
            reviewsList = new ArrayList<>();
            getIsShowProgress().setValue(true);
        }

        CommonRequest.AgentReview agentReview = new CommonRequest.AgentReview();
        agentReview.setPage_no(page);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(baseFragment, API_AGENT_REVIEW, agentReview.toString(), true, this);
    }

    public void getServiceList(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_SOCIAL_PLATFORM_LIST, false, null);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (ProfileResponse.getAgentReview(decryptedData) != null && ProfileResponse.getAgentReview(decryptedData).size() > 0) {
            reviewsList.addAll(ProfileResponse.getAgentReview(decryptedData));
        }
        getListMutableLiveData().postValue(reviewsList);
        getIsShowProgress().setValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().setValue(false);
        if (reviewsList != null) {
            getListMutableLiveData().postValue(reviewsList);
        }
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        SocialPlatformList gigCatCharges = SocialPlatformList.getSocialPlatforms(responseBody);
        if (gigCatCharges != null && gigCatCharges.data != null && gigCatCharges.data.size() > 0) {
            getServiceListMutableData().postValue(gigCatCharges.data);
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {

    }
}
