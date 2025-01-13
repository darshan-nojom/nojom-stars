package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.SocialMediaResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.List;

public class ServiceActivityVM extends ViewModel implements Constants, APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public ServiceActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    void addServices(List<SocialMediaResponse.Price> catIds,String desc) {
        if (!activity.isNetworkConnected()) return;

        getIsShowProgress().postValue(true);

        CommonRequest.AddService addSkills = new CommonRequest.AddService();
        addSkills.setPrices(catIds);
        addSkills.setDescription(desc);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_SERVICE, addSkills.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {

        if (urlEndPoint.equalsIgnoreCase(API_ADD_SERVICE)) {
            activity.toastMessage(msg);
            activity.getProfile();
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}
