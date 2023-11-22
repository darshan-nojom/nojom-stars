package com.nojom.ui.gigs;

import static com.nojom.util.Constants.API_ACTIVE_INACTIVE_GIG;
import static com.nojom.util.Constants.API_CREATE_CUSTOM_GIG;
import static com.nojom.util.Constants.API_CREATE_OFFER;
import static com.nojom.util.Constants.API_DELETE_GIG;
import static com.nojom.util.Constants.API_GET_GIG_CUSTOM_SERVICE_CAT;
import static com.nojom.util.Constants.API_GET_GIG_LANGUAGE;
import static com.nojom.util.Constants.API_GET_GIG_SERVICE_BY_CAT;
import static com.nojom.util.Constants.API_GET_INF_PLATFORM;
import static com.nojom.util.Constants.API_GET_REQUIREMENTS_CATEGORY;
import static com.nojom.util.Constants.API_GIG_CAT_CHARGES;
import static com.nojom.util.Constants.API_UPDATE_CUSTOM_GIG;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.CreateOfferResponse;
import com.nojom.model.GigCatCharges;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.Language;
import com.nojom.model.RequiremetList;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateCustomGigsActivityVM extends ViewModel implements APIRequest.JWTRequestResponseListener {

    private BaseActivity activity;
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCreateSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDeleteGigSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> isActiveInactiveSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgressCreateOffer = new MutableLiveData<>();
    private MutableLiveData<CreateOfferResponse> createOfferSuccess = new MutableLiveData<>();
    private MutableLiveData<ArrayList<GigCatCharges.Data>> gigCatCharges = new MutableLiveData<>();
    private MutableLiveData<ArrayList<GigCategoryModel.Data>> gigCategoryList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<SocialPlatformResponse.Data>> socialDataList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<GigSubCategoryModel.Data>> gigSubCatList = new MutableLiveData<>();
    //    private MutableLiveData<ArrayList<GigDeliveryTimeModel.Data>> gigDeliveryList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Language.Data>> languageList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<RequiremetList.Data>> requirementList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<SocialPlatformResponse.Data>> getSocialDataList() {
        return socialDataList;
    }

    public MutableLiveData<CreateOfferResponse> getCreateOfferSuccess() {
        return createOfferSuccess;
    }

    public MutableLiveData<Boolean> getIsShowProgressCreateOffer() {
        return isShowProgressCreateOffer;
    }

    public MutableLiveData<ArrayList<RequiremetList.Data>> getRequirementList() {
        return requirementList;
    }

    public MutableLiveData<ArrayList<Language.Data>> getLanguageList() {
        return languageList;
    }


