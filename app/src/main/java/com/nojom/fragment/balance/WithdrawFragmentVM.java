package com.nojom.fragment.balance;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.Withdrawal;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_GET_WITHDRAWAL;

public class WithdrawFragmentVM extends ViewModel implements APIRequest.APIRequestListener {
    private BaseFragment fragment;
    private MutableLiveData<List<Withdrawal>> listMutableLiveData;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<List<Withdrawal>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public WithdrawFragmentVM() {

    }

    public void init(BaseFragment fragment) {
        this.fragment = fragment;
    }


    void getWithdrawals() {
        if (!fragment.activity.isNetworkConnected()) {
            getIsShowProgress().postValue(false);
            return;
        }

        getIsShowProgress().postValue(true);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment.activity, API_GET_WITHDRAWAL, null, false, this);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        List<Withdrawal> model = Withdrawal.getWithdrawals(decryptedData);
        List<Withdrawal> withdrawList = new ArrayList<>();
        if (model != null) {
            withdrawList = model;
        }
        getListMutableLiveData().postValue(withdrawList);
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
        getListMutableLiveData().postValue(null);
    }
}
