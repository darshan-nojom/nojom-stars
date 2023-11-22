package com.nojom.fragment.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProfileResponse;
import com.nojom.model.requestmodel.CommonRequest;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_AGENT_REVIEW;

public class ReviewsProfileFragmentVM extends ViewModel implements APIRequest.APIRequestListener {
    private List<ProfileResponse.ProjectReview> reviewsList = new ArrayList<>();
    private MutableLiveData<List<ProfileResponse.ProjectReview>> listMutableLiveData;
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
}
