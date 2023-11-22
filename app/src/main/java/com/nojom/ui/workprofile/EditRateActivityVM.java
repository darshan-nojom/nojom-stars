package com.nojom.ui.workprofile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_UPDATE_PROFILE;

public class EditRateActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private MutableLiveData<Boolean> addRate = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Boolean> getAddRate() {
        return addRate;
    }

    void addPayRate(BaseActivity activity, String rate) {
        if (!activity.isNetworkConnected())
            return;
        getIsShowProgress().postValue(true);
    
        CommonRequest.UpdatePayRate updatePayRate = new CommonRequest.UpdatePayRate();
        updatePayRate.setPay_rate(rate);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updatePayRate.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        getIsShowProgress().postValue(false);
        getAddRate().postValue(true);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}
