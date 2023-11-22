package com.nojom.ui.gigs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.Language;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;

import static com.nojom.util.Constants.API_GET_GIG_LANGUAGE;

public class GigLanguageActivityVM extends ViewModel implements APIRequest.JWTRequestResponseListener {
    private BaseActivity activity;

    private MutableLiveData<ArrayList<Language.Data>> gigLanguageList = new MutableLiveData<>();
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Language.Data>> getGigLanguageList() {
        return gigLanguageList;
    }

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }

    public void init(BaseActivity baseActivity) {
        this.activity = baseActivity;
        getGigLanguage();
    }

    public void getGigLanguage() {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(4);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_LANGUAGE, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_GIG_LANGUAGE)) {
            Language languageList = Language.getLanguages(responseBody);
            if (languageList != null && languageList.data != null && languageList.data.size() > 0) {
                getGigLanguageList().postValue(languageList.data);
                getIsHideProgress().postValue(4);
            }
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_GET_GIG_LANGUAGE)) {
            getIsHideProgress().postValue(4);
        }
    }
}
