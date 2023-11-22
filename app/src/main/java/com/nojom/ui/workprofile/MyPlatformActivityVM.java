package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_SOCIAL_PLATFORMS;
import static com.nojom.util.Constants.API_GET_INF_PLATFORM;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

public class MyPlatformActivityVM extends ViewModel implements APIRequest.APIRequestListener, APIRequest.JWTRequestResponseListener {
    private BaseActivity activity;
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<ArrayList<SocialPlatformResponse.Data>> socialDataList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<SocialPlatformResponse.Data>> getSocialDataList() {
        return socialDataList;
    }

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }


    void addSocialPlatforms(BaseActivity activity, String platformId, String urls, String follow) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.SocialPlatform updateProfileName = new CommonRequest.SocialPlatform();
        updateProfileName.setPlatform_id(platformId);
        updateProfileName.setSocial_platform_url(urls);
        updateProfileName.setSocial_platform_followers(follow);

        this.activity = activity;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_SOCIAL_PLATFORMS, updateProfileName.toString(), true, this);
    }

//    void getSocialPlatforms(BaseActivity activity) {
//        if (!activity.isNetworkConnected())
//            return;
//
//        this.activity = activity;
//
//        APIRequest apiRequest = new APIRequest();
//        apiRequest.makeAPIRequest(activity, API_GET_SOCIAL_PLATFORMS, null, false, this);
//    }

    public void getInfluencerPlatform(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(14);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_INF_PLATFORM, false, null);
    }

    @Override
    public void onResponseSuccess(String responseBody, String urlEndPoint, String msg) {
        /*if (urlEndPoint.equals(API_GET_SOCIAL_PLATFORMS)) {
//            SocialPlatformResponse gigCategories = SocialPlatformResponse.getSocialPlatforms(responseBody);
//            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
//                getSocialDataList().postValue(gigCategories.data);
////                Preferences.saveGigCategory(activity, gigCategories.data);
//            }
//            getIsHideProgress().postValue(14);
        } else*/ if (urlEndPoint.equals(API_ADD_SOCIAL_PLATFORMS)) {
            activity.toastMessage(msg);
            activity.getProfile();
            activity.finish();
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
//        if (urlEndPoint.equals(API_GET_SOCIAL_PLATFORMS)) {
//
//        }
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_INF_PLATFORM)) {
            SocialPlatformResponse gigCategories = SocialPlatformResponse.getSocialPlatforms(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getSocialDataList().postValue(gigCategories.data);
//                Preferences.saveGigCategory(activity, gigCategories.data);
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
