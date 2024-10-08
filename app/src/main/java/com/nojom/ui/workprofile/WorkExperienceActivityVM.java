package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_ADD_EXPERTISE;

public class WorkExperienceActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<List<String>> listMutableLiveData;
    private ResponseListener responseListener;

    public MutableLiveData<List<String>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
        initData();
    }

    private void initData() {

        List<String> expList = new ArrayList<>();
        expList.add(activity.getString(R.string.less_than_1_year));
        expList.add(activity.getString(R.string.year_1_3));
        expList.add(activity.getString(R.string.year_4_6));
        expList.add(activity.getString(R.string.year_7_9));
        expList.add(activity.getString(R.string.year_10_12));
        expList.add(activity.getString(R.string.year_13_15));
        expList.add(activity.getString(R.string.year_16_18));
        expList.add(activity.getString(R.string.year_19_21));
        expList.add(activity.getString(R.string.year_22_24));
        expList.add(activity.getString(R.string.year_25_27));
        expList.add(activity.getString(R.string.year_28_30));
        expList.add(activity.getString(R.string.year_31));

        getListMutableLiveData().postValue(expList);

    }

    void updateExperience(String experience, String serviceIds) {
        if (!activity.isNetworkConnected())
            return;
        activity.disableEnableTouch(true);

        CommonRequest.ExpertiseRequest expertiseRequest = new CommonRequest.ExpertiseRequest();
        expertiseRequest.setExperience(experience);
        expertiseRequest.setService_category_id(serviceIds);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_EXPERTISE, expertiseRequest.toString(), true, this);
    }

    void updateExperience(int pubStatus) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.ExpertiseRequest expertiseRequest = new CommonRequest.ExpertiseRequest();
        expertiseRequest.setExperience("");
        expertiseRequest.setService_category_id("");
        expertiseRequest.setCategory_public_status(pubStatus);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_EXPERTISE, expertiseRequest.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (responseListener != null) {
            responseListener.onResponseSuccess(null);
        }
//        activity.hideProgress();
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (responseListener != null) {
            responseListener.onError();
        }
        activity.disableEnableTouch(false);
    }
}
