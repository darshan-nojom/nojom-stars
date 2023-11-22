package com.nojom.ui.gigs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;
import com.nojom.model.GigProjectList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.nojom.util.Constants.API_CONTRACT_DETAIL;
import static com.nojom.util.Constants.API_CUSTOM_CONTRACT_DETAIL;
import static com.nojom.util.Constants.API_GET_CONTRACT_LIST;

public class GigProjectListFragmentVM extends ViewModel implements APIRequest.JWTRequestResponseListener {
    private BaseFragment fragment;
    private MutableLiveData<List<GigProjectList.Data>> mJobList;
    private MutableLiveData<ContractDetails> contractDetails;
    private MutableLiveData<Boolean> isProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> somethingWrong = new MutableLiveData<>();
    private MutableLiveData<Boolean> contractDetailFail = new MutableLiveData<>();
    private MutableLiveData<Integer> mPageNoData = new MutableLiveData<>();
    private List<GigProjectList.Data> gigProjectList = new ArrayList<>();

    public GigProjectListFragmentVM() {

    }

    public MutableLiveData<Boolean> getContractDetailFail() {
        return contractDetailFail;
    }

    public MutableLiveData<ContractDetails> getContractDetails() {
        if (contractDetails == null) {
            contractDetails = new MutableLiveData<>();
        }
        return contractDetails;
    }

    public MutableLiveData<Integer> getPageNo() {
        return mPageNoData;
    }

    public MutableLiveData<Boolean> getIsProgress() {
        return isProgress;
    }

    public MutableLiveData<Boolean> getSomethingWrong() {
        return somethingWrong;
    }

    public MutableLiveData<List<GigProjectList.Data>> getJobList() {
        if (mJobList == null) {
            mJobList = new MutableLiveData<>();
        }
        return mJobList;
    }

    public void init(BaseFragment projectsListFragment) {

        fragment = projectsListFragment;
    }

    void getProjectList(String statId, boolean isPullToRefresh, int gigId) {
        if (!fragment.activity.isNetworkConnected()) {
            getSomethingWrong().postValue(true);
            return;
        }

        getIsProgress().postValue(isPullToRefresh);

        if (getPageNo().getValue() == null) {
            getPageNo().setValue(1);
        }

        if (getPageNo().getValue() == 1) {
            gigProjectList = new ArrayList<>();
        }

        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody contractType = RequestBody.create(statId, MultipartBody.FORM);
        RequestBody pageNo = RequestBody.create(String.valueOf(getPageNo().getValue()), MultipartBody.FORM);
        RequestBody gigIdBody = RequestBody.create("" + gigId, MultipartBody.FORM);

        map.put("contractType", contractType);
        map.put("gigID", gigIdBody);
        map.put("pageNo", pageNo);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, fragment.activity, API_GET_CONTRACT_LIST, map);
    }

    void getContractById(int contractId) {

        if (!fragment.activity.isNetworkConnected()) {
            return;
        }

        String url = API_CONTRACT_DETAIL + contractId;
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, fragment.activity, url, false, null);
    }

    String customContractUrl = "";

    void getCustomContractById(int contractId) {

        if (!fragment.activity.isNetworkConnected()) {
            return;
        }

        customContractUrl = API_CUSTOM_CONTRACT_DETAIL + contractId;
        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, fragment.activity, customContractUrl, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_CONTRACT_LIST)) {
            GigProjectList gigPackages = GigProjectList.getGigProjectList(responseBody);
            if (gigPackages != null && gigPackages.data != null && gigPackages.data.size() > 0) {
                gigProjectList.addAll(gigPackages.data);
            }
            getJobList().postValue(gigProjectList);
            getIsProgress().postValue(false);
        } else if (customContractUrl.equalsIgnoreCase(url)) {
            ContractDetails project = ContractDetails.getContractDetails(responseBody);
            getContractDetails().postValue(project);
        } else {
            ContractDetails project = ContractDetails.getContractDetails(responseBody);
            getContractDetails().postValue(project);
        }
        fragment.activity.isClickableView = false;
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        fragment.activity.isClickableView = false;
        getSomethingWrong().postValue(true);
        getIsProgress().postValue(false);

        if (url.equalsIgnoreCase(API_CONTRACT_DETAIL)) {
            getContractDetailFail().postValue(true);
        }
    }
}
