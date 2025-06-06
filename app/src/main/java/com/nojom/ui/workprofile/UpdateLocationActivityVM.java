package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.CityResponse;
import com.nojom.model.ProfileResponse;
import com.nojom.model.StateResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.List;

public class UpdateLocationActivityVM extends ViewModel implements Constants, APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    private final MutableLiveData<ProfileResponse.Addresses> responseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<StateResponse.StateData>> stateLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CityResponse.CityData>> cityLiveData = new MutableLiveData<>();


    private final MutableLiveData<Boolean> isShowStateProgress = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isShowCityProgress = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isShowSaveProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowSaveProgress() {
        return isShowSaveProgress;
    }

    public MutableLiveData<List<CityResponse.CityData>> getCityLiveData() {
        return cityLiveData;
    }

    public MutableLiveData<List<StateResponse.StateData>> getStateLiveData() {
        return stateLiveData;
    }

    public MutableLiveData<Boolean> getIsShowStateProgress() {
        return isShowStateProgress;
    }

    public MutableLiveData<Boolean> getIsShowCityProgress() {
        return isShowCityProgress;
    }

    public MutableLiveData<ProfileResponse.Addresses> getResponseMutableLiveData() {
        return responseMutableLiveData;
    }

    void updateLocation(Integer country, Integer city, int steps) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowSaveProgress().postValue(true);

        CommonRequest.AddAddress addAddress = new CommonRequest.AddAddress();
        if (city != null) {
            addAddress.setCityID("" + city);
        } else {
            addAddress.setCityID("0");
        }
        if (country != null) {
            addAddress.setCountryID("" + country);
        } else {
            addAddress.setCountryID("0");
        }
        addAddress.setStateID("0");
//        if (steps != -1) {
//            addAddress.setRegistration_step(steps);
//        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_ADDRESS, addAddress.toString(), true, this);

    }

    public void init(BaseActivity updateLocationActivity) {
        this.activity = updateLocationActivity;
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_STATE)) {
            getIsShowStateProgress().postValue(false);
            getStateLiveData().postValue(StateResponse.getStateData(decryptedData));
        } else if (urlEndPoint.equalsIgnoreCase(API_GET_CITIES)) {
            getIsShowCityProgress().postValue(false);
            getCityLiveData().postValue(CityResponse.getCityData(decryptedData));
        } else if (urlEndPoint.equalsIgnoreCase(API_ADD_ADDRESS)) {
            getResponseMutableLiveData().postValue(ProfileResponse.getAddress(decryptedData));
        }
        getIsShowSaveProgress().postValue(false);
        activity.isClickableView = false;
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_STATE)) {
            getIsShowStateProgress().postValue(false);
        } else if (urlEndPoint.equalsIgnoreCase(API_GET_CITIES)) {
            getIsShowCityProgress().postValue(false);
        } else {
            getIsShowSaveProgress().postValue(false);
        }
        activity.isClickableView = false;
        activity.disableEnableTouch(false);
    }

    public void getStateFromCountry(int countryId) {
        if (!activity.isNetworkConnected())
            return;
        getIsShowStateProgress().postValue(true);

        CommonRequest.GetState state = new CommonRequest.GetState();
        state.setCountryID(countryId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_STATE, state.toString(), true, this);
    }

    public void getCityFromCountryState(int stateId) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowCityProgress().postValue(true);

        CommonRequest.GetCity state = new CommonRequest.GetCity();
        state.setStateID(stateId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_CITIES, state.toString(), true, this);
    }
}
