package com.nojom.ui.gigs;

import static com.nojom.util.Constants.API_DELETE_GIG;
import static com.nojom.util.Constants.API_GET_GIG_LIST;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.GigDetails;
import com.nojom.model.GigList;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ActiveGigsFragmentVM extends ViewModel implements APIRequest.JWTRequestResponseListener {
    private BaseActivity fragment;
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> mPageNoData = new MutableLiveData<>();
    private MutableLiveData<GigList> gigList = new MutableLiveData<>();
    private MutableLiveData<List<GigList.Data>> gigDataList = new MutableLiveData<>();
    public MutableLiveData<GigDetails> gigDetailsMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<GigDetails> getGigDetailsMutableLiveData() {
        return gigDetailsMutableLiveData;
    }

    private List<GigList.Data> dataList;
    private MutableLiveData<Boolean> isDeleteGigSuccess = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsDeleteGigSuccess() {
        return isDeleteGigSuccess;
    }

    public MutableLiveData<List<GigList.Data>> getGigDataList() {
        return gigDataList;
    }

    public MutableLiveData<GigList> getGigList() {
        return gigList;
    }

    public MutableLiveData<Integer> getPageNoData() {
        return mPageNoData;
    }

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }

    public void init(BaseFragment activity) {
        this.fragment = activity.activity;
    }

    public void init(BaseActivity activity) {
        this.fragment = activity;
    }

    public void getActiveGig(boolean isShowProgress, int screen) {
        if (!fragment.isNetworkConnected())
            return;

        if (isShowProgress) {
            getIsShowProgress().postValue(1);
        }

        if (getPageNoData().getValue() == null) {
            getPageNoData().setValue(1);
        }

        if (getPageNoData().getValue() == 1) {
            dataList = new ArrayList<>();
        }

        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody status = RequestBody.create(String.valueOf(screen), MultipartBody.FORM);
        RequestBody pageNo = RequestBody.create(String.valueOf(getPageNoData().getValue()), MultipartBody.FORM);
        map.put("status", status);
        map.put("pageNo", pageNo);

        if (getPageNoData().getValue() == null) {
            getPageNoData().setValue(1);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, fragment, API_GET_GIG_LIST, map);
    }


    public void deleteGig(int gigId) {
        if (!fragment.isNetworkConnected())
            return;

        getIsShowProgress().postValue(7);
        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody gigID = RequestBody.create(String.valueOf(gigId), MultipartBody.FORM);

        map.put("gigID", gigID);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, fragment, API_DELETE_GIG, map);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_GIG_LIST)) {
            GigList gigPackages = GigList.getGigList(responseBody);
            if (gigPackages != null && gigPackages.data != null && gigPackages.data.size() > 0) {
                dataList.addAll(gigPackages.data);
                getGigList().postValue(gigPackages);
            }
            getGigDataList().setValue(dataList);
            getIsHideProgress().postValue(1);
        } else if (url.equalsIgnoreCase(API_DELETE_GIG)) {
            getIsDeleteGigSuccess().postValue(true);
            getIsHideProgress().postValue(7);
        } else {
            GigDetails gigDetails = GigDetails.getGigDetails(responseBody);
            if (gigDetails != null) {
                getGigDetailsMutableLiveData().postValue(gigDetails);
            }
            getIsHideProgress().postValue(2);
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_GET_GIG_LIST)) {
            getIsHideProgress().postValue(1);
        } else if (url.equalsIgnoreCase(API_DELETE_GIG)) {
            getIsHideProgress().postValue(7);
        } else {
            getIsHideProgress().postValue(2);
        }
    }
}
