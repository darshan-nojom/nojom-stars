package com.nojom.ui.workprofile;

import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_UPDATE_PROFILE;

public class PayRateActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private ResponseListener responseListener;

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void addPayRate(BaseActivity activity, String rate) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.UpdatePayRate updatePayRate = new CommonRequest.UpdatePayRate();
        updatePayRate.setPay_rate(rate);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updatePayRate.toString(), true, this);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (responseListener != null) {
            responseListener.onResponseSuccess(null);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (responseListener != null) {
            responseListener.onError();
        }
    }
}
