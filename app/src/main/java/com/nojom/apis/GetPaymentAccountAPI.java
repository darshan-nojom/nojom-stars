package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.BankAccounts;
import com.nojom.model.Language;
import com.nojom.model.Payment;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_GET_BANK_ACCOUNTS;

public class GetPaymentAccountAPI implements APIRequest.APIRequestListener {

    private BaseActivity fragment;
    private MutableLiveData<ArrayList<BankAccounts.Data>> listMutableLiveData;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<String> message = new MutableLiveData<>();

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<ArrayList<BankAccounts.Data>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }


    public void init(BaseActivity activity) {
        this.fragment = activity;
    }

    public void getAccounts() {
        if (!fragment.isNetworkConnected()) {
            getIsShowProgress().postValue(false);
            return;
        }

        getIsShowProgress().postValue(true);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment, API_GET_BANK_ACCOUNTS, null, false, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
//        ArrayList<Payment> accounts = Payment.getAccounts(decryptedData);
        List<BankAccounts.Data> bankData = BankAccounts.getBankList(decryptedData);
        ArrayList<BankAccounts.Data> paymentList = new ArrayList<>();
        if (bankData != null && bankData.size() > 0) {
            paymentList = (ArrayList<BankAccounts.Data>) bankData;
        }
        getListMutableLiveData().postValue(paymentList);
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
        getListMutableLiveData().postValue(null);
        getMessage().postValue(message);
    }
}
