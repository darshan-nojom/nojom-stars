package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.Available;
import com.nojom.ui.BaseActivity;

import java.util.List;

import static com.nojom.util.Constants.API_PAY_TYPES;

public class AvailableForWorkActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private MutableLiveData<List<Available.Data>> listMutableLiveData;

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    public MutableLiveData<List<Available.Data>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    void getPayType() {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_PAY_TYPES, null, false, this);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_PAY_TYPES)) {
            List<Available.Data> availableList = Available.getAvailableList(decryptedData);
            if (availableList != null && availableList.size() > 0) {
                getListMutableLiveData().postValue(availableList);
            }
        }
//        activity.hideProgress();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
    }
}
