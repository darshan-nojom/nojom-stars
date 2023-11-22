package com.nojom.ui.gigs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.GigCatCharges;
import com.nojom.model.GigDeliveryTimeModel;
import com.nojom.model.GigRequirementsModel;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.Language;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.nojom.util.Constants.API_ACTIVE_INACTIVE_GIG;
import static com.nojom.util.Constants.API_CREATE_GIG;
import static com.nojom.util.Constants.API_DELETE_GIG;
import static com.nojom.util.Constants.API_EDIT_GIG;
import static com.nojom.util.Constants.API_GET_GIG_DELIVERY_TIME;
import static com.nojom.util.Constants.API_GET_GIG_LANGUAGE;
import static com.nojom.util.Constants.API_GET_GIG_REQUIREMENTS;
import static com.nojom.util.Constants.API_GET_GIG_SERVICE_BY_CAT;
import static com.nojom.util.Constants.API_GIG_CAT_CHARGES;

public class CreateGigsActivityVM extends ViewModel implements APIRequest.JWTRequestResponseListener {
    private BaseActivity activity;
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCreateSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDeleteGigSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> isActiveInactiveSuccess = new MutableLiveData<>();

    private MutableLiveData<ArrayList<GigCatCharges.Data>> gigCatCharges = new MutableLiveData<>();

    private MutableLiveData<ArrayList<GigSubCategoryModel.Data>> gigSubCatList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<GigDeliveryTimeModel.Data>> gigDeliveryList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<GigRequirementsModel.Data>> gigRequirementList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Language.Data>> languageList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Language.Data>> getLanguageList() {
        return languageList;
    }

    public MutableLiveData<ArrayList<GigRequirementsModel.Data>> getGigRequirementList() {
        return gigRequirementList;
    }

    public MutableLiveData<ArrayList<GigDeliveryTimeModel.Data>> getGigDeliveryList() {
        return gigDeliveryList;
    }

    public MutableLiveData<ArrayList<GigSubCategoryModel.Data>> getGigSubCatList() {
        return gigSubCatList;
    }

    public MutableLiveData<Boolean> getIsActiveInactiveSuccess() {
        return isActiveInactiveSuccess;
    }

    public MutableLiveData<Boolean> getIsDeleteGigSuccess() {
        return isDeleteGigSuccess;
    }

    public MutableLiveData<Boolean> getIsCreateSuccess() {
        return isCreateSuccess;
    }

