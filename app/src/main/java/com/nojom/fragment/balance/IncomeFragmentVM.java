package com.nojom.fragment.balance;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.Balance;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.balance.BalanceActivity;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_GET_INCOME;

public class IncomeFragmentVM extends ViewModel implements APIRequest.APIRequestListener {
    private MutableLiveData<List<Balance.Income>> listMutableLiveData;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private BaseFragment fragment;
    private List<Balance.Income> incomeList;

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<List<Balance.Income>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public IncomeFragmentVM() {

    }

    public void init(BaseFragment fragment) {
        this.fragment = fragment;
    }


    void getIncome(int page) {
        if (!fragment.activity.isNetworkConnected()) {
            getIsShowProgress().postValue(false);
            return;
        }
        if (page == 1) {
            incomeList = new ArrayList<>();
            getIsShowProgress().postValue(true);
        }

        CommonRequest.AgentReview agentReview = new CommonRequest.AgentReview();
        agentReview.setPage_no(page);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment.activity, API_GET_INCOME, agentReview.toString(), true, this);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        Balance balance = Balance.getBalance(decryptedData);
        if (balance != null && balance.income != null) {

            incomeList.addAll(balance.income);

            ((BalanceActivity) fragment.activity).setBalance(balance.availableBalance, balance.pendingBalance, balance.totalBalance);

        } else {
            ((BalanceActivity) fragment.activity).setBalance(0, 0, 0);
        }
        getListMutableLiveData().postValue(incomeList);

        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
        ((BalanceActivity) fragment.activity).setBalance(0, 0, 0);
    }
}
