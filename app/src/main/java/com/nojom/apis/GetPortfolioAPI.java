package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.Portfolios;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_GET_PORTFOLIO;

public class GetPortfolioAPI implements APIRequest.APIRequestListener {

    private BaseActivity activity;
    private List<Portfolios> attachmentList;
    private MutableLiveData<List<Portfolios>> listMutableLiveData;
    private MutableLiveData<Boolean> isProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsProgress() {
        return isProgress;
    }

    public MutableLiveData<List<Portfolios>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
        attachmentList = new ArrayList<>();
    }

    public void getMyPortfolios() {
        if (!activity.isNetworkConnected())
            return;

        getIsProgress().postValue(true);
        attachmentList = new ArrayList<>();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_PORTFOLIO, null, false, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        List<Portfolios> portfolios = Portfolios.getPortfolios(decryptedData);
        if (portfolios != null && portfolios.size() > 0) {
            attachmentList.addAll(portfolios);
        }
        getListMutableLiveData().postValue(attachmentList);
        getIsProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsProgress().postValue(false);
        if (attachmentList != null) {
            getListMutableLiveData().postValue(attachmentList);
        }
    }
}