    public MutableLiveData<ArrayList<GigCatCharges.Data>> getGigCatChargesList() {
        return gigCatCharges;
    }

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getGigCatCharges() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GIG_CAT_CHARGES, false, null);
    }


    public void getServiceCategoriesById(int servCatId) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(2);
        String url = API_GET_GIG_SERVICE_BY_CAT + servCatId;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, url, false, null);
    }

    public void getDeliveryTime() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(4);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_DELIVERY_TIME, false, null);
    }

    private int servCatId;

    public void getRequirements(int servCatId) {
        if (!activity.isNetworkConnected())
            return;
        this.servCatId = servCatId;
        getIsShowProgress().postValue(10);
        String url = API_GET_GIG_REQUIREMENTS + servCatId;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, url, false, null);
    }

    public void getGigLanguage() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_LANGUAGE, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_CREATE_GIG) || url.equalsIgnoreCase(API_EDIT_GIG)) {
            getIsCreateSuccess().postValue(true);
            getIsHideProgress().postValue(isLive ? 5 : 6);
        } else if (url.equalsIgnoreCase(API_DELETE_GIG)) {
            getIsDeleteGigSuccess().postValue(true);
            getIsHideProgress().postValue(7);
        } else if (url.equalsIgnoreCase(API_ACTIVE_INACTIVE_GIG)) {
            getIsActiveInactiveSuccess().postValue(true);
            getIsHideProgress().postValue(activeInactiveStatus == 1 ? 9 : 8);
        } else if (url.equalsIgnoreCase(API_GIG_CAT_CHARGES)) {
            GigCatCharges gigCatCharges = GigCatCharges.getGigCatCharges(responseBody);
            if (gigCatCharges != null && gigCatCharges.data != null && gigCatCharges.data.size() > 0) {
                getGigCatChargesList().postValue(gigCatCharges.data);
                Preferences.saveCategoryCharges(activity, gigCatCharges.data);
            }
        } else if (url.equalsIgnoreCase(API_GET_GIG_DELIVERY_TIME)) {
            GigDeliveryTimeModel gigCategories = GigDeliveryTimeModel.getDeliveryTime(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getGigDeliveryList().postValue(gigCategories.data);
                getIsHideProgress().postValue(4);
            }
        } else if (url.equalsIgnoreCase(API_GET_GIG_REQUIREMENTS + servCatId)) {
            GigRequirementsModel gigCategories = GigRequirementsModel.getRequirements(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getGigRequirementList().postValue(gigCategories.data);
                getIsHideProgress().postValue(10);
            }
        } else if (url.equalsIgnoreCase(API_GET_GIG_LANGUAGE)) {
            Language languageList = Language.getLanguages(responseBody);
            if (languageList != null && languageList.data != null && languageList.data.size() > 0) {
                getLanguageList().postValue(languageList.data);
                Preferences.saveGigLanguage(activity, languageList.data);
            }
        } else {
            GigSubCategoryModel gigCategories = GigSubCategoryModel.getGigSubCategories(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getGigSubCatList().postValue(gigCategories.data);
                getIsHideProgress().postValue(2);
            }
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_CREATE_GIG) || url.equalsIgnoreCase(API_EDIT_GIG)) {
            getIsCreateSuccess().postValue(false);
            getIsHideProgress().postValue(isLive ? 5 : 6);
        } else if (url.equalsIgnoreCase(API_DELETE_GIG)) {
            getIsHideProgress().postValue(7);
        } else if (url.equalsIgnoreCase(API_ACTIVE_INACTIVE_GIG)) {
            getIsHideProgress().postValue(activeInactiveStatus == 1 ? 9 : 8);
        } else if (url.equalsIgnoreCase(API_GET_GIG_DELIVERY_TIME)) {
            getIsHideProgress().postValue(4);
        } else if (url.equalsIgnoreCase(API_GET_GIG_REQUIREMENTS + servCatId)) {
            getIsHideProgress().postValue(10);
        } else {
            getIsHideProgress().postValue(2);
        }
    }

    private boolean isLive;
    private int activeInactiveStatus;

    public void createUpdateGig(RequestBody gigTitleBody, RequestBody gigDescBody, RequestBody gigCatIdBody, RequestBody gigSubCatIdBody,
                                RequestBody gigPackagesBody, RequestBody searTagsList, RequestBody status, RequestBody gigId, MultipartBody.Part[] body, RequestBody language, boolean isLive) {
        this.isLive = isLive;
        getIsShowProgress().postValue(isLive ? 5 : 6);
        String endpointUrl;
        if (gigId != null) {//edit case
            endpointUrl = API_EDIT_GIG;
        } else {//new case
            endpointUrl = API_CREATE_GIG;
        }
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiImageUploadRequestBody(this, activity, endpointUrl, body, gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody, status, gigId, searTagsList, language);
    }

    public void duplicateGig(RequestBody gigTitleBody, RequestBody gigDescBody, RequestBody gigCatIdBody, RequestBody gigSubCatIdBody,
                             RequestBody gigPackagesBody, RequestBody searTagsList, RequestBody status, RequestBody gigId, MultipartBody.Part[] body, RequestBody language, RequestBody fileToDelete, RequestBody isDuplicate, boolean isLive) {
        this.isLive = isLive;

        getIsShowProgress().postValue(isLive ? 5 : 6);

        APIRequest apiRequest = new APIRequest();
        apiRequest.gigDuplicateRequest(this, activity, API_CREATE_GIG, body, gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody, status, gigId, searTagsList, language, fileToDelete, isDuplicate);
    }

    public void deleteGig(int gigId) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(7);
        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody gigID = RequestBody.create(String.valueOf(gigId), MultipartBody.FORM);

        map.put("gigID", gigID);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, activity, API_DELETE_GIG, map);
    }

    public void activeInactiveGig(int gigId, int status) {
        if (!activity.isNetworkConnected())
            return;
        activeInactiveStatus = status;
        getIsShowProgress().postValue(status == 1 ? 9 : 8);
        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody gigID = RequestBody.create(String.valueOf(gigId), MultipartBody.FORM);
        RequestBody gigStatus = RequestBody.create("" + status, MultipartBody.FORM);

        map.put("gigID", gigID);
        map.put("status", gigStatus);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, activity, API_ACTIVE_INACTIVE_GIG, map);
    }
}
