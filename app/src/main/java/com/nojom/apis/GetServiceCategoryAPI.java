package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.ServicesModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_GET_GIG_SERVICE_BY_CAT;
import static com.nojom.util.Constants.API_SERVICES_CATEGORY;

public class GetServiceCategoryAPI implements APIRequest.APIRequestListener, APIRequest.JWTRequestResponseListener {

    private BaseActivity activity;
    private MutableLiveData<List<ServicesModel.Data>> serviceCategoryList;

    public MutableLiveData<List<ServicesModel.Data>> getServiceCategoryList() {
        return serviceCategoryList;
    }

    public void setServiceCategoryList() {
        this.serviceCategoryList = new MutableLiveData<>();
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getServiceCategories() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_SERVICES_CATEGORY, null, false, this);
    }

    public void getServiceCategoriesById(int servCatId) {
        if (!activity.isNetworkConnected())
            return;

        String url = API_GET_GIG_SERVICE_BY_CAT + servCatId;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, url, false, null);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        List<ServicesModel.Data> servicesModel = ServicesModel.getServiceData(decryptedData);
        if (servicesModel != null && servicesModel.size() > 0) {
            Preferences.saveTopServices(activity, servicesModel);
            if (getServiceCategoryList() != null) {
                getServiceCategoryList().postValue(servicesModel);
            }
        }
        activity.disableEnableTouch(false);
        activity.isClickableView=false;
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        activity.isClickableView=false;
        activity.disableEnableTouch(false);
    }

    private MutableLiveData<ArrayList<GigSubCategoryModel.Data>> gigSubCatList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<GigSubCategoryModel.Data>> getGigSubCatList() {
        return gigSubCatList;
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        GigSubCategoryModel gigCategories = GigSubCategoryModel.getGigSubCategories(responseBody);
        if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
            Preferences.setInflSubCategory(activity,gigCategories.data);
            getGigSubCatList().postValue(gigCategories.data);
        }
        activity.disableEnableTouch(false);
        activity.isClickableView=false;
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        activity.toastMessage(message);
        activity.isClickableView=false;
        activity.disableEnableTouch(false);
    }
}
