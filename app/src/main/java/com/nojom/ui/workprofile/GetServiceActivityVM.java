package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.Services;
import com.nojom.model.ServicesData;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

public class GetServiceActivityVM extends ViewModel implements Constants, APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    public MutableLiveData<Services> serviceMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public GetServiceActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }


    public void getServices() {
        if (!activity.isNetworkConnected()) return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.getServices(activity, API_ADD_SERVICE, this);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {

        if (urlEndPoint.equalsIgnoreCase(API_ADD_SERVICE)) {
            Services serviceData = ServicesData.getServiceData(decryptedData);
            if (serviceData != null) {
                serviceMutableLiveData.postValue(serviceData);
            }

        }
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
        serviceMutableLiveData.postValue(null);
    }
}
