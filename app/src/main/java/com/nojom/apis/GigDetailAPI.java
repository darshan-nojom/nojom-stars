package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.GigDetails;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_CUSTOM_GIG_DETAILS;
import static com.nojom.util.Constants.API_GIG_DETAILS;

public class GigDetailAPI implements APIRequest.JWTRequestResponseListener {
    private BaseActivity activity;
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();
    public MutableLiveData<GigDetails> gigDetailsMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<GigDetails> getGigDetailsMutableLiveData() {
        return gigDetailsMutableLiveData;
    }

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getCustomGigDetails(int id) {
        if (!activity.isNetworkConnected())
            return;
        String customGigDetailUrl = API_CUSTOM_GIG_DETAILS + id;
        getIsShowProgress().postValue(2);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, customGigDetailUrl, false, null);
    }

    public void getGigDetails(int id) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(2);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GIG_DETAILS + id, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        GigDetails gigDetails = GigDetails.getGigDetails(responseBody);
        if (gigDetails != null) {
            getGigDetailsMutableLiveData().postValue(gigDetails);
        }
        getIsHideProgress().postValue(2);
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        getIsHideProgress().postValue(2);
    }
}