//    public MutableLiveData<ArrayList<GigDeliveryTimeModel.Data>> getGigDeliveryList() {
//        return gigDeliveryList;
//    }

    public MutableLiveData<ArrayList<GigSubCategoryModel.Data>> getGigSubCatList() {
        return gigSubCatList;
    }

    public MutableLiveData<ArrayList<GigCategoryModel.Data>> getGigCategoryList() {
        return gigCategoryList;
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

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getGigCatCharges() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GIG_CAT_CHARGES, false, null);
    }

    public void getServiceCategories() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(1);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_CUSTOM_SERVICE_CAT, false, null);
    }

    public void getServiceCategoriesById(int servCatId) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(2);
        String url = API_GET_GIG_SERVICE_BY_CAT + servCatId;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, url, false, null);
    }

    public void getInfluencerPlatform() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(14);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_INF_PLATFORM, false, null);
    }

    /*public void getDeliveryTime() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(4);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_DELIVERY_TIME, false, null);
    }*/


    public void getGigLanguage() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_LANGUAGE, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_CREATE_CUSTOM_GIG) || url.equalsIgnoreCase(API_UPDATE_CUSTOM_GIG)) {
            getIsCreateSuccess().postValue(true);
            getIsHideProgress().postValue(isLive ? 10 : 11);
        } else if (url.equalsIgnoreCase(API_DELETE_GIG)) {
            getIsDeleteGigSuccess().postValue(true);
            getIsHideProgress().postValue(7);
        } else if (url.equalsIgnoreCase(API_ACTIVE_INACTIVE_GIG)) {
            getIsActiveInactiveSuccess().postValue(true);
            getIsHideProgress().postValue(activeInactiveStatus == 1 ? 12 : 13);
        } else if (url.equalsIgnoreCase(API_GIG_CAT_CHARGES)) {
            GigCatCharges gigCatCharges = GigCatCharges.getGigCatCharges(responseBody);
            if (gigCatCharges != null && gigCatCharges.data != null && gigCatCharges.data.size() > 0) {
                getGigCatChargesList().postValue(gigCatCharges.data);
                Preferences.saveCategoryCharges(activity, gigCatCharges.data);
            }
        } else if (url.equalsIgnoreCase(API_GET_GIG_CUSTOM_SERVICE_CAT)) {
            GigCategoryModel gigCategories = GigCategoryModel.getGigCategories(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getGigCategoryList().postValue(gigCategories.data);
//                Preferences.saveGigCategory(activity, gigCategories.data);
            }
            getIsHideProgress().postValue(1);
        } else if (url.equalsIgnoreCase(API_GET_GIG_LANGUAGE)) {
            Language languageList = Language.getLanguages(responseBody);
            if (languageList != null && languageList.data != null && languageList.data.size() > 0) {
                getLanguageList().postValue(languageList.data);
                Preferences.saveGigLanguage(activity, languageList.data);
            }
        } else if (url.equalsIgnoreCase(API_GET_REQUIREMENTS_CATEGORY)) {
            RequiremetList languageList = RequiremetList.getRequirement(responseBody);
            if (languageList != null && languageList.data != null && languageList.data.size() > 0) {
                getRequirementList().postValue(languageList.data);
            }
            getIsHideProgress().postValue(9);
        } else if (url.equalsIgnoreCase(API_CREATE_OFFER)) {
            activity.toastMessage(message);
            CreateOfferResponse createOffer = CreateOfferResponse.getCreateOffer(responseBody);
            if (createOffer != null) {
                getCreateOfferSuccess().postValue(createOffer);
            }
            getIsShowProgressCreateOffer().postValue(false);
        } else if (url.equalsIgnoreCase(API_GET_INF_PLATFORM)) {
            SocialPlatformResponse gigCategories = SocialPlatformResponse.getSocialPlatforms(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getSocialDataList().postValue(gigCategories.data);
//                Preferences.saveGigCategory(activity, gigCategories.data);
            }
            getIsHideProgress().postValue(14);
        } else {
            GigSubCategoryModel gigCategories = GigSubCategoryModel.getGigSubCategories(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getGigSubCatList().postValue(gigCategories.data);
            }
            getIsHideProgress().postValue(2);
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_CREATE_CUSTOM_GIG) || url.equalsIgnoreCase(API_UPDATE_CUSTOM_GIG)) {
            getIsCreateSuccess().postValue(false);
            getIsHideProgress().postValue(isLive ? 10 : 11);
        } else if (url.equalsIgnoreCase(API_DELETE_GIG)) {
            getIsHideProgress().postValue(7);
        } else if (url.equalsIgnoreCase(API_ACTIVE_INACTIVE_GIG)) {
            getIsHideProgress().postValue(activeInactiveStatus == 1 ? 12 : 13);
        } else if (url.equalsIgnoreCase(API_GET_GIG_CUSTOM_SERVICE_CAT)) {
            getIsHideProgress().postValue(1);
        } else if (url.equalsIgnoreCase(API_GET_REQUIREMENTS_CATEGORY)) {
            getIsHideProgress().postValue(9);
        } else if (url.equalsIgnoreCase(API_CREATE_OFFER)) {
            activity.toastMessage(message);
            getIsShowProgressCreateOffer().postValue(false);
        } else if (url.equalsIgnoreCase(API_GET_INF_PLATFORM)) {
            getIsHideProgress().postValue(14);
        } else {
            getIsHideProgress().postValue(2);
        }
    }

    private boolean isLive;
    private int activeInactiveStatus;

    public void createUpdateGig(RequestBody gigTitleBody, RequestBody gigDescBody, RequestBody gigCatIdBody, RequestBody gigSubCatIdBody,
                                RequestBody gigPackagesBody, RequestBody gigOtherBody, RequestBody searTagsList, RequestBody status,
                                RequestBody gigId, MultipartBody.Part[] body, RequestBody language, boolean isLive, RequestBody profileBody
            , RequestBody deadlineArray, RequestBody mainPriceBody, RequestBody deadlineDesc, RequestBody platformBody) {
        this.isLive = isLive;
        getIsShowProgress().postValue(isLive ? 10 : 11);
        String endpointUrl;
        if (gigId != null) {//edit case
            endpointUrl = API_UPDATE_CUSTOM_GIG;
        } else {//new case
            endpointUrl = API_CREATE_CUSTOM_GIG;
        }
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiImageUploadRequestBodyCustomGig(this, activity, endpointUrl, body, gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody,
                gigOtherBody, status, gigId, searTagsList, language, profileBody, deadlineArray, mainPriceBody, deadlineDesc,platformBody);
    }

    public void duplicateGig(RequestBody gigTitleBody, RequestBody gigDescBody, RequestBody gigCatIdBody, RequestBody gigSubCatIdBody,
                             RequestBody gigPackagesBody, RequestBody gigOtherReqBody, RequestBody searTagsList, RequestBody status, RequestBody gigId, MultipartBody.Part[] body, RequestBody language, RequestBody fileToDelete, RequestBody isDuplicate, boolean isLive, RequestBody profileBody, RequestBody deadlineType
            , RequestBody mainPriceBody, RequestBody deadlineDesc, RequestBody platformBody) {
        this.isLive = isLive;

        getIsShowProgress().postValue(isLive ? 10 : 11);

        APIRequest apiRequest = new APIRequest();
        apiRequest.gigDuplicateRequestCustomGig(this, activity, API_CREATE_CUSTOM_GIG, body, gigTitleBody, gigDescBody, gigCatIdBody, gigSubCatIdBody, gigPackagesBody, gigOtherReqBody, status, gigId, searTagsList, language, fileToDelete, isDuplicate, profileBody, deadlineType, mainPriceBody, deadlineDesc,platformBody);
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
        getIsShowProgress().postValue(status == 1 ? 12 : 13);
        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody gigID = RequestBody.create(String.valueOf(gigId), MultipartBody.FORM);
        RequestBody gigStatus = RequestBody.create("" + status, MultipartBody.FORM);

        map.put("gigID", gigID);
        map.put("status", gigStatus);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, activity, API_ACTIVE_INACTIVE_GIG, map);
    }

    public void getRequirementById(int servCatId, boolean isRefresh) {
        if (!activity.isNetworkConnected())
            return;

        if (isRefresh) {
            getIsShowProgress().postValue(9);
        }

        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody serCatIdBody = RequestBody.create("" + servCatId, MultipartBody.FORM);

        map.put("parentServiceCategoryID", serCatIdBody);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, activity, API_GET_REQUIREMENTS_CATEGORY, map);
    }

    public void createOfferGig(HashMap<String, RequestBody> map) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgressCreateOffer().postValue(true);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, activity, API_CREATE_OFFER, map);
    }
}
