package com.nojom.ui.balance;

import static com.nojom.util.Constants.API_ADD_BANK;
import static com.nojom.util.Constants.API_DELETE_BANK_NEW;
import static com.nojom.util.Constants.API_GET_ACCOUNTS;
import static com.nojom.util.Constants.API_GET_HISTORY;
import static com.nojom.util.Constants.API_GET_WALLET;
import static com.nojom.util.Constants.API_GET_WALLET_TXN;
import static com.nojom.util.Constants.API_UPDATE_BANK;
import static com.nojom.util.Constants.API_WITHDRAW_AMOUNT;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.api.CampaignListener;
import com.nojom.api.WalletListener;
import com.nojom.model.AddCard;
import com.nojom.model.CampListData;
import com.nojom.model.UpdateCard;
import com.nojom.model.WalletData;
import com.nojom.model.WithdrawAmount;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.List;

public class CampByIdVM extends AndroidViewModel implements CampaignListener, WalletListener {
    @SuppressLint("StaticFieldLeak")
    private final BaseActivity activity;
    public MutableLiveData<Boolean> mutableProgress = new MutableLiveData<>();
    public MutableLiveData<List<WalletData>> mpWalletData = new MutableLiveData<>();
    public MutableLiveData<WalletData> mpWalletBalanceData = new MutableLiveData<>();
    public MutableLiveData<WalletData> bankAccountList = new MutableLiveData<>();

    public CampByIdVM(Application application, BaseActivity freelancerProfileActivity) {
        super(application);
        activity = freelancerProfileActivity;
    }

    @Override
    public void successResponse(CampListData responseBody, String url, String message) {
        //activity.toastMessage(message);
        switch (url) {
            case API_WITHDRAW_AMOUNT:
            case API_DELETE_BANK_NEW:
            case API_ADD_BANK:
            case API_UPDATE_BANK:
                activity.toastMessage(message);
                activity.finish();
                break;
        }
        activity.isClickableView = false;
        mutableProgress.postValue(false);
    }

    @Override
    public void successResponse(WalletData responseBody, String url, String message) {
        switch (url) {
            case API_GET_WALLET:
                mpWalletBalanceData.postValue(responseBody);
                break;
            case API_GET_ACCOUNTS:
                bankAccountList.postValue(responseBody);
                break;
            case API_WITHDRAW_AMOUNT:
            case API_ADD_BANK:
                activity.toastMessage(message);
                activity.finish();
                break;
            case API_GET_HISTORY:
                bankAccountList.postValue(responseBody);
                break;
        }
        activity.isClickableView = false;
        mutableProgress.postValue(false);
    }

    @Override
    public void successTxnResponse(List<WalletData> responseBody, String url, String message) {
        if (url.equals(API_GET_WALLET_TXN)) {
            mpWalletData.postValue(responseBody);
        }
        activity.isClickableView = false;
        mutableProgress.postValue(false);
    }

    @Override
    public void failureResponse(Throwable throwable, String url, String message) {
        activity.isClickableView = false;
        activity.toastMessage("Error: " + message);
        mutableProgress.postValue(false);
    }

    /*public void getCampaignById(int id) {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
        mutableProgress.postValue(true);
        String url = API_CREATE_CAMP + "/" + id;
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.getCampaignById(this, activity, url);
    }*/

    public void getWalletBalance() {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
        mutableProgress.postValue(true);
        APIRequest apiRequest = new APIRequest();
        apiRequest.getWalletBalance(this, activity, API_GET_WALLET);
    }

    public void getWalletTransaction() {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
        mutableProgress.postValue(true);
        APIRequest apiRequest = new APIRequest();
        apiRequest.getWalletTxn(this, activity, API_GET_WALLET_TXN);

    }

    public void getAccounts() {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
//        mutableProgress.postValue(true);
        APIRequest apiRequest = new APIRequest();
        apiRequest.getAccounts(this, activity, API_GET_ACCOUNTS);
    }

    public void withdrawMoney(int id, double amount) {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
        mutableProgress.postValue(true);

        WithdrawAmount withdrawAmount = new WithdrawAmount();
        withdrawAmount.setAmount(amount);
        withdrawAmount.setBank_account_id(id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.withdrawMoney(this, activity, API_WITHDRAW_AMOUNT, withdrawAmount);
    }

    public void addCard(String bankName, String benName, String account, String iban) {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
        mutableProgress.postValue(true);

        AddCard addCard = new AddCard();
        addCard.setBank_name(bankName);
        addCard.setBeneficiary_name(benName);
        addCard.setAccount_number(account);
        addCard.setIban(iban);

        APIRequest apiRequest = new APIRequest();
        apiRequest.addCard(this, activity, API_ADD_BANK, addCard);
    }

    public void updateCard(String bankName, String benName, String account, String iban, int id) {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
        mutableProgress.postValue(true);

        UpdateCard addCard = new UpdateCard();
        addCard.setBank_name(bankName);
        addCard.setBeneficiary_name(benName);
        addCard.setAccount_number(account);
        addCard.setIban(iban);
        addCard.setId(id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.updateBank(this, activity, API_UPDATE_BANK, addCard);
    }

    public void deleteBank(int id) {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
        mutableProgress.postValue(true);

        CommonRequest.DeleteBank addPayment = new CommonRequest.DeleteBank();
        addPayment.setId(id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.deleteCard(this, activity, API_DELETE_BANK_NEW, addPayment);
    }

    public void getHistory() {
        if (!activity.isNetworkConnected()) return;
        activity.isClickableView = true;
//        mutableProgress.postValue(true);
        APIRequest apiRequest = new APIRequest();
        apiRequest.getHistory(this, activity, API_GET_HISTORY);
    }
}

