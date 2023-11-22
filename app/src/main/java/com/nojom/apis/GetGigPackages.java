package com.nojom.apis;

import static com.nojom.util.Constants.API_GET_GIG_PACKAGE;
import static com.nojom.util.Constants.API_GET_GIG_STAND_SERVICE_CAT;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.GigPackages;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.ArrayList;

public class GetGigPackages implements APIRequest.JWTRequestResponseListener {
    private BaseActivity activity;
    private MutableLiveData<ArrayList<GigPackages.Data>> gigPackageList = new MutableLiveData<>();
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<ArrayList<GigCategoryModel.Data>> gigCategoryList = new MutableLiveData<>();
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }

    public MutableLiveData<ArrayList<GigCategoryModel.Data>> getGigCategoryList() {
        return gigCategoryList;
    }

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<ArrayList<GigPackages.Data>> getGigPackageList() {
        return gigPackageList;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getGigPackages() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(1);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_PACKAGE, false, null);
    }

    public void getStndrdServiceCategories() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(1);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_STAND_SERVICE_CAT, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_GIG_PACKAGE)) {
            GigPackages gigPackages = GigPackages.getGigPackages(responseBody);
            if (gigPackages != null && gigPackages.data != null && gigPackages.data.size() > 0) {
                getGigPackageList().postValue(gigPackages.data);
                Preferences.saveGigPackages(activity, gigPackages.data);
            }
        } else if (url.equalsIgnoreCase(API_GET_GIG_STAND_SERVICE_CAT)) {
            GigCategoryModel gigCategories = GigCategoryModel.getGigCategories(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getGigCategoryList().postValue(gigCategories.data);
                getIsHideProgress().postValue(1);
                Preferences.saveStndrdGigCategory(activity, gigCategories.data);
            }
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_GET_GIG_STAND_SERVICE_CAT)) {
            getIsHideProgress().postValue(1);
        }
    }
}
