package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.ServicesModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.List;

import static com.nojom.util.Constants.API_SERVICES_CATEGORY;

public class GetServiceCategoryAPI implements APIRequest.APIRequestListener {

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

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        List<ServicesModel.Data> servicesModel = ServicesModel.getServiceData(decryptedData);
        if (servicesModel != null && servicesModel.size() > 0) {
            Preferences.saveTopServices(activity, servicesModel);
            if (getServiceCategoryList() != null) {
                getServiceCategoryList().postValue(servicesModel);
            }
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {

    }
}
