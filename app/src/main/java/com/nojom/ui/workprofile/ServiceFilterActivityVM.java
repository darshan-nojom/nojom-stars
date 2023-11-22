package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_GET_INF_PLATFORM;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.ServicesModel;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ServiceFilterActivityVM extends ViewModel implements APIRequest.JWTRequestResponseListener {

    private MutableLiveData<List<ServicesModel.Data>> lisDataMutableLiveData;
    private MutableLiveData<Integer> selectedPos = new MutableLiveData<>();

    public MutableLiveData<Integer> getSelectedPos() {
        return selectedPos;
    }
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    private MutableLiveData<ArrayList<SocialPlatformResponse.Data>> socialDataList = new MutableLiveData<>();

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }

    public MutableLiveData<ArrayList<SocialPlatformResponse.Data>> getSocialDataList() {
        return socialDataList;
    }

    public MutableLiveData<List<ServicesModel.Data>> getLisDataMutableLiveData() {
        if (lisDataMutableLiveData == null) {
            lisDataMutableLiveData = new MutableLiveData<>();
        }
        return lisDataMutableLiveData;
    }

    BaseActivity activity;

    void init(BaseActivity activity) {
        this.activity = activity;

//        int serviceId = Preferences.readInteger(activity, Constants.FILTER_ID, 0);
//
//        List<ServicesModel.Data> servicesList = Preferences.getTopServices(activity);
//        try {
//            if (servicesList != null && servicesList.size() > 0) {
//
//                for (ServicesModel.Data item : servicesList) {
//                    if (item.id == 5) {//writing & editing
//                        servicesList.remove(item);
//                        break;
//                    }
//                }
//
//                ServicesModel.Data otherData = servicesList.get(servicesList.size() - 1);
//                servicesList.remove(servicesList.size() - 1);
//                servicesList.add(0, otherData);
//
//                for (int i = 0; i < servicesList.size(); i++) {
//                    if (servicesList.get(i).id == serviceId) {
//                        getSelectedPos().postValue(i);
//                        break;
//                    }
//                }
//            }
//            getLisDataMutableLiveData().postValue(servicesList);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        getInfluencerPlatform();
    }

    public void getInfluencerPlatform() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(14);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_INF_PLATFORM, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_INF_PLATFORM)) {
            SocialPlatformResponse gigCategories = SocialPlatformResponse.getSocialPlatforms(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getSocialDataList().postValue(gigCategories.data);
            }
            getIsHideProgress().postValue(14);
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_GET_INF_PLATFORM)) {
            getIsHideProgress().postValue(14);
        }
    }
}
