package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_UPDATE_PAYTYPE;

public class UpdateTypeAPI implements APIRequest.APIRequestListener {
    private BaseActivity activity;

    private MutableLiveData<Boolean> isShowError = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowError() {
        return isShowError;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void updatePayTypes(int payTypeId, int status) {
        if (!activity.isNetworkConnected()) {
            getIsShowError().postValue(true);
            return;
        }
        CommonRequest.PayTypesRequest payTypesRequest = new CommonRequest.PayTypesRequest();
        payTypesRequest.setPay_type_id(payTypeId);
        payTypesRequest.setStatus(status);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PAYTYPE, payTypesRequest.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        getIsShowError().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowError().postValue(true);
    }
}
