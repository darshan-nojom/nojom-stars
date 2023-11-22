package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.CountryResponse;
import com.nojom.ui.BaseActivity;

import java.util.List;

import static com.nojom.util.Constants.API_GET_COUNTRIES;

public class GetCountriesAPI implements APIRequest.APIRequestListener {
    private BaseActivity activity;
    private MutableLiveData<List<CountryResponse.CountryData>> countryLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowCountryProgress = new MutableLiveData<>();


    public MutableLiveData<Boolean> getIsShowCountryProgress() {
        return isShowCountryProgress;
    }

    public MutableLiveData<List<CountryResponse.CountryData>> getCountryLiveData() {
        return countryLiveData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getCountries() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowCountryProgress().postValue(true);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_COUNTRIES, null, false, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_COUNTRIES)) {
            getIsShowCountryProgress().postValue(false);
            getCountryLiveData().postValue(CountryResponse.getCountryData(decryptedData));
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowCountryProgress().postValue(false);
    }
}
